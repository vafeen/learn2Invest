package ru.surf.learn2invest.ui.alert_dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.databinding.BuyDialogBinding
import ru.surf.learn2invest.noui.database_components.entity.AssetInvest
import ru.surf.learn2invest.noui.database_components.entity.Transaction.Transaction
import ru.surf.learn2invest.noui.database_components.entity.Transaction.TransactionsType
import ru.surf.learn2invest.noui.logs.Loher
import ru.surf.learn2invest.ui.alert_dialogs.parent.CustomAlertDialog

class Buy(
    context: Context,
    val lifecycleScope: LifecycleCoroutineScope,
    val id: String,
    val name: String,
    val symbol: String

) : CustomAlertDialog(context = context) {

    private var binding = BuyDialogBinding.inflate(LayoutInflater.from(context))

    override fun setCancelable(): Boolean {
        return true
    }

    @SuppressLint("SuspiciousIndentation")
    override fun initListeners() {


        binding.apply {

            lifecycleScope.launch(Dispatchers.Main) {

                balanceNumBuyDialog.text =
                    App.profile.fiatBalance.getWithCurrency() // TODO()Володь, Сюда также нужно
                //            поставить нужный тип баланса

                while (true) {
                    val str = 777f
                    priceNumberBuyDialog.text =
                        str.getWithCurrency()  // TODO Сюда нужно будет кидать цену,
                    // которая приходит через ретрофит

                    updateFields()

                    delay(2000)
                }
            }

            buttonExitBuyDialog.setOnClickListener {
                cancel()
            }

            buttonBuyBuyDialog.isVisible = false

            buttonBuyBuyDialog.setOnClickListener {
                buy()

                cancel()
            }

            imageButtonPlusBuyDialog.setOnClickListener {

                enteringNumberOfLotsBuyDialog.setText(enteringNumberOfLotsBuyDialog.text.let { numOfLotsText ->

                    (numOfLotsText.toString().toIntOrNull() ?: 0).let {
                        val balance = App.profile.fiatBalance
                        when {
                            it == 0 -> {
                                "1"
                            }

                            resultPrice(onFuture = true) <= balance -> {
                                (it + 1).toString()
                            }

                            else -> {
                                it.toString()
                            }
                        }
                    }

                })

            }
            imageButtonMinusBuyDialog.setOnClickListener {

                enteringNumberOfLotsBuyDialog.setText(enteringNumberOfLotsBuyDialog.text.let { text ->

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

            enteringNumberOfLotsBuyDialog.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    updateFields()

                    buttonBuyBuyDialog.isVisible =
                        enteringNumberOfLotsBuyDialog.text.toString().toInt() > 0

                }
            })

            tradingPassword.isVisible = if (App.profile.tradingPasswordHash != null) {

                tradingPasswordTV.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?, start: Int, count: Int, after: Int
                    ) {

                    }

                    override fun onTextChanged(
                        s: CharSequence?, start: Int, before: Int, count: Int
                    ) {

                    }

                    override fun afterTextChanged(s: Editable?) {
                        buttonBuyBuyDialog.isVisible = s?.isTrueTradingPassword() ?: false
                    }
                })

                true

            } else {
                false

            }
            Loher.e("вернулось ${tradingPassword.isVisible}")
        }
    }

    private fun buy() {
        val balance = App.profile.fiatBalance

        val price = binding.priceNumberBuyDialog.text.toString().getFloatFromStringWithCurrency()

        val x = binding.enteringNumberOfLotsBuyDialog.toString().toIntOrNull()

        Log.d("buy", "x = \"${binding.enteringNumberOfLotsBuyDialog.text}\" -> $x")

        val amount = (x ?: 0).toFloat()

        if (balance > price * amount) {

            // обновление баланса
            App.profile = App.profile.copy(
                fiatBalance = balance - price * amount
            )

            lifecycleScope.launch(Dispatchers.IO) {
                App.mainDB.apply {

                    // обновление истории
                    transactionDao().insertAll(
                        Transaction(
                            name = name,
                            symbol = symbol,
                            coinPrice = price,
                            dealPrice = price * amount,
                            amount = amount,
                            transactionType = TransactionsType.Buy
                        )
                    )

                    // обновление портфеля
                    assetInvestDao().insertAll(
                        AssetInvest(
                            name = name, symbol = symbol, coinPrice = price, amount = amount
                        )
                    )
                }
            }
        }
    }


    override fun getDialogView(): View {
        return binding.root
    }

    private fun updateFields() {
        "Итого: ${resultPrice(onFuture = false).getWithCurrency()}".also {
            binding.itogoBuyDialog.text = it
        }
    }


    private fun resultPrice(
        onFuture: Boolean
    ): Float {
        binding.apply {
            val priceText = priceNumberBuyDialog.text.toString()

            val price = priceText.substring(2, priceText.length).getFloatFromStringWithCurrency()
            Log.e("error", "price  t= $price")
            val number = enteringNumberOfLotsBuyDialog.text.toString().toIntOrNull() ?: 0

            Log.e("error", "number = $number")
            return price * (number + if (onFuture) {
                1
            } else {
                0
            })
        }
    }

}