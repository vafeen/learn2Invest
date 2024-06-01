package ru.surf.learn2invest.chart

import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import ru.surf.learn2invest.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class LineChartStyle(private val context: Context) {
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        private val timeFormatter = DateTimeFormatter.ofPattern("H:mm")
    }

    fun styleChart(lineChart: LineChart) = lineChart.apply {
        axisRight.isEnabled = false

        axisLeft.apply {
            isEnabled = false
            axisMinimum = 0f
            textSize = 12f
            setDrawGridLines(false)
        }

        xAxis.apply {
            axisMinimum = 0f
            axisMaximum = 23f
            isGranularityEnabled = true
            granularity = 4f
            textSize = 12f
            setDrawGridLines(false)
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = TimeValueFormatter()
        }
        setTouchEnabled(true)
        isDragEnabled = true
        setScaleEnabled(false)
        setPinchZoom(false)
        setDrawMarkers(true)
        marker = CustomMarkerView(context, R.layout.chart_marker)
        description = null
        legend.isEnabled = false
    }

    fun styleLineDataSet(lineDataSet: LineDataSet) = lineDataSet.apply {
        color = context.resources.getColor(R.color.hint)
        valueTextColor = Color.BLACK
        lineWidth = 2f
        isHighlightEnabled = true
        valueTextSize = 15f
        setDrawValues(false)
        setDrawCircles(false)
        setDrawHighlightIndicators(false)
        mode = LineDataSet.Mode.CUBIC_BEZIER

        setDrawFilled(true)
        fillDrawable = context.resources.getDrawable(R.drawable.line_chart_style)
    }

    private class TimeValueFormatter : ValueFormatter() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun getFormattedValue(value: Float): String {
            val localTime = LocalTime.of(value.toInt(), 0)
            return timeFormatter.format(localTime)
        }
    }
}