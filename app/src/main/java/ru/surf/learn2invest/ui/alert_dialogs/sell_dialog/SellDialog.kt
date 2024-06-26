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
import kotlinx.coroutines.launch
import ru.surf.learn2invest.R
import ru.surf.learn2invest.databinding.SellDialogBinding
import ru.surf.learn2invest.noui.cryptography.isTrueTradingPasswordOrIsNotDefined
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.noui.database_components.entity.AssetInvest
import ru.surf.learn2invest.ui.alert_dialogs.getFloatFromStringWithCurrency
import ru.surf.learn2invest.ui.alert_dialogs.getWithCurrency
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
                name = name, symbol = symbol, coinPrice = 0f, amount = 0f, assetID = id
            )
        }
        binding.apply {
            balanceNum.text = DatabaseRepository.profile.fiatBalance.getWithCurrency()
            viewModel.realTimeUpdateJob = viewModel.startRealTimeUpdate {
                lifecycleScope.launch(Dispatchers.Main) {
                    binding.priceNumber.text = it
                    updateFields()
                }
            }
            buttonExit.setOnClickListener {
                cancel()
            }
            buttonSell.isVisible = false
            buttonSell.setOnClickListener {
                sell()
                cancel()
            }
            val coin = viewModel.coin
            imageButtonPlus.isVisible = coin.amount != 0f
            imageButtonMinus.isVisible = coin.amount != 0f
            enteringNumberOfLots.isEnabled = coin.amount != 0f
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
                if (DatabaseRepository.profile.tradingPasswordHash != null && coin.amount > 0) {
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

    override fun cancel() {
        super.cancel()
        viewModel.realTimeUpdateJob.cancel()
    }

    private fun sell() {
        val price = binding.priceNumber.text.toString().getFloatFromStringWithCurrency()
        val amountCurrent = binding.enteringNumberOfLots.text.toString().toInt().toFloat()
        viewModel.sell(price, amountCurrent)
    }


    override fun getDialogView(): View {
        return binding.root
    }

    private fun updateFields() {
        val coin = viewModel.coin
        binding.apply {
            when {
                coin.amount == 0f -> {
                    buttonSell.isVisible = false
                    result.text =
                        ContextCompat.getString(requireContext(), R.string.no_asset_for_sale)
                }

                coin.amount > 0 && enteringNumberOfLots.text.toString().toFloatOrNull().let {
                    it != null && it in 1f..coin.amount
                } -> {
                    buttonSell.isVisible =
                        tradingPasswordTV.text.toString().isTrueTradingPasswordOrIsNotDefined()
                    result.text = buildString {
                        append(
                            ContextCompat.getString(
                                requireContext(), R.string.itog
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
        }
    }

    private fun resultPrice(): Float {
        binding.apply {
            val price = priceNumber.text.toString().getFloatFromStringWithCurrency()
            val number = enteringNumberOfLots.text.toString().toIntOrNull() ?: 0
            return price * number
        }
    }

    override fun show() {
        super.show()
        lifecycleScope.launch(Dispatchers.IO) {
            DatabaseRepository.getBySymbolAssetInvest(symbol = symbol)?.let {
                viewModel.coin = it
            }
        }
    }


}