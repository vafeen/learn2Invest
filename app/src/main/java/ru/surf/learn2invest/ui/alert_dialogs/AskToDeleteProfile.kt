package ru.surf.learn2invest.ui.alert_dialogs


import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.databinding.AskToDeleteProfileDialogBinding
import ru.surf.learn2invest.main.Learn2InvestApp
import ru.surf.learn2invest.ui.alert_dialogs.parent.CustomAlertDialog


class AskToDeleteProfile(
    val context: Context,
    private val lifecycleScope: LifecycleCoroutineScope
) : CustomAlertDialog(context = context) {

    private var binding =
        AskToDeleteProfileDialogBinding.inflate(LayoutInflater.from(context))

    override fun setCancelable(): Boolean {
        return true
    }

    override fun initListeners() {
        binding.okDeleteAskToDeleteProfileDialog.setOnClickListener {

            lifecycleScope.launch(Dispatchers.IO) {
                val dao = Learn2InvestApp.mainDB.profileDao()

                val profileList = dao.getProfile()

                if (profileList.isNotEmpty()) {
                    dao.delete(
                        profileList[0]
                    )
                }
            }

            (context as Activity).finish()

            cancel()

            // TODO() // сюда нужно кинуть выход к экрану  Регистрации

        }

        binding.noDeleteAskToDeleteProfileDialog.setOnClickListener {
            cancel()
        }
    }

    override fun getDialogView(): View {
        return binding.root
    }

}





