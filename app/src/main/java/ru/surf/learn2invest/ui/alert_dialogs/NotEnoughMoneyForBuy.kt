package ru.surf.learn2invest.ui.alert_dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import ru.surf.learn2invest.databinding.NotEnoughMoneyForBuyDialogBinding
import ru.surf.learn2invest.ui.alert_dialogs.parent.CustomAlertDialog

class NotEnoughMoneyForBuy(
    context: Context,
    layoutInflater: LayoutInflater
) : CustomAlertDialog(context = context) {

    private var binding =
        NotEnoughMoneyForBuyDialogBinding.inflate(layoutInflater)

    override fun setCancelable(): Boolean {
        return true
    }

    override fun initListeners() {
        binding.buttonExitWithoutMoney.setOnClickListener {
            cancel()
        }
    }

    override fun getDialogView(): View {
        return binding.root
    }

}