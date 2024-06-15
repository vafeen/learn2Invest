package ru.surf.learn2invest.ui.alert_dialogs

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.surf.learn2invest.R
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.databinding.SellDialogBinding
import ru.surf.learn2invest.noui.cryptography.verifyTradingPassword
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.noui.database_components.entity.AssetInvest
import ru.surf.learn2invest.noui.database_components.entity.transaction.Transaction
import ru.surf.learn2invest.noui.database_components.entity.transaction.TransactionsType
import ru.surf.learn2invest.noui.logs.Loher
import ru.surf.learn2invest.ui.alert_dialogs.parent.CustomAlertDialog

class Sell(
    val context: Context,
    val lifecycleScope: LifecycleCoroutineScope,
    val id: String,
    val name: String,
    val symbol: String


) : CustomAlertDialog(context = context) {

    private var binding = SellDialogBinding.inflate(LayoutInflater.from(context))

    private var coin: AssetInvest = AssetInvest(
        name = name, symbol = symbol, coinPrice = 0f, amount = 0f
    )

    override fun setCancelable(): Boolean {
        return true
    }

    override fun initListeners() {


        binding.apply {

            enteringNumberOfLotsSellDialog.setText("0")

            lifecycleScope.launch {
                balanceNumSellDialog.text = App.profile.fiatBalance.getWithCurrency()

                while (true) {
                    val str = 777f
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
                sell()

                cancel()
            }

            imageButtonPlusSellDialog.setOnClickListener {

                enteringNumberOfLotsSellDialog.setText(enteringNumberOfLotsSellDialog.text.let { text ->

                    val newNumberOfLots = if (text.isNotEmpty()) {
                        text.toString().toIntOrNull()?.let {
                            if (enteringNumberOfLotsSellDialog.text.toString()
                                    .toFloat() < coin.amount
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

            enteringNumberOfLotsSellDialog.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    updateFields()

                    buttonSellSellDialog.isVisible = enteringNumberOfLotsSellDialog.text.toString()
                        .isNotEmpty() && enteringNumberOfLotsSellDialog.text.toString()
                        .toInt() > 0 && coin.amount > 0 && if (App.profile.tradingPasswordHash != null) {
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

    private fun sell() {
        val balance = App.profile.fiatBalance

        val price = binding.priceNumberSellDialog.text.toString().getFloatFromStringWithCurrency()

        val amountCurrent = binding.enteringNumberOfLotsSellDialog.text.toString().toInt().toFloat()

        // обновление баланса
        App.profile = App.profile.copy(
            fiatBalance = balance + price * amountCurrent
        )

        lifecycleScope.launch(Dispatchers.IO) {
            DatabaseRepository.apply {

                // обновление истории
                insertAllTransaction(
                    Transaction(
                        name = name,
                        symbol = symbol,
                        coinPrice = price,
                        dealPrice = price * amountCurrent,
                        amount = amountCurrent,
                        transactionType = TransactionsType.Sell
                    )
                )


                // обновление портфеля
                if (amountCurrent < coin.amount) {
                    insertAllAssetInvest(
                        coin.copy(
                            coinPrice = (coin.coinPrice * coin.amount - amountCurrent * price) / (coin.amount - amountCurrent),
                            amount = coin.amount - amountCurrent
                        )
                    )
                } else {
                    deleteAssetInvest(coin)
                }


            }
        }


    }


    override fun getDialogView(): View {
        return binding.root
    }

    private fun updateFields() {
        binding.itogoSellDialog.text =
            ContextCompat.getString(context, R.string.final_price) + "$ ${resultPrice()}"
    }

    private fun resultPrice(): Float {
        binding.apply {
            val priceText = priceNumberSellDialog.text.toString()

            val price = priceText.getFloatFromStringWithCurrency()

            val number = enteringNumberOfLotsSellDialog.text.toString().toIntOrNull() ?: 0

            Log.e("error", "number = $number")

            return price * number
        }
    }

    override fun show() {
        super.show()

        lifecycleScope.launch(Dispatchers.IO) {
            val coinMayBeInPortfolio = DatabaseRepository
                .getBySymbolAssetInvest(symbol = symbol)

            if (coinMayBeInPortfolio != null) {
                coin = coinMayBeInPortfolio
            }

            Log.d("coin", "coin = $coin")
        }

    }

}