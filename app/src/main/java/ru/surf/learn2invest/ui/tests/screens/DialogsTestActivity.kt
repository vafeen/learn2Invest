package ru.surf.learn2invest.ui.tests.screens

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import ru.surf.learn2invest.databinding.ActivityDialogsTestBinding
import ru.surf.learn2invest.ui.alert_dialogs.AskToDeleteProfile
import ru.surf.learn2invest.ui.alert_dialogs.Buy
import ru.surf.learn2invest.ui.alert_dialogs.PriceAlertDialog
import ru.surf.learn2invest.ui.alert_dialogs.RefillAccount
import ru.surf.learn2invest.ui.alert_dialogs.Sell
import ru.surf.learn2invest.ui.tests.data.insertProfileInCoroutineScope

class DialogsTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDialogsTestBinding
    private lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDialogsTestBinding.inflate(layoutInflater)

        context = this@DialogsTestActivity

        setContentView(binding.root)

        initListeners()

        insertProfileInCoroutineScope(lifecycleScope)
    }

    override fun onResume() {
        super.onResume()
    }

    private fun initListeners() {
        binding.apply {
            askToDeleteProfileShowForTesting.setOnClickListener {

                AskToDeleteProfile(
                    context = this@DialogsTestActivity,
                    lifecycleScope = lifecycleScope
                ).initDialog()
                    .show()

            }

            buyShowForTesting.setOnClickListener {

                Buy(context = context, lifecycleScope = lifecycleScope).initDialog().show()

            }

            priceAlertShowForTesting.setOnClickListener {

                PriceAlertDialog(
                    context = context,
                    currentPrice = 100f,
                    lifecycleScope = lifecycleScope
                ).initDialog().show()

            }

            refillAccountShowForTesting.setOnClickListener {

                RefillAccount(context = context, lifecycleScope = lifecycleScope).initDialog()
                    .show()

            }

            sellShowForTesting.setOnClickListener {
                Sell(
                    context = context,
                    lifecycleScope = lifecycleScope
                ).initDialog().show()
            }
        }

    }


}