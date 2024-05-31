package ru.surf.learn2invest.ui.components.screens.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import ru.surf.learn2invest.R
import ru.surf.learn2invest.chart.CustomMarkerView
import ru.surf.learn2invest.databinding.FragmentPortfolioBinding

class PortfolioFragment : Fragment() {
    private lateinit var binding: FragmentPortfolioBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPortfolioBinding.inflate(inflater, container, false)

        val list: ArrayList<Entry> = ArrayList()

        list.add(Entry(0f, 5f))
        list.add(Entry(1f, 4f))
        list.add(Entry(2f, 7f))
        list.add(Entry(3f, 8f))
        list.add(Entry(4f, 10f))
        list.add(Entry(5f, 7f))
        list.add(Entry(6f, 3f))
        list.add(Entry(7f, 6f))
        list.add(Entry(8f, 5f))
        list.add(Entry(9f, 8f))

        val lineDataSet = LineDataSet(list, "List")

        val customMarkerView = CustomMarkerView(requireContext(), R.layout.chart_marker)

        lineDataSet.apply {
            color = resources.getColor(R.color.hint)
            valueTextColor = Color.BLACK
            lineWidth = 2f
            isHighlightEnabled = true
            valueTextSize = 15f
            setDrawValues(false)
            setDrawCircles(false)
            setDrawHighlightIndicators(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER

            setDrawFilled(true)
            fillDrawable = resources.getDrawable(R.drawable.line_chart_style)
        }

        binding.chart.apply {
            axisRight.isEnabled = false

            axisLeft.apply {
                isEnabled = false
                axisMinimum = 0f
                textSize = 12f
                setDrawGridLines(false)
            }

            xAxis.apply {
                isGranularityEnabled = true
                granularity = 4f
                textSize = 12f
                setDrawGridLines(false)
                position = XAxis.XAxisPosition.BOTTOM
            }
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(false)
            setPinchZoom(false)
            setDrawMarkers(true)
            marker = customMarkerView
            description = null
            legend.isEnabled = false
        }

        val lineData = LineData(lineDataSet)
        binding.chart.data = lineData

        return binding.root
    }
}