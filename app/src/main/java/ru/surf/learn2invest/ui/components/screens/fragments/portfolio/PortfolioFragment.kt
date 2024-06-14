package ru.surf.learn2invest.ui.components.screens.fragments.portfolio

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.surf.learn2invest.R
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.chart.LineChartHelper
import ru.surf.learn2invest.databinding.FragmentPortfolioBinding

// Экран портфеля
class PortfolioFragment : Fragment() {
    private lateinit var binding: FragmentPortfolioBinding
    private lateinit var chartHelper: LineChartHelper
    private lateinit var viewModel: PortfolioViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPortfolioBinding.inflate(inflater, container, false)
        chartHelper = LineChartHelper(requireContext())

        val factory = PortfolioViewModelFactory(App.mainDB.assetBalanceHistoryDao())
        viewModel = ViewModelProvider(this, factory)[PortfolioViewModel::class.java]

        chartHelper.setupChart(binding.chart)

        viewModel.chartData.observe(viewLifecycleOwner) { data ->
            chartHelper.updateData(data)
        }
        viewModel.loadChartData()

        initDrawer()

        binding.imageButton.setOnClickListener {
            activity?.apply {
                val drawer = binding.drawerLayout

                if (!drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.openDrawer(GravityCompat.START)
                }
            }

        }

        return binding.root
    }

    private fun initDrawer() {
        activity?.apply {
            val drawerLayout: DrawerLayout = binding.drawerLayout

            val toggle = ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )

            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()


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

            languageType.text = "RUSSIAN"

            language.setOnClickListener {

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