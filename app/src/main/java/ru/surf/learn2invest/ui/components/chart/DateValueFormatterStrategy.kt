package ru.surf.learn2invest.ui.components.chart

import android.icu.util.Calendar
import com.github.mikephil.charting.charts.LineChart
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface DateValueFormatterStrategy {
    fun getFormattedValue(value: Float, chart: LineChart): String
}

class Last7DaysFormatter : DateValueFormatterStrategy {
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

class AssetBalanceHistoryFormatter(private val dates: List<Date>) : DateValueFormatterStrategy {
    private val dateFormatter = SimpleDateFormat("dd MMM", Locale.getDefault())

    override fun getFormattedValue(value: Float, chart: LineChart): String {
        val index = value.toInt()
        return if (index in dates.indices) {
            dateFormatter.format(dates[index])
        } else {
            ""
        }
    }
}