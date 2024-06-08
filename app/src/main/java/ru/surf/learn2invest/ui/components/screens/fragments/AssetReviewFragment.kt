package ru.surf.learn2invest.ui.components.screens.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.surf.learn2invest.R
import ru.surf.learn2invest.chart.LineChartHelper
import ru.surf.learn2invest.databinding.FragmentAssetReviewBinding
import ru.surf.learn2invest.noui.logs.Loher
import ru.surf.learn2invest.ui.components.screens.fragments.asset_overview.AssetOverviewFragment
import ru.surf.learn2invest.ui.components.screens.fragments.asset_overview.AssetOverviewViewModel

// Экран Обзор актива
class AssetReviewFragment : Fragment() {
    private lateinit var binding: FragmentAssetReviewBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssetReviewBinding.inflate(inflater, container, false)

        binding.assetReviewBtn.setOnClickListener {
            goToFragment(AssetOverviewFragment())
        }

        Loher.d(
            "Hello AssetReviewFragment ${
                requireArguments().getString("symbol")
            }"
        )
        return binding.root
    }

    private fun goToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }
}