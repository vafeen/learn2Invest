package ru.surf.learn2invest.ui.components.screens.fragments.asset_overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import ru.surf.learn2invest.databinding.FragmentAssetOverviewBinding
import ru.surf.learn2invest.ui.components.chart.Last7DaysFormatter
import ru.surf.learn2invest.ui.components.chart.LineChartHelper
import ru.surf.learn2invest.utils.AssetConstants
import ru.surf.learn2invest.utils.viewModelCreator
import javax.inject.Inject

/**
 * Фрагмент обзора актива в [AssetReviewActivity][ru.surf.learn2invest.ui.components.screens.fragments.asset_review.AssetReviewActivity]
 */
@AndroidEntryPoint
class AssetOverviewFragment : Fragment() {
    private lateinit var binding: FragmentAssetOverviewBinding

    @Inject
    lateinit var factory: AssetOverViewFragmentViewModel.Factory

    private val viewModel: AssetOverViewFragmentViewModel by viewModelCreator {
        factory.createAssetOverViewFragmentViewModel(
            id = requireArguments().getString(AssetConstants.ID.key) ?: ""
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssetOverviewBinding.inflate(inflater, container, false)
        val dateFormatterStrategy = Last7DaysFormatter()
        viewModel.chartHelper = LineChartHelper(requireContext(), dateFormatterStrategy)
        viewModel.chartHelper.setupChart(binding.chart)

        viewModel.loadChartData(viewModel.id) { data, marketCap, price ->
            viewModel.chartHelper.updateData(data)
            binding.capitalisation.text = marketCap
            binding.price.text = price
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.realTimeUpdateJob =
            viewModel.startRealTimeUpdate(viewModel.id) { data, marketCap, price ->
                viewModel.chartHelper.updateData(data)
                binding.capitalisation.text = marketCap
                binding.price.text = price
            }
    }

    override fun onStop() {
        super.onStop()
        viewModel.realTimeUpdateJob.cancel()
    }

    companion object {
        fun newInstance(id: String): AssetOverviewFragment {
            val fragment = AssetOverviewFragment()
            val args = Bundle()
            args.putString(AssetConstants.ID.key, id)
            fragment.arguments = args
            return fragment
        }
    }
}