package ru.surf.learn2invest.ui.components.chart

import android.icu.util.Calendar
import com.github.mikephil.charting.charts.LineChart
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Docs?
 */
class Last7DaysFormatter : CustomDateValueFormatter {
    private val dateFormatter = SimpleDateFormat("dd MMM", Locale.getDefault())

    override fun getFormattedValue(value: Float, chart: LineChart): String {
        val daysAgo = chart.data?.xMax?.toInt()?.let { maxIndex ->
            maxIndex - value.toInt()
        } ?: 0

        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -daysAgo)
        }

        return dateFormatter.format(calendar.time)
    }
}