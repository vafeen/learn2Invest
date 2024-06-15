package ru.surf.learn2invest.ui.components.screens.fragments.portfolio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
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

		return binding.root
	}
}