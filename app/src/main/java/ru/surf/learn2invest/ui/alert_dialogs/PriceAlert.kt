package ru.surf.learn2invest.ui.alert_dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.LifecycleCoroutineScope
import ru.surf.learn2invest.databinding.PriceAlertDialogBinding
import ru.surf.learn2invest.ui.alert_dialogs.parent.CustomAlertDialog

class PriceAlert(
    context: Context,
    layoutInflater: LayoutInflater,
    private val lifecycleScope: LifecycleCoroutineScope
) : CustomAlertDialog(context = context) {

    private var binding =
        PriceAlertDialogBinding.inflate(layoutInflater)

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