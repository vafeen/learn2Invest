package ru.surf.learn2invest.ui.components.screens.fragments.portfolio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry
import ru.surf.learn2invest.noui.database_components.dao.AssetBalanceHistoryDao

class PortfolioViewModel : ViewModel() {
    private val _chartData = MutableLiveData<List<Entry>>()
    val chartData: LiveData<List<Entry>> get() = _chartData

    fun loadChartData() {
        // Здесь загружаются данные из базы данных или сервера
        val data: List<Entry> = listOf(
            Entry(0f, 5f),
            Entry(1f, 4f),
            Entry(2f, 7f),
            Entry(3f, 8f),
            Entry(4f, 10f),
            Entry(5f, 7f),
            Entry(6f, 3f)
        )
        _chartData.value = data
    }
}