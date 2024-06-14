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
import ru.surf.learn2invest.noui.cryptography.verifyTradingPassword
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

    private var haveAssetsOrNot = false

    private var coin: AssetInvest = AssetInvest(
        name = name, symbol = symbol, coinPrice = 0f, amount = 0f
    )

    override fun setCancelable(): Boolean {
        return true
    }

    @SuppressLint("SuspiciousIndentation")
    override fun initListeners() {


        binding.apply {

            enteringNumberOfLotsBuyDialog.setText("0")

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
                                "0"
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

                    buttonBuyBuyDialog.isVisible = enteringNumberOfLotsBuyDialog.text.toString()
                        .isNotEmpty() && enteringNumberOfLotsBuyDialog.text.toString()
                        .toInt() > 0 && if (App.profile.tradingPasswordHash != null) {
                        verifyTradingPassword(
                            user = App.profile, password = binding.tradingPasswordTV.text.toString()
                        )
                    } else {
                        true
                    }

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

        val amountCurrent = binding.enteringNumberOfLotsBuyDialog.text.toString().toInt().toFloat()

        if (balance > price * amountCurrent) {

            // обновление баланса
            App.profile = App.profile.copy(
                fiatBalance = balance - price * amountCurrent,
                assetBalance = App.profile.assetBalance + price * amountCurrent
            )

            lifecycleScope.launch(Dispatchers.IO) {
                App.mainDB.apply {

                    // обновление истории
                    transactionDao().insertAll(
                        Transaction(
                            name = name,
                            symbol = symbol,
                            coinPrice = price,
                            dealPrice = price * amountCurrent,
                            amount = amountCurrent,
                            transactionType = TransactionsType.Buy
                        )
                    )

                    if (haveAssetsOrNot) {
                        assetInvestDao().update(
                            coin.copy(
                                coinPrice = (coin.coinPrice * coin.amount + amountCurrent * price) / (coin.amount + amountCurrent),
                                amount = coin.amount + amountCurrent
                            )
                        )
                    } else {
                        // обновление портфеля
                        assetInvestDao().insertAll(
                            coin.copy(
                                coinPrice = (coin.coinPrice * coin.amount + amountCurrent * price) / (coin.amount + amountCurrent),
                                amount = coin.amount + amountCurrent
                            )
                        )
                    }
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

            val price = priceText.getFloatFromStringWithCurrency()
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


    override fun show() {
        super.show()

        lifecycleScope.launch(Dispatchers.IO) {
            val coinMayBeInPortfolio = App.mainDB.assetInvestDao().getBySymbol(symbol = symbol)

            if (coinMayBeInPortfolio != null) {
                haveAssetsOrNot = true

                coin = coinMayBeInPortfolio
            }

            Log.d("coin", "coin = $coin")
        }

    }

}