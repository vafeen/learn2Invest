package ru.surf.learn2invest.ui.tests.screens

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import ru.surf.learn2invest.databinding.ActivityDialogsTestBinding
import ru.surf.learn2invest.ui.alert_dialogs.AskToDeleteProfile
import ru.surf.learn2invest.ui.alert_dialogs.Buy
import ru.surf.learn2invest.ui.alert_dialogs.NoAssetForSale
import ru.surf.learn2invest.ui.alert_dialogs.NotEnoughMoneyForBuy
import ru.surf.learn2invest.ui.alert_dialogs.PriceAlert
import ru.surf.learn2invest.ui.alert_dialogs.RefillAccount
import ru.surf.learn2invest.ui.alert_dialogs.Sell

class DialogsTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDialogsTestBinding
    private lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDialogsTestBinding.inflate(layoutInflater)

        context = this@DialogsTestActivity

        setContentView(binding.root)

        initListeners()

    }

    private fun initListeners() {
        binding.apply {
            askToDeleteProfileShowForTesting.setOnClickListener {

                AskToDeleteProfile(
                    context = this@DialogsTestActivity,
                    layoutInflater = layoutInflater
                ).initDialog()
                    .show()

            }

            buyShowForTesting.setOnClickListener {

                Buy(context = context, layoutInflater = layoutInflater).initDialog().show()

            }

            noAssetForSaleShowForTesting.setOnClickListener {

                NoAssetForSale(context = context, layoutInflater = layoutInflater).initDialog()
                    .show()

            }

            notEnoughMoneyForBuyShowForTesting.setOnClickListener {

                NotEnoughMoneyForBuy(
                    context = context,
                    layoutInflater = layoutInflater
                ).initDialog().show()

            }

            priceAlertShowForTesting.setOnClickListener {

                PriceAlert(
                    context = context,
                    layoutInflater = layoutInflater,
                    lifecycleScope = lifecycleScope
                ).initDialog().show()

            }

            refillAccountShowForTesting.setOnClickListener {

                RefillAccount(
                    context = context,
                    layoutInflater = layoutInflater,
                    lifecycleScope = lifecycleScope
                ).initDialog().show()

            }

            sellShowForTesting.setOnClickListener {
                Sell(
                    context = context,
                    layoutInflater = layoutInflater
                ).initDialog().show()
            }
        }

    }


}