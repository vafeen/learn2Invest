package ru.surf.learn2invest.chart

import android.content.Context
import android.graphics.Color
import android.icu.util.Calendar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import ru.surf.learn2invest.R
import java.text.SimpleDateFormat
import java.util.Locale

class LineChartHelper(private val context: Context) {
    private lateinit var chart: LineChart
    companion object {
        private val timeFormatter = SimpleDateFormat("H:mm", Locale.getDefault())
    }

    fun setupChart(lineChart: LineChart) {
        this.chart = lineChart
        lineChart.apply {

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
                textSize = 10f
                setDrawGridLines(false)
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = TimeValueFormatter()
                typeface = ResourcesCompat.getFont(context,R.font.montserrat_medium)
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
    }

    fun updateData(data: List<Entry>){
        val lineDataSet = LineDataSet(data, "List")
        val lineData = LineData(lineDataSet)
        styleLineDataSet(lineDataSet)
        chart.data = lineData
        chart.invalidate()
    }

    private fun styleLineDataSet(lineDataSet: LineDataSet) = lineDataSet.apply {
        color = ContextCompat.getColor(context,R.color.graphic)
        valueTextColor = Color.BLACK
        lineWidth = 2f
        isHighlightEnabled = true
        valueTextSize = 15f
        setDrawValues(false)
        setDrawCircles(false)
        setDrawHighlightIndicators(false)
        mode = LineDataSet.Mode.CUBIC_BEZIER

        setDrawFilled(true)
        fillDrawable = ContextCompat.getDrawable(context,R.drawable.line_chart_style)
    }

    private class TimeValueFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, value.toInt())
            calendar.set(Calendar.MINUTE,0)
            return timeFormatter.format(calendar.time)
        }
    }
}