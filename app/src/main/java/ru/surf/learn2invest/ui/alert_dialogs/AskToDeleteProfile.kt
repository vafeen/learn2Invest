package ru.surf.learn2invest.ui.alert_dialogs


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.databinding.AskToDeleteProfileDialogBinding
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.ui.alert_dialogs.parent.CustomAlertDialog
import ru.surf.learn2invest.ui.main.MainActivity


class AskToDeleteProfile(
    private val lifecycleScope: LifecycleCoroutineScope,
    val dialogContext: Context,
    supportFragmentManager: FragmentManager
) : CustomAlertDialog(supportFragmentManager) {

    override val dialogTag: String = "askToDeleteProfile"


    private var binding =
        AskToDeleteProfileDialogBinding.inflate(LayoutInflater.from(dialogContext))

    override fun initListeners() {
        binding.okDeleteAskToDeleteProfileDialog.setOnClickListener {

            lifecycleScope.launch(Dispatchers.IO) {
                DatabaseRepository.deleteProfile(App.profile)
            }

            (dialogContext as Activity).finish()

            cancel()

            dialogContext.startActivity(Intent(dialogContext, MainActivity::class.java))
        }

        binding.noAskToDeleteProfileDialog.setOnClickListener {
            cancel()
        }

    }

    override fun getDialogView(): View {
        return binding.root
    }

}





