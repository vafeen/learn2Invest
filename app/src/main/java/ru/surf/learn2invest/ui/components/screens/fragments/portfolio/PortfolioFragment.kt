package ru.surf.learn2invest.ui.components.screens.fragments.portfolio

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.surf.learn2invest.R
import ru.surf.learn2invest.databinding.FragmentPortfolioBinding
import ru.surf.learn2invest.ui.components.alert_dialogs.refill_account_dialog.RefillAccountDialog
import ru.surf.learn2invest.ui.components.chart.AssetBalanceHistoryFormatter
import ru.surf.learn2invest.ui.components.chart.LineChartHelper
import ru.surf.learn2invest.utils.DevStrLink
import ru.surf.learn2invest.utils.getWithCurrency
import ru.surf.learn2invest.utils.setStatusBarColor
import java.util.Locale
import javax.inject.Inject

/**
 * Фрагмент портфеля в [HostActivity][ru.surf.learn2invest.ui.components.screens.host.HostActivity]
 */

@AndroidEntryPoint
class PortfolioFragment : Fragment() {
    private lateinit var binding: FragmentPortfolioBinding
    private lateinit var chartHelper: LineChartHelper
    private lateinit var realTimeUpdateJob: Job
    private val viewModel: PortfolioFragmentViewModel by viewModels()

    @Inject
    lateinit var adapter: PortfolioAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        activity?.apply {
            setStatusBarColor(
                window,
                this,
                R.color.accent_background,
                R.color.accent_background_dark
            )
        }

        binding = FragmentPortfolioBinding.inflate(inflater, container, false)

        setupAssetsRecyclerView()
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewModel.totalBalance.collect { balance ->
                binding.balanceText.text = balance.getWithCurrency()
                val isBalanceNonZero = balance != 0f
                binding.chart.isVisible = isBalanceNonZero
                binding.percent.isVisible = isBalanceNonZero
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewModel.fiatBalance.collect { balance ->
                binding.accountFunds.text = balance.getWithCurrency()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            val dates = viewModel.getAssetBalanceHistoryDates()
            val dateFormatterStrategy = AssetBalanceHistoryFormatter(dates)
            chartHelper = LineChartHelper(requireContext(), dateFormatterStrategy)
            chartHelper.setupChart(binding.chart)
            viewModel.chartData.collect { data ->
                chartHelper.updateData(data)
            }
        }

        binding.topUpBtn.setOnClickListener {
            RefillAccountDialog(dialogContext = requireContext()) {
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                    viewModel.refreshData()
                }
            }.also {
                it.show(parentFragmentManager, it.dialogTag)
            }

        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewModel.assetsFlow.collect { assets ->
                adapter.assets = assets
                binding.assets.isVisible = assets.isNotEmpty()
                binding.assetsAreEmpty.isVisible = assets.isEmpty()
                adapter.notifyDataSetChanged()

            }
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewModel.priceChanges.collect { priceChanges ->
                adapter.priceChanges = priceChanges
                adapter.notifyItemRangeChanged(
                    (binding.assets.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition(),
                    (binding.assets.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewModel.portfolioChangePercentage.collect { percentage ->
                binding.percent.apply {
                    text = if (percentage == 0f) "%.2f%%".format(Locale.getDefault(), percentage)
                    else "%+.2f%%".format(Locale.getDefault(), percentage)

                    background = when {
                        percentage > 0 -> AppCompatResources.getDrawable(
                            requireContext(), R.drawable.percent_increase_background
                        )

                        percentage < 0 -> AppCompatResources.getDrawable(
                            requireContext(), R.drawable.percent_recession_background
                        )

                        else -> AppCompatResources.getDrawable(
                            requireContext(), R.drawable.percent_zero_background
                        )
                    }
                }
            }
        }

        initDrawerListeners()

        binding.imageButton.setOnClickListener {
            openDrawer()
        }

        binding.drawerLayout.setOnTouchListener { _, _ ->
            closeDrawer()
            false
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        realTimeUpdateJob = startRealtimeUpdate()
    }

    override fun onStop() {
        super.onStop()
        realTimeUpdateJob.cancel()
        closeDrawer()
    }

    private fun startRealtimeUpdate() = viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
        while (true) {
            viewModel.refreshData()
            delay(5000)
        }
    }

    private fun setupAssetsRecyclerView() {
        binding.assets.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.assets.adapter = adapter
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

    private fun initDrawerListeners() {
        binding.apply {

            contactUs.setOnClickListener {
                // написать нам
                startActivity(Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto: ${DevStrLink.CHERY}")
                })
            }

            code.setOnClickListener {
                // исходный код
                openLink(DevStrLink.CODE)
            }

            figma.setOnClickListener {
                // фигма
                openLink(DevStrLink.FIGMA)
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