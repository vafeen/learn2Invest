package ru.surf.learn2invest.ui.components.screens.fragments.asset_review

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.Disposable
import coil.request.ImageRequest
import dagger.hilt.android.AndroidEntryPoint
import ru.surf.learn2invest.R
import ru.surf.learn2invest.databinding.ActivityAssetReviewBinding
import ru.surf.learn2invest.ui.components.alert_dialogs.buy_dialog.BuyDialog
import ru.surf.learn2invest.ui.components.alert_dialogs.sell_dialog.SellDialog
import ru.surf.learn2invest.ui.components.screens.fragments.asset_overview.AssetOverviewFragment
import ru.surf.learn2invest.ui.components.screens.fragments.portfolio.AssetConstants
import ru.surf.learn2invest.ui.components.screens.fragments.subhistory.SubHistoryFragment
import ru.surf.learn2invest.utils.RetrofitLinks.API_ICON

/**
 * Экран обзора актива
 */
@AndroidEntryPoint
class AssetReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAssetReviewBinding
    private lateinit var disposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
        )
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)

        binding = ActivityAssetReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra(AssetConstants.ID.key) ?: ""
        val name = intent.getStringExtra(AssetConstants.NAME.key) ?: ""
        val symbol = intent.getStringExtra(AssetConstants.SYMBOL.key) ?: ""

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
            .data("${API_ICON}${symbol.lowercase()}.svg")
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
            BuyDialog(
                dialogContext = this,
                id = id,
                name = name,
                symbol = symbol
            ).also {
                it.show(supportFragmentManager, it.tag)
            }
        }

        binding.sellAssetBtn.setOnClickListener {
            SellDialog(
                dialogContext = this,
                lifecycleScope = lifecycleScope,
                id = id,
                name = name,
                symbol = symbol
            ).also {
                it.show(supportFragmentManager, it.dialogTag)
            }
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