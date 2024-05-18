package ru.surf.learn2invest.ui.alert_dialogs

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import ru.surf.learn2invest.databinding.PriceAlertDialogBinding
import ru.surf.learn2invest.noui.logs.Loher
import ru.surf.learn2invest.ui.alert_dialogs.parent.CustomAlertDialog

class PriceAlert(
    context: Context,
    val currentPrice: Float,
) : CustomAlertDialog(context = context) {

    private var binding =
        PriceAlertDialogBinding.inflate(LayoutInflater.from(context))

    override fun setCancelable(): Boolean {
        return true
    }

    private fun changePriceByPercent(onChangedPercent: Editable?) {
        binding.pricePriceAlertDialog.setText(
            "$onChangedPercent".toDoubleOrNull().let {
                if (it != null) {
                    currentPrice * (1 + it / 100)
                }
            }.toString()
        )
    }


    private fun changePercentByPrice(newPrice: Editable?) {
        binding.priceInPercentPriceAlertDialog.setText(
            "$newPrice".toDoubleOrNull()?.times(currentPrice).toString()
        )
    }

    override fun initListeners() {
        binding.apply {
            buttonCreatePriceAlertPriceAlertDialog.setOnClickListener {
                cancel()
            }

            buttonExitPriceAlertDialog.setOnClickListener {
                cancel()
            }


            pricePriceAlertDialog.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    if (pricePriceAlertDialog.hasFocus()) {
                        Loher.d("percent")

                        changePercentByPrice(newPrice = s)
                    }
                }
            })


            priceInPercentPriceAlertDialog.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    if (priceInPercentPriceAlertDialog.hasFocus()) {
                        Loher.d("price")

                        changePriceByPercent(onChangedPercent = s)
                    }

                }
            })


        }
    }

    override fun getDialogView(): View {
        return binding.root
    }

}