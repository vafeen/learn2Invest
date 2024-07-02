package ru.surf.learn2invest.ui.components.chart

import com.github.mikephil.charting.charts.LineChart

/**
 * Интерфейс для кастомных классов отображения даты на графике
 */
interface CustomDateValueFormatter {
    fun getFormattedValue(value: Float, chart: LineChart): String
}