package ru.surf.learn2invest.ui.components.screens.fragments.asset_review

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ru.surf.learn2invest.R
import ru.surf.learn2invest.databinding.ActivityAssetReviewBinding
import ru.surf.learn2invest.ui.components.screens.fragments.asset_overview.AssetOverviewFragment

// Экран Обзор актива
class AssetReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAssetReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAssetReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.assetReviewBtn.setOnClickListener {
            goToFragment(AssetOverviewFragment())
        }

        binding.coinName.text = intent.getStringExtra("name")
        binding.coinSymbol.text = intent.getStringExtra("symbol")
    }

    private fun goToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
            .commit()
    }
}