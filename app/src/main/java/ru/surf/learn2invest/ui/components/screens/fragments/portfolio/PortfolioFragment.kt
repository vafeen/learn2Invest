package ru.surf.learn2invest.ui.components.screens.fragments.portfolio

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.chart.LineChartHelper
import ru.surf.learn2invest.databinding.FragmentPortfolioBinding
import ru.surf.learn2invest.noui.database_components.entity.AssetInvest
import ru.surf.learn2invest.ui.components.screens.fragments.asset_review.AssetReviewActivity

// Экран портфеля
class PortfolioFragment : Fragment() {
    private lateinit var binding: FragmentPortfolioBinding
    private lateinit var chartHelper: LineChartHelper
    private lateinit var viewModel: PortfolioViewModel
    private val adapter = PortfolioAdapter { asset ->
        startAssetReviewIntent(asset)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPortfolioBinding.inflate(inflater, container, false)
        chartHelper = LineChartHelper(requireContext())

        setupAssetsRecyclerView()

        val factory = PortfolioViewModelFactory(
            App.mainDB.assetBalanceHistoryDao(),
            App.mainDB.assetInvestDao()
        )
        viewModel = ViewModelProvider(this, factory)[PortfolioViewModel::class.java]

        viewModel.assetBalance.observe(viewLifecycleOwner) { balance ->
            binding.balanceText.text = "${balance}$"
        }

        viewModel.fiatBalance.observe(viewLifecycleOwner) { balance ->
            binding.accountFunds.text = "${balance}$"
        }
        viewModel.loadBalanceData()

        chartHelper.setupChart(binding.chart)
        viewModel.chartData.observe(viewLifecycleOwner) { data ->
            chartHelper.updateData(data)
        }
        viewModel.loadChartData()

        viewModel.loadAssetsData()
        adapter.assets = viewModel.assets
        adapter.notifyDataSetChanged()

        return binding.root
    }

    private fun startAssetReviewIntent(asset: AssetInvest) {
        val intent = Intent(requireContext(), AssetReviewActivity::class.java)
        val bundle = Bundle()
        bundle.putString("id", asset.id.toString())
        bundle.putString("name", asset.name)
        bundle.putString("symbol", asset.symbol)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun setupAssetsRecyclerView() {
        binding.assets.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.assets.adapter = adapter
    }
}