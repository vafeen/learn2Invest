package ru.surf.learn2invest.ui.alert_dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.databinding.BuyDialogBinding
import ru.surf.learn2invest.main.Learn2InvestApp
import ru.surf.learn2invest.ui.alert_dialogs.parent.CustomAlertDialog

class Buy(
    context: Context,
    layoutInflater: LayoutInflater,
    val lifecycleScope: LifecycleCoroutineScope
) : CustomAlertDialog(context = context) {

    private var binding =
        BuyDialogBinding.inflate(layoutInflater)

    override fun setCancelable(): Boolean {
        return true
    }

    override fun initListeners() {
        binding.apply {
            lifecycleScope.launch(Dispatchers.IO) {
                val profileList = Learn2InvestApp.mainDB.profileDao().getProfile()
                balanceTextviewBuyDialog.text = if (profileList.isNotEmpty()) {
                    profileList[0].fiatBalance.toString()
                    // TODO(Володь, то, что выше, нужно исправить, т.к. я хз, что тут за баланс)
                } else {
                    "error balance"
                }
            }

            buttonExitBuyDialog.setOnClickListener {
                cancel()
            }

            buttonBuyBuyDialog.setOnClickListener {
                cancel()
            }


        }
    }

    override fun getDialogView(): View {
        return binding.root
    }

}