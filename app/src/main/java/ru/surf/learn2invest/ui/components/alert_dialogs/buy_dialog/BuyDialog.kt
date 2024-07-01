package ru.surf.learn2invest.ui.components.alert_dialogs.buy_dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.R
import ru.surf.learn2invest.databinding.BuyDialogBinding
import ru.surf.learn2invest.noui.database_components.entity.AssetInvest
import ru.surf.learn2invest.ui.components.alert_dialogs.parent.CustomBottomSheetDialog
import ru.surf.learn2invest.utils.getFloatFromStringWithCurrency
import ru.surf.learn2invest.utils.getWithCurrency
import ru.surf.learn2invest.utils.isTrueTradingPasswordOrIsNotDefined

/**
 * Диалог покупки актива
 * @param dialogContext [Контекст открытия диалога]
 * @param id [ID coin'а]
 * @param name [Имя (Bitcoin)]
 * @param symbol [Абревиатура (BTC)]
 */

@AndroidEntryPoint
class BuyDialog(
    val dialogContext: Context,
    private val id: String,
    private val name: String,
    private val symbol: String,
) : CustomBottomSheetDialog() {
    private var binding = BuyDialogBinding.inflate(LayoutInflater.from(dialogContext))
    override val dialogTag: String = "buy"
    private val viewModel: BuyDialogViewModel by viewModels()

    override fun initListeners() {
        binding.apply {
            lifecycleScope.launch(Dispatchers.Main) {
                balanceNum.text = viewModel.databaseRepository.profile.fiatBalance.getWithCurrency()
            }
            buttonBuy.isVisible = false

            buttonBuy.setOnClickListener {
                buy()
                dismiss()
            }

            imageButtonPlus.setOnClickListener {
                enteringNumberOfLots.setText(enteringNumberOfLots.text.let { numOfLotsText ->
                    (numOfLotsText.toString().toIntOrNull() ?: 0).let {
                        val balance = viewModel.databaseRepository.profile.fiatBalance
                        when {
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

            imageButtonMinus.setOnClickListener {
                enteringNumberOfLots.setText(enteringNumberOfLots.text.let { text ->
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

            enteringNumberOfLots.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    updateFields()
                }
            })

            tradingPassword.isVisible =
                if (viewModel.databaseRepository.profile.tradingPasswordHash != null && viewModel.databaseRepository.profile.fiatBalance != 0f) {
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
                        }
                    })
                    true
                } else false
        }
    }

    override fun dismiss() {
        super.dismiss()
        viewModel.realTimeUpdateJob.cancel()
    }

    private fun buy() {
        val price = binding.priceNumber.text.toString().getFloatFromStringWithCurrency() ?: 0f
        val amountCurrent = binding.enteringNumberOfLots.text.toString().toInt().toFloat()
        viewModel.buy(amountCurrent = amountCurrent, price = price)
    }

    override fun getDialogView(): View {
        return binding.root
    }

    private fun updateFields() {
        val willPrice = resultPrice(onFuture = false)
        val fiatBalance = viewModel.databaseRepository.profile.fiatBalance

        binding.apply {
            when {
                enteringNumberOfLots.text.toString().toIntOrNull().let {
                    it != null && it > 0
                } && fiatBalance != 0f && willPrice <= fiatBalance -> {
                    buttonBuy.isVisible =
                        tradingPasswordTV.text.toString().isTrueTradingPasswordOrIsNotDefined(
                            profile = viewModel.databaseRepository.profile
                        )
                    result.text = buildString {
                        append(ContextCompat.getString(dialogContext, R.string.itog))
                        append(willPrice.getWithCurrency())
                    }
                }

                willPrice > fiatBalance || fiatBalance == 0f -> {
                    buttonBuy.isVisible = false
                    result.text = ContextCompat.getString(
//                        dialogContext,
                        dialogContext,
                        R.string.not_enough_money_for_buy
                    )
                }

                else -> {
                    buttonBuy.isVisible = false
                    result.text = ""
                }
            }
            viewModel.apply {
                imageButtonPlus.isVisible = databaseRepository.profile.fiatBalance != 0f
                imageButtonMinus.isVisible = databaseRepository.profile.fiatBalance != 0f
                enteringNumberOfLots.isEnabled = databaseRepository.profile.fiatBalance != 0f
            }
        }
    }


    private fun resultPrice(
        onFuture: Boolean
    ): Float {
        binding.apply {
            val priceText = priceNumber.text.toString()
            val price = priceText.getFloatFromStringWithCurrency() ?: 0f
            val number = enteringNumberOfLots.text.toString().toIntOrNull() ?: 0
            return price * (number + if (onFuture) 1 else 0)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        var asset: AssetInvest? = null
        viewModel.apply {
            coin = AssetInvest(
                name = name, symbol = symbol, coinPrice = 0f, amount = 0f, assetID = id
            )
            lifecycleScope.launch(Dispatchers.IO) {
                asset = databaseRepository.getBySymbolAssetInvest(symbol = symbol)
            }.invokeOnCompletion {
                if (asset != null) coin = asset as AssetInvest
                updateFields()
                realTimeUpdateJob = startRealTimeUpdate {
                    lifecycleScope.launch(Dispatchers.Main) {
                        binding.priceNumber.text = it
                        updateFields()
                    }
                }
            }
        }
    }
}