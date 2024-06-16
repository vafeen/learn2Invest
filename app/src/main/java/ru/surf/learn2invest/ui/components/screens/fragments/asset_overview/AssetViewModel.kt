package ru.surf.learn2invest.ui.components.screens.fragments.asset_overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.surf.learn2invest.network_components.NetworkRepository
import ru.surf.learn2invest.network_components.ResponseWrapper
import ru.surf.learn2invest.noui.logs.Loher
import java.text.NumberFormat
import java.util.Locale

class AssetViewModel(
) : ViewModel() {
    private var marketCap = 0.0
    private var price = 0.0
    private var data = mutableListOf<Entry>()
    private lateinit var formattedMarketCap: String
    private lateinit var formattedPrice: String
    fun loadChartData(id: String, onDataLoaded: (List<Entry>, String, String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = NetworkRepository.getCoinHistory(id)) {
                is ResponseWrapper.Success -> {
                    data = response.value.data.mapIndexed { index, coinPriceResponse ->
                        Entry(index.toFloat(), coinPriceResponse.priceUsd)
                    }.toMutableList()
                    when (val coinResponse = NetworkRepository.getCoinReview(id)) {
                        is ResponseWrapper.Success -> {
                            data.add(Entry(data.size.toFloat(), coinResponse.value.data.priceUsd))
                            marketCap = coinResponse.value.data.marketCapUsd.toDouble()
                            formattedMarketCap = NumberFormat.getInstance(Locale.US).apply {
                                maximumFractionDigits = 0
                            }.format(marketCap) + " $"
                            price = coinResponse.value.data.priceUsd.toDouble()
                            formattedPrice = NumberFormat.getInstance(Locale.US).apply {
                                maximumFractionDigits = 2
                            }.format(price) + " $"
                            withContext(Dispatchers.Main) {
                                onDataLoaded(data, formattedMarketCap, formattedPrice)
                            }
                        }

                        is ResponseWrapper.NetworkError -> {}
                    }
                }

                is ResponseWrapper.NetworkError -> {}
            }
        }
    }

    fun startRealTimeUpdate(id: String, onDataLoaded: (List<Entry>, String, String) -> Unit): Job =
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                delay(5000)
                when (val result = NetworkRepository.getCoinReview(id)) {
                    is ResponseWrapper.Success -> {
                        if (data.size != 0) {
                            Loher.d("${result.value.data.priceUsd}")
                            data.removeLast()
                            data.add(Entry(data.size.toFloat(), result.value.data.priceUsd))
                            marketCap = result.value.data.marketCapUsd.toDouble()
                            formattedMarketCap = NumberFormat.getInstance(Locale.US).apply {
                                maximumFractionDigits = 0
                            }.format(marketCap) + " $"
                            price = result.value.data.priceUsd.toDouble()
                            formattedPrice = NumberFormat.getInstance(Locale.US).apply {
                                maximumFractionDigits = 2
                            }.format(price) + " $"
                            Loher.d("$data")
                            withContext(Dispatchers.Main) {
                                onDataLoaded(data, formattedMarketCap, formattedPrice)
                            }
                        }
                    }
                    is ResponseWrapper.NetworkError -> {}
                }
            }
        }

}