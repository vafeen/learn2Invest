package ru.surf.learn2invest.ui.components.screens.fragments.portfolio

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.surf.learn2invest.chart.LineChartHelper
import ru.surf.learn2invest.databinding.FragmentPortfolioBinding
import ru.surf.learn2invest.noui.database_components.DatabaseRepository


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

        val factory = PortfolioViewModelFactory(DatabaseRepository.assetBalanceHistoryDao)
        viewModel = ViewModelProvider(this, factory)[PortfolioViewModel::class.java]

        chartHelper.setupChart(binding.chart)

        viewModel.chartData.observe(viewLifecycleOwner) { data ->
            chartHelper.updateData(data)
        }
        viewModel.loadChartData()

        initDrawer()

        binding.imageButton.setOnClickListener {
            openDrawer()
        }

        return binding.root
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
            val drawerLayout: DrawerLayout = binding.drawerLayout

            drawerLayout.addDrawerListener(
                object : DrawerLayout.DrawerListener {
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

                }
            )

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