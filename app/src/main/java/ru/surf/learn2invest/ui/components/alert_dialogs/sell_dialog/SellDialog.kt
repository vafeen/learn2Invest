package ru.surf.learn2invest.ui.components.alert_dialogs.sell_dialog

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.R
import ru.surf.learn2invest.databinding.SellDialogBinding
import ru.surf.learn2invest.noui.database_components.entity.AssetInvest
import ru.surf.learn2invest.ui.components.alert_dialogs.parent.CustomBottomSheetDialog
import ru.surf.learn2invest.utils.getFloatFromStringWithCurrency
import ru.surf.learn2invest.utils.getWithCurrency
import ru.surf.learn2invest.utils.isTrueTradingPasswordOrIsNotDefined

/**
 * Диалог продажи актива
 * @param dialogContext [Контекст открытия диалога]
 * @param lifecycleScope [Scope для выполнения асинхронных операция]
 * @param id [ID coin'а]
 * @param name [Имя (Bitcoin)]
 * @param symbol [Абревиатура (BTC)]
 */
@AndroidEntryPoint
class SellDialog(
    val dialogContext: Context,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val id: String,
    private val name: String,
    private val symbol: String,
) : CustomBottomSheetDialog() {
    override val dialogTag: String = "sell"
    private var binding = SellDialogBinding.inflate(LayoutInflater.from(dialogContext))
    private val viewModel: SellDialogViewModel by viewModels()

    override fun initListeners() {
        binding.apply {
            balanceNum.text = viewModel.databaseRepository.profile.fiatBalance.getWithCurrency()
            buttonSell.isVisible = false
            buttonSell.setOnClickListener {
                sell()
                dismiss()
            }
            val coin = viewModel.coin
            imageButtonPlus.setOnClickListener {
                enteringNumberOfLots.setText(enteringNumberOfLots.text.let { text ->
                    val newNumberOfLots = if (text.isNotEmpty()) {
                        text.toString().toIntOrNull()?.let {
                            if (enteringNumberOfLots.text.toString()
                                    .toFloat() < coin.amount
                            ) it + 1 else it
                        } ?: 0
                    } else 1

                    "$newNumberOfLots"
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

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    updateFields()
                }
            })

            tradingPassword.isVisible =
                if (viewModel.databaseRepository.profile.tradingPasswordHash != null && coin.amount > 0) {
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

    private fun sell() {
        val price = binding.priceNumber.text.toString().getFloatFromStringWithCurrency() ?: 0f
        val amountCurrent = binding.enteringNumberOfLots.text.toString().toInt().toFloat()
        viewModel.sell(price, amountCurrent)
    }


    override fun getDialogView(): View {
        return binding.root
    }

    private fun updateFields() {
        binding.apply {
            when {
                viewModel.coin.amount == 0f -> {
                    buttonSell.isVisible = false
                    result.text =
                        ContextCompat.getString(dialogContext, R.string.no_asset_for_sale)
                }

                viewModel.coin.amount > 0f && enteringNumberOfLots.text.toString().toFloatOrNull()
                    .let {
                        it != null && it in 1f..viewModel.coin.amount
                    } -> {
                    buttonSell.isVisible =
                        tradingPasswordTV.text.toString()
                            .isTrueTradingPasswordOrIsNotDefined(profile = viewModel.databaseRepository.profile)
                    result.text = buildString {
                        append(
                            ContextCompat.getString(
                                dialogContext, R.string.itog
                            )
                        )
                        append(resultPrice().getWithCurrency())
                    }
                }

                else -> {
                    buttonSell.isVisible = false
                    result.text = ""
                }
            }
            viewModel.apply {
                imageButtonPlus.isVisible = coin.amount != 0f
                imageButtonMinus.isVisible = coin.amount != 0f
                enteringNumberOfLots.isEnabled = coin.amount != 0f
            }
        }
    }

    private fun resultPrice(): Float {
        binding.apply {
            val price = priceNumber.text.toString().getFloatFromStringWithCurrency() ?: 0f
            val number = enteringNumberOfLots.text.toString().toIntOrNull() ?: 0
            return price * number
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