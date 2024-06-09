package ru.surf.learn2invest.ui.alert_dialogs

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.databinding.SellDialogBinding
import ru.surf.learn2invest.noui.logs.Loher
import ru.surf.learn2invest.ui.alert_dialogs.parent.CustomAlertDialog

class Sell(
    context: Context, val lifecycleScope: LifecycleCoroutineScope
) : CustomAlertDialog(context = context) {

    private var binding = SellDialogBinding.inflate(LayoutInflater.from(context))

    override fun setCancelable(): Boolean {
        return true
    }

    override fun initListeners() {


        binding.apply {

            lifecycleScope.launch {
                balanceNumSellDialog.text =
                    App.profile.fiatBalance.getWithCurrency() // TODO()Володь, Сюда также нужно
                //            поставить нужный тип баланса

                while (true) {
                    val str = 777777f
                    priceNumberSellDialog.text =
                        str.getWithCurrency()  // TODO Сюда нужно будет кидать цену,
                    // которая приходит через ретрофит

                    updateFields()
                    delay(2000)
                }
            }

            buttonExitSellDialog.setOnClickListener {
                cancel()
            }

            buttonSellSellDialog.isVisible = false

            buttonSellSellDialog.setOnClickListener {
                // TODO Логика продажи

                cancel()
            }

            imageButtonPlusSellDialog.setOnClickListener {

                enteringNumberOfLotsSellDialog.setText(enteringNumberOfLotsSellDialog.text.let { text ->

                    val newNumberOfLots = if (text.isNotEmpty()) {
                        text.toString().toIntOrNull()?.let {
                            if (enteringNumberOfLotsSellDialog.text.toString()
                                    .toFloat() < 10 // TODO()Сюда вместо 10-ти нужно закинуть количество имеющихся лотов бумаги этой бумаги
                            ) {
                                it + 1
                            } else {
                                it
                            }
                        } ?: 0
                    } else {
                        1
                    }

                    "$newNumberOfLots"
                })
            }

            imageButtonMinusSellDialog.setOnClickListener {

                enteringNumberOfLotsSellDialog.setText(enteringNumberOfLotsSellDialog.text.let { text ->
                    text.toString().toIntOrNull()?.let {
                        when {
                            it == 1 || it == 0 -> {
                                ""
                            }

                            it > 1 -> {
                                (it - 1).toString()
                            }

                            else -> {
                                it.toString()
                            }
                        }
                    }
                })
            }

            enteringNumberOfLotsSellDialog.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    updateFields()
                }
            })

            tradingPassword.isVisible = if (App.profile.tradingPasswordHash != null) {

                tradingPasswordTV.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?, start: Int, count: Int, after: Int
                    ) {

                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {

                    }

                    override fun afterTextChanged(s: Editable?) {
                        buttonSellSellDialog.isVisible = s?.isTrueTradingPassword() ?: false
                    }
                })

                true

            } else {

                false

            }
            Loher.e("вернулось ${tradingPassword.isVisible}")
        }
    }

    override fun getDialogView(): View {
        return binding.root
    }

    private fun updateFields() {
        binding.itogoSellDialog.text = "Итого: $ ${resultPrice()}"
    }

    private fun resultPrice(): Float {
        binding.apply {
            val priceText = priceNumberSellDialog.text.toString()

            val price = priceText.substring(2, priceText.length).getFloatFromStringWithCurrency()

            val number = enteringNumberOfLotsSellDialog.text.toString().toIntOrNull() ?: 0

            Log.e("error", "number = $number")

            return price * number
        }
    }

}