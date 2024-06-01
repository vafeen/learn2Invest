package ru.surf.learn2invest.ui.components.screens.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import ru.surf.learn2invest.chart.LineChartStyle
import ru.surf.learn2invest.databinding.FragmentPortfolioBinding

class PortfolioFragment : Fragment() {
    private lateinit var binding: FragmentPortfolioBinding
    private lateinit var chartStyle: LineChartStyle
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPortfolioBinding.inflate(inflater, container, false)
        chartStyle = LineChartStyle(requireContext())

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
        list.add(Entry(10f, 10f))
        list.add(Entry(11f, 1f))
        list.add(Entry(12f, 3f))
        list.add(Entry(13f, 5f))
        list.add(Entry(14f, 2f))
        list.add(Entry(15f, 8f))

        val lineDataSet = LineDataSet(list, "List")

        chartStyle.styleChart(binding.chart)
        chartStyle.styleLineDataSet(lineDataSet)
        val lineData = LineData(lineDataSet)
        binding.chart.data = lineData

        return binding.root
    }
}