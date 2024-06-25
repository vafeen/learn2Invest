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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.surf.learn2invest.R
import ru.surf.learn2invest.databinding.BuyDialogBinding
import ru.surf.learn2invest.network_components.NetworkRepository
import ru.surf.learn2invest.network_components.ResponseWrapper
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.noui.database_components.entity.AssetInvest
import ru.surf.learn2invest.noui.database_components.entity.transaction.Transaction
import ru.surf.learn2invest.noui.database_components.entity.transaction.TransactionsType
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
            realTimeUpdateJob = startRealTimeUpdate()
        }
        binding.apply {
            lifecycleScope.launch(Dispatchers.Main) {
                balanceNumBuyDialog.text = DatabaseRepository.profile.fiatBalance.getWithCurrency()
            }

            buttonExitBuyDialog.setOnClickListener {
                cancel()
            }

            buttonBuyBuyDialog.isVisible = false
            buttonBuyBuyDialog.setOnClickListener {
                buy()
                cancel()
            }
            imageButtonPlusBuyDialog.isVisible = DatabaseRepository.profile.fiatBalance != 0f
            imageButtonMinusBuyDialog.isVisible = DatabaseRepository.profile.fiatBalance != 0f
            enteringNumberOfLotsBuyDialog.isEnabled = DatabaseRepository.profile.fiatBalance != 0f
            imageButtonPlusBuyDialog.setOnClickListener {
                enteringNumberOfLotsBuyDialog.setText(enteringNumberOfLotsBuyDialog.text.let { numOfLotsText ->
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
                                text
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
        val balance = DatabaseRepository.profile.fiatBalance
        val price = binding.priceNumberBuyDialog.text.toString().getFloatFromStringWithCurrency()
        val amountCurrent = binding.enteringNumberOfLotsBuyDialog.text.toString().toInt().toFloat()
        if (balance > price * amountCurrent) {
            lifecycleScope.launch(Dispatchers.IO) {
                DatabaseRepository.apply {
                    // обновление баланса
                    updateProfile(profile.copy(fiatBalance = balance - price * amountCurrent))

                    // обновление истории
                    insertAllTransaction(
                        Transaction(
                            coinID = id,
                            name = name,
                            symbol = symbol,
                            coinPrice = price,
                            dealPrice = price * amountCurrent,
                            amount = amountCurrent,
                            transactionType = TransactionsType.Buy
                        )
                    )
                    viewModel.apply {
                        // обновление портфеля
                        if (viewModel.haveAssetsOrNot) {
                            updateAssetInvest(
                                coin.copy(
                                    coinPrice = (coin.coinPrice * coin.amount + amountCurrent * price)
                                            / (coin.amount + amountCurrent),
                                    amount = coin.amount + amountCurrent
                                )
                            )
                        } else {
                            insertAllAssetInvest(
                                coin.copy(
                                    coinPrice = (coin.coinPrice * coin.amount + amountCurrent * price)
                                            / (coin.amount + amountCurrent),
                                    amount = coin.amount + amountCurrent
                                )
                            )
                        }
                    }
                }
            }
        }
    }


    override fun getDialogView(): View {
        return binding.root
    }

    private fun updateFields() {
        val willPrice = resultPrice(onFuture = false)
        val fiatBalance = DatabaseRepository.profile.fiatBalance
        when {
            binding.enteringNumberOfLotsBuyDialog.text.toString()
                .toIntOrNull().let {
                    it != null && it > 0
                } && fiatBalance != 0f && willPrice <= fiatBalance -> {
                binding.buttonBuyBuyDialog.isVisible = true
                binding.itogoBuyDialog.text = buildString {
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
                binding.buttonBuyBuyDialog.isVisible = false
                binding.itogoBuyDialog.text =
                    ContextCompat.getString(requireContext(), R.string.not_enough_money_for_buy)
            }

            else -> {
                binding.buttonBuyBuyDialog.isVisible = false
                binding.itogoBuyDialog.text = ""
            }
        }
    }


    private fun resultPrice(
        onFuture: Boolean
    ): Float {
        binding.apply {
            val priceText = priceNumberBuyDialog.text.toString()
            val price = priceText.getFloatFromStringWithCurrency()
            val number = enteringNumberOfLotsBuyDialog.text.toString().toIntOrNull() ?: 0
            return price * (number + if (onFuture) 1 else 0)
        }
    }


    override fun show() {
        super.show()

        lifecycleScope.launch(Dispatchers.IO) {
            val coinMayBeInPortfolio =
                DatabaseRepository.getBySymbolAssetInvest(symbol = symbol)
            if (coinMayBeInPortfolio != null) {
                viewModel.apply {
                    haveAssetsOrNot = true
                    coin = coinMayBeInPortfolio
                }
            }
        }
    }

    private fun startRealTimeUpdate(): Job = lifecycleScope.launch(Dispatchers.IO) {
        while (true) {
            when (val result = NetworkRepository.getCoinReview(id)) {
                is ResponseWrapper.Success -> {
                    withContext(Dispatchers.Main) {
                        binding.priceNumberBuyDialog.text =
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