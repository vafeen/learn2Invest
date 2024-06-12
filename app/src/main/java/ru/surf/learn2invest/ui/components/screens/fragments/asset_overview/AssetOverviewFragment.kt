package ru.surf.learn2invest.ui.components.screens.fragments.asset_overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.surf.learn2invest.chart.LineChartHelper
import ru.surf.learn2invest.databinding.FragmentAssetOverviewBinding
import ru.surf.learn2invest.network_components.CoinAPIService
import ru.surf.learn2invest.network_components.util.CoinRetrofitClient

// Вкладка Обзор в Обзоре актива
class AssetOverviewFragment : Fragment() {
    private lateinit var binding: FragmentAssetOverviewBinding
    private lateinit var chartHelper: LineChartHelper
    private lateinit var viewModel: AssetViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssetOverviewBinding.inflate(inflater, container, false)
        chartHelper = LineChartHelper(requireContext())

        val id = requireArguments().getString("id") ?: ""

        val coinAPIService = CoinRetrofitClient.client.create(CoinAPIService::class.java)
        val factory = AssetOverviewViewModelFactory(coinAPIService)
        viewModel = ViewModelProvider(this, factory)[AssetViewModel::class.java]

        chartHelper.setupChart(binding.chart)

        viewModel.loadChartData(id) { data, marketCap ->
            chartHelper.updateData(data)
            binding.capitalisation.text = marketCap
        }

        return binding.root
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