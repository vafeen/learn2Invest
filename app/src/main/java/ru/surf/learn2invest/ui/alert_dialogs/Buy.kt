package ru.surf.learn2invest.ui.alert_dialogs

import android.content.Context
import android.text.TextWatcher
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
    val lifecycleScope: LifecycleCoroutineScope
) : CustomAlertDialog(context = context) {

    private var binding =
        BuyDialogBinding.inflate(LayoutInflater.from(context))

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
//            editText1.addTextChangedListener(object : TextWatcher {
//                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                    editText2.setText(s) // Устанавливаем текст из первого поля во второе
//                }
//
//                override fun afterTextChanged(s: Editable?) {}
//            })

        }
    }

    override fun getDialogView(): View {
        return binding.root
    }

}