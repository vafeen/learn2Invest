package ru.surf.learn2invest.ui.alert_dialogs.price_alert

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.databinding.PriceAlertDialogBinding
import ru.surf.learn2invest.main.Learn2InvestApp
import ru.surf.learn2invest.noui.database_components.entity.PriceAlert
import ru.surf.learn2invest.noui.logs.Loher
import ru.surf.learn2invest.ui.alert_dialogs.parent.CustomAlertDialog


class PriceAlertDialog(
    val context: Context,
    val currentPrice: Float,
    val lifecycleScope: LifecycleCoroutineScope
) : CustomAlertDialog(context = context) {

    private var binding = PriceAlertDialogBinding.inflate(LayoutInflater.from(context))

    private var alerts: List<PriceAlert> = listOf()
    var alertAdapter = PriceAlertAdapter(
        context = context,
        data = alerts,
        lifecycleScope = lifecycleScope,
        mainBinding = binding
    ) {
        notifyChangingData()
    }

    private fun notifyChangingData() {
        alertAdapter.notifyDataSetChanged()
    }

    companion object {

        var currentAlert: Int? = null
    }

    override fun setCancelable(): Boolean {
        return false
    }

    private fun changePriceByPercent(onChangedPercent: Editable?) {
        binding.pricePriceAlertDialog.setText("${
            "$onChangedPercent".toDoubleOrNull().let {
                if (it != null) {
                    currentPrice * (1 + it / 100)
                } else {
                    ""
                }
            }
        }")
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun initDialog(): CustomAlertDialog {
        val dialog =
            super.initDialog() as PriceAlertDialog

        lifecycleScope.launch(Dispatchers.Main) {

            Learn2InvestApp.mainDB.priceAlertDao().getAll().collect {
                alertAdapter.alerts = it
                alertAdapter.notifyDataSetChanged()
            }
        }
        return dialog

    }

    private fun changeVisibilityIcons(
        price: Boolean = binding.pricePriceAlertDialog.text.isNotEmpty(),
        percent: Boolean = binding.priceInPercentPriceAlertDialog.text.isNotEmpty(),
        comment: Boolean = binding.commentAlertDialog.text.isNotEmpty()
    ) {
        binding.apply {
            priceClearAlertDialog.isVisible = price

            priceInPercentClearAlertDialog.isVisible = percent

            commentClearAlertDialog.isVisible = comment
        }
    }

    private fun changePercentByPrice(newPrice: Editable?) {
        binding.priceInPercentPriceAlertDialog.setText(
            "${"$newPrice".toDoubleOrNull() ?: ""}"
        )

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initListeners() {

        changeVisibilityIcons()

        binding.apply {

            binding.priceAlertListRecyclerView.adapter = alertAdapter

            buttonCreatePriceAlertPriceAlertDialog.setOnClickListener {

                lifecycleScope.launch(Dispatchers.IO) {
                    Learn2InvestApp.mainDB.priceAlertDao().insertAll(
                        PriceAlert(
                            symbol = "",
                            coinPrice = pricePriceAlertDialog.text.toString().toFloat().toInt(),
                            changePercent24Hr = priceInPercentPriceAlertDialog.text.toString()
                                .toFloat().toInt(),
                            comment = commentAlertDialog.text.toString()
                        )
                    )
                }

            }

            buttonExitPriceAlertDialog.setOnClickListener {
                cancel()
            }

            pricePriceAlertDialog.addTextChangedListener(object : TextWatcher {
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
                    changeVisibilityIcons()
                }

                override fun afterTextChanged(s: Editable?) {
                    if (pricePriceAlertDialog.hasFocus()) {
                        Loher.d("price")

                        changePercentByPrice(newPrice = s)

                    }
                }
            })

            priceInPercentPriceAlertDialog.addTextChangedListener(object : TextWatcher {
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
                    changeVisibilityIcons()
                }

                override fun afterTextChanged(s: Editable?) {
                    if (priceInPercentPriceAlertDialog.hasFocus()) {
                        Loher.d("percent")

                        changePriceByPercent(onChangedPercent = s)
                    }

                }
            })

            commentAlertDialog.addTextChangedListener(object : TextWatcher {
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
                    changeVisibilityIcons()
                }

                override fun afterTextChanged(s: Editable?) {
                    if (priceInPercentPriceAlertDialog.hasFocus()) {
                        Loher.d("comment")
                    }

                }
            })


            priceClearAlertDialog.setOnClickListener {
                pricePriceAlertDialog.setText("")
            }

            priceInPercentClearAlertDialog.setOnClickListener {
                priceInPercentPriceAlertDialog.setText("")
            }


            commentClearAlertDialog.setOnClickListener {
                commentAlertDialog.setText("")
            }

        }
    }

    override fun getDialogView(): View {
        return binding.root
    }

    override fun cancel() {
        currentAlert = null
        super.cancel()
    }

}