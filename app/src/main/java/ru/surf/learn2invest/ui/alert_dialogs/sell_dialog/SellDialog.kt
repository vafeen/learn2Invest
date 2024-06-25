package ru.surf.learn2invest.ui.alert_dialogs.sell_dialog

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.surf.learn2invest.R
import ru.surf.learn2invest.databinding.SellDialogBinding
import ru.surf.learn2invest.network_components.NetworkRepository
import ru.surf.learn2invest.network_components.ResponseWrapper
import ru.surf.learn2invest.noui.cryptography.verifyTradingPassword
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.noui.database_components.entity.AssetInvest
import ru.surf.learn2invest.noui.database_components.entity.transaction.Transaction
import ru.surf.learn2invest.noui.database_components.entity.transaction.TransactionsType
import ru.surf.learn2invest.ui.alert_dialogs.getFloatFromStringWithCurrency
import ru.surf.learn2invest.ui.alert_dialogs.getWithCurrency
import ru.surf.learn2invest.ui.alert_dialogs.isTrueTradingPassword
import ru.surf.learn2invest.ui.alert_dialogs.parent.CustomAlertDialog

class SellDialog(
    dialogContext: Context,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val id: String,
    private val name: String,
    private val symbol: String,
    supportFragmentManager: FragmentManager
) : CustomAlertDialog(supportFragmentManager) {
    override val dialogTag: String = "sell"
    private var binding = SellDialogBinding.inflate(LayoutInflater.from(dialogContext))
    private lateinit var viewModel: SellDialogViewModel

    override fun setCancelable(): Boolean = false

    override fun initListeners() {
        viewModel = ViewModelProvider(this)[SellDialogViewModel::class.java]
        viewModel.apply {
            coin = AssetInvest(
                name = name, symbol = symbol, coinPrice = 0f, amount = 0f,
                assetID = id
            )
        }
        binding.apply {
            balanceNumSellDialog.text = DatabaseRepository.profile.fiatBalance.getWithCurrency()
            viewModel.realTimeUpdateJob = startRealTimeUpdate()
            buttonExitSellDialog.setOnClickListener {
                cancel()
            }
            buttonSellSellDialog.isVisible = false
            buttonSellSellDialog.setOnClickListener {
                sell()
                cancel()
            }
            val coin = viewModel.coin
            imageButtonPlusSellDialog.isVisible = coin.amount != 0f
            imageButtonMinusSellDialog.isVisible = coin.amount != 0f
            enteringNumberOfLotsSellDialog.isEnabled = coin.amount != 0f
            imageButtonPlusSellDialog.setOnClickListener {
                enteringNumberOfLotsSellDialog.setText(enteringNumberOfLotsSellDialog.text.let { text ->
                    val newNumberOfLots = if (text.isNotEmpty()) {
                        text.toString().toIntOrNull()?.let {
                            if (enteringNumberOfLotsSellDialog.text.toString()
                                    .toFloat() < coin.amount
                            ) it + 1 else it
                        } ?: 0
                    } else 1

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
                                text
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

            tradingPassword.isVisible =
                if (DatabaseRepository.profile.tradingPasswordHash != null) {
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
                            updateFields()
                            buttonSellSellDialog.isVisible = s?.isTrueTradingPassword() ?: false
                        }
                    })
                    true
                } else false
        }
    }

    override fun cancel() {
        super.cancel()
        viewModel.realTimeUpdateJob.cancel()
    }

    private fun sell() {
        val balance = DatabaseRepository.profile.fiatBalance
        val price = binding.priceNumberSellDialog.text.toString().getFloatFromStringWithCurrency()
        val amountCurrent = binding.enteringNumberOfLotsSellDialog.text.toString().toInt().toFloat()
        lifecycleScope.launch(Dispatchers.IO) {
            DatabaseRepository.apply {
                // обновление баланса
                updateProfile(
                    profile.copy(
                        fiatBalance = balance + price * amountCurrent,
                    )
                )

                // обновление истории
                insertAllTransaction(
                    Transaction(
                        coinID = id,
                        name = name,
                        symbol = symbol,
                        coinPrice = price,
                        dealPrice = price * amountCurrent,
                        amount = amountCurrent,
                        transactionType = TransactionsType.Sell
                    )
                )
                viewModel.apply {
                    // обновление портфеля
                    if (amountCurrent < coin.amount) {
                        insertAllAssetInvest(
                            coin.copy(
                                coinPrice = (coin.coinPrice * coin.amount - amountCurrent * price) / (coin.amount - amountCurrent),
                                amount = coin.amount - amountCurrent
                            )
                        )
                    } else deleteAssetInvest(coin)
                }
            }
        }
    }


    override fun getDialogView(): View {
        return binding.root
    }

    private fun updateFields() {
        val coin = viewModel.coin
        when {
            coin.amount == 0f -> {
                binding.buttonSellSellDialog.isVisible = false
                binding.itogoSellDialog.text =
                    ContextCompat.getString(requireContext(), R.string.no_asset_for_sale)
            }

            coin.amount > 0
                    && binding.enteringNumberOfLotsSellDialog.text.toString()
                .toFloatOrNull().let {
                    it != null && it in 1f..coin.amount
                }
                    && if (DatabaseRepository.profile.tradingPasswordHash != null) verifyTradingPassword(
                user = DatabaseRepository.profile,
                password = binding.tradingPasswordTV.text.toString()
            ) else true
            -> {
                binding.buttonSellSellDialog.isVisible = true
                binding.itogoSellDialog.text = buildString {
                    append(
                        ContextCompat.getString(
                            requireContext(),
                            R.string.itog
                        )
                    )
                    append(resultPrice().getWithCurrency())
                }
            }

            else -> {
                binding.buttonSellSellDialog.isVisible = false
                binding.itogoSellDialog.text = ""
            }
        }
    }

    private fun resultPrice(): Float {
        binding.apply {
            val price = priceNumberSellDialog.text.toString().getFloatFromStringWithCurrency()
            val number = enteringNumberOfLotsSellDialog.text.toString().toIntOrNull() ?: 0
            return price * number
        }
    }

    override fun show() {
        super.show()
        lifecycleScope.launch(Dispatchers.IO) {
            val coinMayBeInPortfolio = DatabaseRepository.getBySymbolAssetInvest(symbol = symbol)
            if (coinMayBeInPortfolio != null) {
                viewModel.coin = coinMayBeInPortfolio
            }
        }
    }

    private fun startRealTimeUpdate(): Job = lifecycleScope.launch(Dispatchers.IO) {
        while (true) {
            when (val result = NetworkRepository.getCoinReview(id)) {
                is ResponseWrapper.Success -> {
                    withContext(Dispatchers.Main) {
                        binding.priceNumberSellDialog.text =
                            result.value.data.priceUsd.getWithCurrency()
                        updateFields()
                    }
                }

                is ResponseWrapper.NetworkError -> {}
            }

            delay(5000)
        }
    }
}