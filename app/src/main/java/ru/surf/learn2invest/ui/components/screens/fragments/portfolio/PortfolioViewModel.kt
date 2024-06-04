package ru.surf.learn2invest.ui.components.screens.fragments.portfolio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.launch
import ru.surf.learn2invest.noui.database_components.dao.AssetBalanceHistoryDao
import ru.surf.learn2invest.noui.database_components.entity.AssetBalanceHistory

class PortfolioViewModel(
    private val assetBalanceHistoryDao: AssetBalanceHistoryDao
) : ViewModel() {
    private val _chartData = MutableLiveData<List<Entry>>()
    val chartData: LiveData<List<Entry>> get() = _chartData

    val testData = listOf(
        AssetBalanceHistory(assetBalance = 5f),
        AssetBalanceHistory(assetBalance = 4f),
        AssetBalanceHistory(assetBalance = 7f),
        AssetBalanceHistory(assetBalance = 8f),
        AssetBalanceHistory(assetBalance = 10f),
        AssetBalanceHistory(assetBalance = 7f),
        AssetBalanceHistory(assetBalance = 3f)
    )

    fun loadChartData() {
        viewModelScope.launch {
            assetBalanceHistoryDao.getAllAsFlow().collect{
                balanceHistories ->
                val data = balanceHistories.mapIndexed { index, assetBalanceHistory ->
                    Entry(index.toFloat(), assetBalanceHistory.assetBalance)
                }
                _chartData.value = data
            }
        }
    }

    fun insertTestData() {
        viewModelScope.launch {
            assetBalanceHistoryDao.insertAll(*testData.toTypedArray())
        }
    }
}