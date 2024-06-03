package ru.surf.learn2invest.ui.alert_dialogs


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.databinding.AskToDeleteProfileDialogBinding
import ru.surf.learn2invest.ui.alert_dialogs.parent.CustomAlertDialog
import ru.surf.learn2invest.ui.main.MainActivity


class AskToDeleteProfile(
    private val lifecycleScope: LifecycleCoroutineScope,
    val context: Context,
) : CustomAlertDialog(context = context) {

    private var binding =
        AskToDeleteProfileDialogBinding.inflate(LayoutInflater.from(context))

    override fun setCancelable(): Boolean {
        return true
    }

    override fun initListeners() {
        binding.okDeleteAskToDeleteProfileDialog.setOnClickListener {

            lifecycleScope.launch(Dispatchers.IO) {
                val dao = App.mainDB.profileDao()

                val profileList = App.profile
                    .first()

                if (profileList.isNotEmpty()) {
                    dao.delete(
                        profileList[App.idOfProfile]
                    )
                }
            }

            (context as Activity).finish()

            cancel()

            context.startActivity(Intent(context, MainActivity::class.java))
        }

        binding.noDeleteAskToDeleteProfileDialog.setOnClickListener {
            cancel()
        }

    }

    override fun getDialogView(): View {
        return binding.root
    }

}





