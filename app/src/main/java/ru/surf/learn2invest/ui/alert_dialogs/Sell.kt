package ru.surf.learn2invest.ui.alert_dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import ru.surf.learn2invest.databinding.SellDialogBinding
import ru.surf.learn2invest.ui.alert_dialogs.parent.CustomAlertDialog

class Sell(
    context: Context,
) : CustomAlertDialog(context = context) {

    private var binding =
        SellDialogBinding.inflate(LayoutInflater.from(context))

    override fun setCancelable(): Boolean {
        return true
    }

    override fun initListeners() {
        binding.apply {
            buttonExitSellDialog.setOnClickListener {
                cancel()
            }

            buttonSellSellDialog.setOnClickListener {
                // TODO Логика продажи

                cancel()
            }
        }
    }

    override fun getDialogView(): View {
        return binding.root
    }

}