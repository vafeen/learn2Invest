package ru.surf.learn2invest.ui.components.screens.fragments.portfolio

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.chart.LineChartHelper
import ru.surf.learn2invest.databinding.FragmentPortfolioBinding
import ru.surf.learn2invest.noui.database_components.entity.AssetInvest
import ru.surf.learn2invest.ui.alert_dialogs.RefillAccount
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

        viewModel = ViewModelProvider(this)[PortfolioViewModel::class.java]

        viewModel.loadBalanceData()
        viewModel.assetBalance.observe(viewLifecycleOwner) { balance ->
            binding.balanceText.text = "${balance}$"
        }

        viewModel.fiatBalance.observe(viewLifecycleOwner) { balance ->
            binding.accountFunds.text = "${balance}$"
        }

        chartHelper.setupChart(binding.chart)
        viewModel.chartData.observe(viewLifecycleOwner) { data ->
            chartHelper.updateData(data)
        }
        viewModel.loadChartData()

        binding.topUpBtn.setOnClickListener {
            RefillAccount(requireContext(), lifecycleScope).initDialog().show()
        }

        viewModel.loadAssetsData()
        adapter.assets = viewModel.assets
        adapter.notifyDataSetChanged()

        viewModel.priceChanges.observe(viewLifecycleOwner) { priceChanges ->
            adapter.priceChanges = priceChanges
            adapter.notifyDataSetChanged()
        }

        return binding.root
    }

    private fun startAssetReviewIntent(asset: AssetInvest) {
        val intent = Intent(requireContext(), AssetReviewActivity::class.java)
        val bundle = Bundle()
        bundle.putString("id", asset.assetID)
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