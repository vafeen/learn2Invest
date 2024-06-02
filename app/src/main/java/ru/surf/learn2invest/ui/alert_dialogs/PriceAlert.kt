package ru.surf.learn2invest.ui.alert_dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.databinding.PriceAlertDialogBinding
import ru.surf.learn2invest.noui.database_components.entity.PriceAlert
import ru.surf.learn2invest.noui.logs.Loher
import ru.surf.learn2invest.ui.alert_dialogs.parent.CustomAlertDialog

class PriceAlert(
    val context: Context,
    val currentPrice: Float,
    val lifecycleScope: LifecycleCoroutineScope
) : CustomAlertDialog(context = context) {

    private var binding = PriceAlertDialogBinding.inflate(LayoutInflater.from(context))

    private var alerts: Flow<List<PriceAlert>> = flowOf(listOf())

    override fun setCancelable(): Boolean {
        return true
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

    private fun initData() {
        lifecycleScope.launch(Dispatchers.Main) {
            alerts = App.mainDB.priceAlertDao().getAllAsFlow()
        }
    }


    override fun initDialog(): CustomAlertDialog {
        val dialog = super.initDialog() as ru.surf.learn2invest.ui.alert_dialogs.PriceAlert

        dialog.initData()

        return this
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
            buttonCreatePriceAlertPriceAlertDialog.setOnClickListener {

                // TODO логика создания диалога

                cancel()
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

}