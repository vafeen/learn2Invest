package ru.surf.learn2invest.ui.components.chart

import com.github.mikephil.charting.charts.LineChart

interface CustomDateValueFormatter {
    fun getFormattedValue(value: Float, chart: LineChart): String
}