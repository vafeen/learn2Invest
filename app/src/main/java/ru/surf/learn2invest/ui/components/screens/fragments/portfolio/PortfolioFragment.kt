package ru.surf.learn2invest.ui.components.screens.fragments.portfolio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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
            val navView: NavigationView = binding.navView

            val toggle = ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )

            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            // Handle navigation item clicks
            navView.setNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.nav_home -> {
                        // Handle Home click
                        // Replace with your logic
                        false
                    }

                    R.id.nav_profile -> {
                        // Handle Profile click
                        // Replace with your logic
                        false
                    }
                    // Add more cases for other menu items
                    else -> true
                }
            }
        }
    }
}