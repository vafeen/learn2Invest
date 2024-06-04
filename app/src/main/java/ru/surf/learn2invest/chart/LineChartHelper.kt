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
        private val dateFormatter = SimpleDateFormat("dd MMM", Locale.getDefault())
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
                axisMinimum = -0.3f
                axisMaximum = 6.3f
                isGranularityEnabled = true
                granularity = 1f
                textSize = 10f
                setDrawGridLines(false)
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = DateValueFormatter()
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

        val minY = data.minByOrNull { it.y }?.y ?: 0f
        val maxY = data.maxByOrNull { it.y }?.y ?: 0f

        chart.apply {
            axisLeft.axisMinimum = minY - (0.1f * (maxY - minY)) // Небольшой отступ снизу
            axisLeft.axisMaximum = maxY + (0.1f * (maxY - minY)) // Небольшой отступ сверху
            this.data = lineData
            invalidate()
        }
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

    private class DateValueFormatter:ValueFormatter(){
        override fun getFormattedValue(value: Float): String {
            val calendar= Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -(6-value.toInt()))
            }
            return dateFormatter.format(calendar.time)
        }
    }
}