package ru.surf.learn2invest.ui.alert_dialogs

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import ru.surf.learn2invest.databinding.PriceAlertDialogBinding
import ru.surf.learn2invest.noui.logs.Loher
import ru.surf.learn2invest.ui.alert_dialogs.parent.CustomAlertDialog

class PriceAlert(
    context: Context,
    val currentPrice: Float,
) : CustomAlertDialog(context = context) {

    private var binding =
        PriceAlertDialogBinding.inflate(LayoutInflater.from(context))

    override fun setCancelable(): Boolean {
        return true
    }

    override fun initListeners() {
        binding.apply {
            buttonCreatePriceAlertPriceAlertDialog.setOnClickListener {
                cancel()
            }

            buttonExitPriceAlertDialog.setOnClickListener {
                cancel()
            }

        }
    }

    override fun getDialogView(): View {
        return binding.root
    }

}