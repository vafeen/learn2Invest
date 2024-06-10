package ru.surf.learn2invest.ui.components.screens.fragments.asset_overview

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.surf.learn2invest.network_components.CoinAPIService

class AssetViewModel(
    private val coinAPIService: CoinAPIService
) : ViewModel() {
    private var marketCap: Double = 0.0
    fun loadChartData(id: String, onDataLoaded: (List<Entry>, Double) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val calendar = Calendar.getInstance()
            val endTime = calendar.timeInMillis
            calendar.add(Calendar.DAY_OF_YEAR, -8)
            val startTime = calendar.timeInMillis

            val response = coinAPIService.getCoinHistory(id, "d1", startTime, endTime)
            val data = response.data.mapIndexed { index, coinPriceResponse ->
                Entry(index.toFloat(), coinPriceResponse.priceUsd)
            }

            val coinResponse = coinAPIService.getCoinReview(id)
            marketCap = coinResponse.data.marketCapUsd.toDouble()

            withContext(Dispatchers.Main) {
                onDataLoaded(data, marketCap)
            }
        }
    }
}