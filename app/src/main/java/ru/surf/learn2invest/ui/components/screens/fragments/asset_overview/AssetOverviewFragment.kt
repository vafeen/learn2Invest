package ru.surf.learn2invest.ui.components.screens.fragments.asset_overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Job
import ru.surf.learn2invest.chart.LineChartHelper
import ru.surf.learn2invest.databinding.FragmentAssetOverviewBinding

// Вкладка Обзор в Обзоре актива
class AssetOverviewFragment : Fragment() {
    private lateinit var binding: FragmentAssetOverviewBinding
    private lateinit var chartHelper: LineChartHelper
    private lateinit var viewModel: AssetViewModel
    private lateinit var id: String
    private lateinit var realTimeUpdateJob: Job
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssetOverviewBinding.inflate(inflater, container, false)
        chartHelper = LineChartHelper(requireContext())

        id = requireArguments().getString("id") ?: ""

        viewModel = AssetViewModel()
        chartHelper.setupChart(binding.chart)

        viewModel.loadChartData(id) { data, marketCap ->
            chartHelper.updateData(data)
            binding.capitalisation.text = marketCap
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        realTimeUpdateJob = viewModel.startRealTimeUpdate(id) { data ->
            chartHelper.updateData(data)
        }
    }

    override fun onStop() {
        super.onStop()
        realTimeUpdateJob.cancel()
    }

    companion object {
        fun newInstance(id: String): AssetOverviewFragment {
            val fragment = AssetOverviewFragment()
            val args = Bundle()
            args.putString("id", id)
            fragment.arguments = args
            return fragment
        }
    }
}