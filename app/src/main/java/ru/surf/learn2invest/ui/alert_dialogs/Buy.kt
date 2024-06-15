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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.databinding.BuyDialogBinding
import ru.surf.learn2invest.network_components.NetworkRepository
import ru.surf.learn2invest.network_components.ResponseWrapper
import ru.surf.learn2invest.noui.logs.Loher
import ru.surf.learn2invest.ui.alert_dialogs.parent.CustomAlertDialog

class Buy(
    context: Context, val lifecycleScope: LifecycleCoroutineScope, val id: String
) : CustomAlertDialog(context = context) {

    private var binding = BuyDialogBinding.inflate(LayoutInflater.from(context))
    private lateinit var realTimeUpdateJob: Job

    override fun setCancelable(): Boolean {
        return true
    }

    @SuppressLint("SuspiciousIndentation")
    override fun initListeners() {


        binding.apply {

            lifecycleScope.launch(Dispatchers.Main) {

                balanceNumBuyDialog.text =
                    App.profile.fiatBalance.getWithCurrency()

                realTimeUpdateJob = startRealTimeUpdate()
            }

            buttonExitBuyDialog.setOnClickListener {
                cancel()
            }

            buttonBuyBuyDialog.isVisible = false

            buttonBuyBuyDialog.setOnClickListener {
                // TODO Логика продажи

                cancel()
            }

            imageButtonPlusBuyDialog.setOnClickListener {

                lifecycleScope.launch(Dispatchers.Main) {

                    enteringNumberOfLotsBuyDialog.setText(enteringNumberOfLotsBuyDialog.text.let { numOfLotsText ->

                        (numOfLotsText.toString().toIntOrNull() ?: 0).let {
                            val balance = App.profile.fiatBalance
                            when {
                                it == 0 -> {
                                    ""
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
            }
            imageButtonMinusBuyDialog.setOnClickListener {

                enteringNumberOfLotsBuyDialog.setText(
                    enteringNumberOfLotsBuyDialog.text.let { text ->

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
                }
            })

            tradingPassword.isVisible = if (App.profile.tradingPasswordHash != null) {

                tradingPasswordTV.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?, start: Int, count: Int, after: Int
                    ) {

                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
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

    override fun cancel() {
        super.cancel()
        realTimeUpdateJob.cancel()
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

    fun startRealTimeUpdate(): Job =
        lifecycleScope.launch(Dispatchers.IO) {
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