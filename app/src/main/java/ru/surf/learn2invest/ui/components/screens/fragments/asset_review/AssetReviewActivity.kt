package ru.surf.learn2invest.ui.components.screens.fragments.asset_review

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.Disposable
import coil.request.ImageRequest
import ru.surf.learn2invest.R
import ru.surf.learn2invest.databinding.ActivityAssetReviewBinding
import ru.surf.learn2invest.network_components.util.Const
import ru.surf.learn2invest.ui.alert_dialogs.Buy
import ru.surf.learn2invest.ui.alert_dialogs.Sell
import ru.surf.learn2invest.ui.components.screens.fragments.asset_overview.AssetOverviewFragment
import ru.surf.learn2invest.ui.components.screens.fragments.subhistory.SubHistoryFragment

// Экран Обзор актива
class AssetReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAssetReviewBinding
    private lateinit var disposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAssetReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra("id")
        val name = intent.getStringExtra("name")
        val symbol = intent.getStringExtra("symbol")

        binding.goBack.setOnClickListener {
            finish()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, AssetOverviewFragment.newInstance(id ?: ""))
            .commit()

        binding.assetReviewBtn.setOnClickListener {
            goToFragment(AssetOverviewFragment.newInstance(id ?: ""))
        }

        binding.assetHistoryBtn.setOnClickListener {
            goToFragment(SubHistoryFragment.newInstance(symbol ?: ""))
        }

        binding.coinName.text = name
        binding.coinSymbol.text = symbol

        val imageLoader = ImageLoader.Builder(binding.coinIcon.context)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()
        val request = ImageRequest.Builder(binding.coinIcon.context)
            .data("${Const.API_ICON}${symbol?.lowercase()}.svg")
            .target(onSuccess = {
                binding.coinIcon.setImageDrawable(it)
            },
                onError = {
                    binding.coinIcon.setImageResource(R.drawable.coin_placeholder)
                },
                onStart = {
                    binding.coinIcon.setImageResource(R.drawable.placeholder)
                })
            .build()
        disposable = imageLoader.enqueue(request)

        binding.buyAssetBtn.setOnClickListener {
            Buy(this, lifecycleScope, id ?: "").initDialog().show()
        }

        binding.sellAssetBtn.setOnClickListener {
            Sell(this, lifecycleScope, id ?: "").initDialog().show()
        }
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }

    private fun goToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
            .commit()
    }
}