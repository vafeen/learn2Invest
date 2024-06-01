package ru.surf.learn2invest.ui.components.screens.fragments.portfolio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry

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
            Entry(6f, 3f),
            Entry(7f, 6f),
            Entry(8f, 5f),
            Entry(9f, 8f),
            Entry(10f, 10f),
            Entry(11f, 1f),
            Entry(12f, 3f),
            Entry(13f, 5f),
            Entry(14f, 2f),
            Entry(15f, 8f),
            Entry(16f, 0f),
            Entry(17f, 2f),
            Entry(18f, 5f),
            Entry(19f, 10f),
            Entry(20f, 9f),
            Entry(21f, 10f),
            Entry(22f, 4f),
            Entry(23f, 2f),
        )
        _chartData.value = data
    }
}