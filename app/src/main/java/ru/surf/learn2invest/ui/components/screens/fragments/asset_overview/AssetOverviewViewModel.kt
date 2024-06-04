package ru.surf.learn2invest.ui.components.screens.fragments.asset_overview

import android.icu.util.Calendar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.launch
import ru.surf.learn2invest.network_components.CoinAPIService

class AssetOverviewViewModel(
    private val coinAPIService: CoinAPIService
) : ViewModel() {
    private val _chartData = MutableLiveData<List<Entry>>()
    val chartData: LiveData<List<Entry>> get() = _chartData

    init {
        loadChartData()
    }

    fun loadChartData() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val endTime = calendar.timeInMillis
            calendar.add(Calendar.DAY_OF_YEAR, -8)
            val startTime = calendar.timeInMillis

            val response = coinAPIService.getCoinHistory("bitcoin", "d1", startTime, endTime)
            val data = response.data.mapIndexed { index, coinPriceResponse ->
                Entry(index.toFloat(), coinPriceResponse.priceUsd.toFloat())
            }
            _chartData.value = data
        }
    }
}