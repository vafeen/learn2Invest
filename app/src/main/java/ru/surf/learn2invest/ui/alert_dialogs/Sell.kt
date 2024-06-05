package ru.surf.learn2invest.ui.alert_dialogs

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.databinding.SellDialogBinding
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

            balanceNumSellDialog.text =
                App.profile?.fiatBalance.toString() // TODO()Володь, Сюда также нужно
            //            поставить нужный тип баланса

            lifecycleScope.launch(Dispatchers.Main) {
                while (true) {
                    val str = "777777"
                    priceNumberSellDialog.text = str// TODO Сюда нужно будет кидать цену,
                    // которая приходит через ретрофит

                    updateFields()
                    delay(2000)
                }
            }

            buttonExitSellDialog.setOnClickListener {
                cancel()
            }

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
                    val newNumberOfLots = text.toString().toIntOrNull()?.let {
                        if (it > 0) {
                            it - 1
                        } else {
                            it
                        }
                    } ?: 0

                    "$newNumberOfLots"
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

        }
    }

    override fun getDialogView(): View {
        return binding.root
    }

    private fun updateFields() {
        binding.itogoSellDialog.text = "Итого: $ ${itog(onFuture = false)}"
    }

    private fun itog(
        onFuture: Boolean
    ): Float {
        binding.apply {
            val priceText = priceNumberSellDialog.text.toString()

            val price = priceText.substring(1, priceText.length).toFloatOrNull() ?: 0f

            val number = enteringNumberOfLotsSellDialog.text.toString().toIntOrNull() ?: 0

            return price * (number + if (onFuture) {
                1
            } else {
                0
            })
        }
    }

}