package ru.surf.learn2invest.ui.components.screens.fragments.portfolio

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.R
import ru.surf.learn2invest.chart.LineChartHelper
import ru.surf.learn2invest.databinding.FragmentPortfolioBinding
import ru.surf.learn2invest.noui.database_components.entity.AssetInvest
import ru.surf.learn2invest.ui.alert_dialogs.RefillAccountDialog

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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        activity?.window?.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.main_background)

        viewModel = ViewModelProvider(this)[PortfolioViewModel::class.java]
        binding = FragmentPortfolioBinding.inflate(inflater, container, false)
        chartHelper = LineChartHelper(requireContext())

        setupAssetsRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewModel.totalBalance.collect { balance ->
                binding.balanceText.text = "${balance}$"
                val isBalanceNonZero = balance != 0f
                binding.chart.isVisible = isBalanceNonZero
                binding.percent.isVisible = isBalanceNonZero
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewModel.fiatBalance.collect { balance ->
                binding.accountFunds.text = "${balance}$"
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            viewModel.refreshData()
        }

        chartHelper.setupChart(binding.chart)
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewModel.chartData.collect { data ->
                chartHelper.updateData(data)
            }
        }

        binding.topUpBtn.setOnClickListener {
            RefillAccountDialog(requireContext(), lifecycleScope, parentFragmentManager) {
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.refreshData()
                }
            }.show()
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewModel.assetsFlow.collect { assets ->
                adapter.assets = assets
                adapter.notifyDataSetChanged()
                binding.assets.isVisible = assets.isNotEmpty()
                binding.assetsAreEmpty.isVisible = assets.isEmpty()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewModel.priceChanges.collect { priceChanges ->
                adapter.priceChanges = priceChanges
                adapter.notifyDataSetChanged()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewModel.portfolioChangePercentage.collect { percentage ->
                binding.percent.apply {
                    text = if (percentage == 0f) "%.2f%%".format(Locale.getDefault(), percentage)
                    else "%+.2f%%".format(Locale.getDefault(), percentage)

                    background = when {
                        percentage > 0 -> AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.percent_increase_background
                        )

                        percentage < 0 -> AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.percent_recession_background
                        )

                        else -> AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.percent_zero_background
                        )
                    }
                }
            }
        }

        initDrawer()

        binding.imageButton.setOnClickListener {
            openDrawer()
        }

        binding.drawerLayout.setOnTouchListener { _, _ ->
            closeDrawer()
            false
        }

        return binding.root
    }

    private fun startAssetReviewIntent(asset: AssetInvest) {
        startActivity(Intent(requireContext(), AssetReviewActivity::class.java).apply {
            putExtras(Bundle().apply {
                putString(AssetConstants.ID.key, asset.assetID)
                putString(AssetConstants.NAME.key, asset.name)
                putString(AssetConstants.SYMBOL.key, asset.symbol)
            })
        })
    }

    private fun setupAssetsRecyclerView() {
        binding.assets.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.assets.adapter = adapter
    }

    override fun onStop() {
        super.onStop()

        closeDrawer()
    }

    private fun openDrawer() {
        activity?.apply {
            val drawer = binding.drawerLayout

            if (!drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.openDrawer(GravityCompat.START)
            }
        }
    }

    private fun closeDrawer() {
        activity?.apply {
            val drawer = binding.drawerLayout

            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
            }
        }
    }

    private fun initDrawer() {
        activity?.apply {
            binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                    // Вызывается при перемещении Drawer
                    if (slideOffset > 0) {
                        binding.imageButton.isVisible = false
                    } else {
                        binding.imageButton.isVisible = true
                    }
                }

                override fun onDrawerOpened(drawerView: View) {
                    // Вызывается, когда Drawer открыт
                }

                override fun onDrawerClosed(drawerView: View) {
                    // Вызывается, когда Drawer закрыт
                }

                override fun onDrawerStateChanged(newState: Int) {
                    // Вызывается при изменении состояния Drawer
                }

            })

            initDrawerListeners()
        }
    }

    private fun initDrawerListeners() {
        binding.apply {

            contactUs.setOnClickListener {
                // написать нам
                startActivity(Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto: ${Link.chery}")
                })
            }

            code.setOnClickListener {
                // исходный код
                openLink(Link.code)
            }

            figma.setOnClickListener {
                // фигма
                openLink(Link.figma)
            }

            versionCode.text = getVersionName()
        }
    }


    private fun getVersionName(): String {
        val packageManager = requireContext().packageManager
        val packageName = requireContext().packageName
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val appVersion = packageInfo.versionName
        return appVersion

    }

    private fun openLink(link: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
    }

}