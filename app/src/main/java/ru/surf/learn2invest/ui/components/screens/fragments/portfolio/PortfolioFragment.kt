package ru.surf.learn2invest.ui.components.screens.fragments.portfolio

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import ru.surf.learn2invest.R
import ru.surf.learn2invest.chart.LineChartHelper
import ru.surf.learn2invest.databinding.FragmentPortfolioBinding
import ru.surf.learn2invest.noui.database_components.entity.AssetInvest
import ru.surf.learn2invest.ui.alert_dialogs.RefillAccount
import ru.surf.learn2invest.ui.components.screens.fragments.asset_review.AssetReviewActivity
import java.util.Locale

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

        lifecycleScope.launch {
            viewModel.assetBalance.collect { balance ->
                binding.balanceText.text = "${balance}$"
            }
        }

        lifecycleScope.launch {
            viewModel.fiatBalance.collect { balance ->
                binding.accountFunds.text = "${balance}$"
            }
        }

        chartHelper.setupChart(binding.chart)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.chartData.collect { data ->
                chartHelper.updateData(data)
            }
        }

        binding.topUpBtn.setOnClickListener {
            RefillAccount(requireContext(), lifecycleScope).initDialog().show()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.assetsFlow.collect { assets ->
                adapter.assets = assets
                adapter.notifyDataSetChanged()
                binding.assets.isVisible = assets.isNotEmpty()
                binding.chart.isVisible = assets.isNotEmpty()
                binding.assetsAreEmpty.isVisible = assets.isEmpty()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.priceChanges.collect { priceChanges ->
                adapter.priceChanges = priceChanges
                adapter.notifyDataSetChanged()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.portfolioChangePercentage.collect { percentage ->
                val formattedPercentage = String.format(Locale.getDefault(), "%.2f%%", percentage)
                binding.percent.text =
                    if (percentage >= 0) "+$formattedPercentage" else formattedPercentage

                val background = if (percentage >= 0) {
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.percent_increase_background
                    )
                } else {
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.percent_recession_background
                    )
                }
                binding.percent.background = background
            }
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