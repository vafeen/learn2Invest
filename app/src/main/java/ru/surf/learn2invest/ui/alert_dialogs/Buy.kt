package ru.surf.learn2invest.ui.alert_dialogs

import android.annotation.SuppressLint
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
import ru.surf.learn2invest.databinding.BuyDialogBinding
import ru.surf.learn2invest.ui.alert_dialogs.parent.CustomAlertDialog

class Buy(
    context: Context, val lifecycleScope: LifecycleCoroutineScope
) : CustomAlertDialog(context = context) {

    private var binding = BuyDialogBinding.inflate(LayoutInflater.from(context))

    override fun setCancelable(): Boolean {
        return true
    }

    @SuppressLint("SuspiciousIndentation")
    override fun initListeners() {


        binding.apply {
         balanceNumBuyDialog.text =
                App.profile?.fiatBalance.toString() // TODO()Володь, Сюда также нужно
            //            поставить нужный тип баланса

            lifecycleScope.launch(Dispatchers.Main) {
                while (true) {
                    val str = "777777"
                    priceNumberBuyDialog.text = str  // TODO Сюда нужно будет кидать цену,
                    // которая приходит через ретрофит

                    updateFields()
                    delay(2000)
                }
            }

            buttonExitBuyDialog.setOnClickListener {
                cancel()
            }

            buttonBuyBuyDialog.setOnClickListener {
                // TODO Логика продажи

                cancel()
            }

            imageButtonPlusBuyDialog.setOnClickListener {
                enteringNumberOfLotsBuyDialog.setText(enteringNumberOfLotsBuyDialog.text.let { text ->

                    val newNumberOfLots = if (text.isNotEmpty()) {
                        text.toString().toIntOrNull()?.let {
                            val balance = App.profile?.fiatBalance
                                ?: 0 // TODO(Володь, тут также поменять баланс на нужный)
                            val itog = itog(onFuture = true)
                            //Loher.d("itog = $itog")
                            if (itog(onFuture = true) <= balance) {
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

            imageButtonMinusBuyDialog.setOnClickListener {
                enteringNumberOfLotsBuyDialog.setText(enteringNumberOfLotsBuyDialog.text.let { text ->
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

            enteringNumberOfLotsBuyDialog.addTextChangedListener(object : TextWatcher {
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
                    updateFields()
                }
            })

        }
    }

    override fun getDialogView(): View {
        return binding.root
    }

    private fun updateFields() {
        binding.itogoBuyDialog.text = "Итого: $ ${itog(onFuture = false)}"
    }


    private fun itog(
        onFuture: Boolean
    ): Float {
        binding.apply {
            val priceText = priceNumberBuyDialog.text.toString()

            val price = priceText.substring(1, priceText.length)
                .toFloatOrNull() ?: 0f

            val number = enteringNumberOfLotsBuyDialog.text.toString()
                .toIntOrNull() ?: 0

            return price * (number + if (onFuture) {
                1
            } else {
                0
            })
        }
    }

}