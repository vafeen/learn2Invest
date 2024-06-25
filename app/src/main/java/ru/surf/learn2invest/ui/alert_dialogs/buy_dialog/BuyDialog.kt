package ru.surf.learn2invest.ui.alert_dialogs.buy_dialog

import android.annotation.SuppressLint
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
import ru.surf.learn2invest.databinding.BuyDialogBinding
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.noui.database_components.entity.AssetInvest
import ru.surf.learn2invest.ui.alert_dialogs.getFloatFromStringWithCurrency
import ru.surf.learn2invest.ui.alert_dialogs.getWithCurrency
import ru.surf.learn2invest.ui.alert_dialogs.parent.CustomAlertDialog

class BuyDialog(
    context: Context,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val id: String,
    private val name: String,
    private val symbol: String,
    supportFragmentManager: FragmentManager
) : CustomAlertDialog(supportFragmentManager) {
    override val dialogTag: String = "buy"
    private var binding = BuyDialogBinding.inflate(LayoutInflater.from(context))
    private lateinit var viewModel: BuyDialogViewModel


    override fun setCancelable(): Boolean = false

    @SuppressLint("SuspiciousIndentation")
    override fun initListeners() {
        viewModel = ViewModelProvider(this)[BuyDialogViewModel::class.java]
        viewModel.apply {
            coin = AssetInvest(
                name = name, symbol = symbol, coinPrice = 0f, amount = 0f,
                assetID = id
            )
            realTimeUpdateJob = startRealTimeUpdate {
                lifecycleScope.launch(Dispatchers.Main) {
                    binding.priceNumber.text = it
                    updateFields()
                }
            }
        }
        binding.apply {
            lifecycleScope.launch(Dispatchers.Main) {
                balanceNum.text = DatabaseRepository.profile.fiatBalance.getWithCurrency()
            }

            buttonExit.setOnClickListener {
                cancel()
            }

            buttonBuy.isVisible = false
            buttonBuy.setOnClickListener {
                buy()
                cancel()
            }
            imageButtonPlus.isVisible = DatabaseRepository.profile.fiatBalance != 0f
            imageButtonMinus.isVisible = DatabaseRepository.profile.fiatBalance != 0f
            enteringNumberOfLots.isEnabled = DatabaseRepository.profile.fiatBalance != 0f
            imageButtonPlus.setOnClickListener {
                enteringNumberOfLots.setText(enteringNumberOfLots.text.let { numOfLotsText ->
                    (numOfLotsText.toString().toIntOrNull() ?: 0).let {
                        val balance = DatabaseRepository.profile.fiatBalance
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

    private fun buy() {
        val price = binding.priceNumber.text.toString().getFloatFromStringWithCurrency()
        val amountCurrent = binding.enteringNumberOfLots.text.toString().toInt().toFloat()
        viewModel.buy(amountCurrent = amountCurrent, price = price)
    }


    override fun getDialogView(): View {
        return binding.root
    }

    private fun updateFields() {
        val willPrice = resultPrice(onFuture = false)
        val fiatBalance = DatabaseRepository.profile.fiatBalance
        when {
            binding.enteringNumberOfLots.text.toString()
                .toIntOrNull().let {
                    it != null && it > 0
                } && fiatBalance != 0f && willPrice <= fiatBalance -> {
                binding.buttonBuy.isVisible = true
                binding.result.text = buildString {
                    append(
                        ContextCompat.getString(
                            requireContext(),
                            R.string.itog
                        )
                    )
                    append(willPrice.getWithCurrency())
                }
            }

            willPrice > fiatBalance || fiatBalance == 0f -> {
                binding.buttonBuy.isVisible = false
                binding.result.text =
                    ContextCompat.getString(requireContext(), R.string.not_enough_money_for_buy)
            }

            else -> {
                binding.buttonBuy.isVisible = false
                binding.result.text = ""
            }
        }
    }


    private fun resultPrice(
        onFuture: Boolean
    ): Float {
        binding.apply {
            val priceText = priceNumber.text.toString()
            val price = priceText.getFloatFromStringWithCurrency()
            val number = enteringNumberOfLots.text.toString().toIntOrNull() ?: 0
            return price * (number + if (onFuture) 1 else 0)
        }
    }


    override fun show() {
        super.show()

        lifecycleScope.launch(Dispatchers.IO) {

            DatabaseRepository.getBySymbolAssetInvest(symbol = symbol)?.let {
                viewModel.apply {
                    haveAssetsOrNot = true
                    coin = it
                }
            }
        }
    }


}