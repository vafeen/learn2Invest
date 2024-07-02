package ru.surf.learn2invest.ui.components.screens.fragments.asset_overview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.surf.learn2invest.noui.network_components.NetworkRepository
import ru.surf.learn2invest.noui.network_components.responses.ResponseWrapper
import ru.surf.learn2invest.ui.components.chart.LineChartHelper
import ru.surf.learn2invest.utils.getWithCurrency
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

class AssetOverViewFragmentViewModel @AssistedInject constructor(
    var networkRepository: NetworkRepository,
    @Assisted var id: String
) : ViewModel() {
    private var marketCap = 0.0
    private var data = mutableListOf<Entry>()
    private lateinit var formattedMarketCap: String
    private lateinit var formattedPrice: String
    lateinit var chartHelper: LineChartHelper
    lateinit var realTimeUpdateJob: Job
    fun loadChartData(id: String, onDataLoaded: (List<Entry>, String, String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = networkRepository.getCoinHistory(id)) {
                is ResponseWrapper.Success -> {
                    data = response.value.mapIndexed { index, coinPriceResponse ->
                        Entry(index.toFloat(), coinPriceResponse.priceUsd)
                    }.toMutableList()
                    when (val coinResponse = networkRepository.getCoinReview(id)) {
                        is ResponseWrapper.Success -> {
                            data.add(Entry(data.size.toFloat(), coinResponse.value.priceUsd))
                            marketCap = coinResponse.value.marketCapUsd.toDouble()
                            formattedMarketCap = NumberFormat.getInstance(Locale.US).apply {
                                maximumFractionDigits = 0
                            }.format(marketCap) + " $"
                            formattedPrice = String.format(Locale.US, "%.8f", coinResponse.value.priceUsd) + " $"
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
                when (val result = networkRepository.getCoinReview(id)) {
                    is ResponseWrapper.Success -> {
                        if (data.size != 0) {
                            data.removeLast()
                            data.add(Entry(data.size.toFloat(), result.value.priceUsd))
                            marketCap = result.value.marketCapUsd.toDouble()
                            formattedMarketCap = NumberFormat.getInstance(Locale.US).apply {
                                maximumFractionDigits = 0
                            }.format(marketCap) + " $"
                            formattedPrice = String.format(Locale.US, "%.8f", result.value.priceUsd) + " $"
                            withContext(Dispatchers.Main) {
                                onDataLoaded(data, formattedMarketCap, formattedPrice)
                            }
                        }
                    }

                    is ResponseWrapper.NetworkError -> {}
                }
            }
        }

    @AssistedFactory
    interface Factory {
        fun createAssetOverViewFragmentViewModel(id: String): AssetOverViewFragmentViewModel
    }
}