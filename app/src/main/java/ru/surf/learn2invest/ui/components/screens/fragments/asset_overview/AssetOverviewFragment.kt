package ru.surf.learn2invest.ui.components.screens.fragments.asset_overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.surf.learn2invest.chart.LineChartHelper
import ru.surf.learn2invest.databinding.FragmentAssetOverviewBinding

class AssetOverviewFragment : Fragment() {
    private lateinit var binding: FragmentAssetOverviewBinding
    private lateinit var chartHelper: LineChartHelper
    private lateinit var viewModel: AssetOverviewViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssetOverviewBinding.inflate(inflater, container, false)
        chartHelper = LineChartHelper(requireContext())
        viewModel = ViewModelProvider(this)[AssetOverviewViewModel::class.java]

        chartHelper.setupChart(binding.chart)

        viewModel.chartData.observe(viewLifecycleOwner) { data ->
            chartHelper.updateData(data)
        }
        viewModel.loadChartData()

        return binding.root
    }
}