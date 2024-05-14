package ru.surf.learn2invest.ui.alert_dialogs


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import ru.surf.learn2invest.databinding.AskToDeleteProfileDialogBinding
import ru.surf.learn2invest.ui.alert_dialogs.parent.CustomAlertDialog


class AskToDeleteProfile(
    context: Context,
    layoutInflater: LayoutInflater
) : CustomAlertDialog(context = context) {

    private var binding =
        AskToDeleteProfileDialogBinding.inflate(layoutInflater)

    override fun setCancelable(): Boolean {
        return true
    }

    override fun initListeners() {
        binding.okDelete.setOnClickListener {

            TODO() // сюда нужно кинуть удаление профиля из базы данных
            // и выход к экрану  Регистрации

            cancel()
        }

        binding.noDelete.setOnClickListener {
            cancel()
        }
    }

    override fun getDialogView(): View {
        return binding.root
    }

}





