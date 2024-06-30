package ru.surf.learn2invest.ui.components.screens.fragments.asset_overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import ru.surf.learn2invest.databinding.FragmentAssetOverviewBinding
import ru.surf.learn2invest.ui.components.chart.Last7DaysFormatter
import ru.surf.learn2invest.ui.components.chart.LineChartHelper

/**
 * Фрагмент обзора актива в [AssetReviewActivity][ru.surf.learn2invest.ui.components.screens.fragments.asset_review.AssetReviewActivity]
 */
@AndroidEntryPoint
class AssetOverviewFragment : Fragment() {
    private lateinit var binding: FragmentAssetOverviewBinding
    private lateinit var chartHelper: LineChartHelper
    private val viewModel: AssetOverViewFragmentViewModel by viewModels()
    private lateinit var id: String
    private lateinit var realTimeUpdateJob: Job

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssetOverviewBinding.inflate(inflater, container, false)
        val dateFormatterStrategy = Last7DaysFormatter()
        chartHelper = LineChartHelper(requireContext(), dateFormatterStrategy)

        id = requireArguments().getString("id") ?: ""

        chartHelper.setupChart(binding.chart)

        viewModel.loadChartData(id) { data, marketCap, price ->
            chartHelper.updateData(data)
            binding.capitalisation.text = marketCap
            binding.price.text = price
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        realTimeUpdateJob = viewModel.startRealTimeUpdate(id) { data, marketCap, price ->
            chartHelper.updateData(data)
            binding.capitalisation.text = marketCap
            binding.price.text = price
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