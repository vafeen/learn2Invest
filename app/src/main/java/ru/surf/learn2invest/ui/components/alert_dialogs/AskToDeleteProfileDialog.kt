package ru.surf.learn2invest.ui.components.alert_dialogs


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.databinding.AskToDeleteProfileDialogBinding
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.ui.components.alert_dialogs.parent.CustomAlertDialog
import ru.surf.learn2invest.ui.main.MainActivity

/**
 * Диалог подтверждения удаления аккаунта
 * @param context [Контекст открытия диалога]
 * @param lifecycleScope [Scope для выполнения асинхронных операция]
 * @param supportFragmentManager [Менеджер открытия диалогов]
 */
class AskToDeleteProfileDialog(
    private val lifecycleScope: LifecycleCoroutineScope,
    private val context: Context,
    supportFragmentManager: FragmentManager
) : CustomAlertDialog(supportFragmentManager) {
    override val dialogTag: String = "askToDeleteProfile"
    private var binding =
        AskToDeleteProfileDialogBinding.inflate(LayoutInflater.from(context))

    override fun initListeners() {
        binding.ok.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                DatabaseRepository.clearAllTables()
            }
            cancel()
            (context as Activity).finish()
            context.startActivity(Intent(context, MainActivity::class.java))
        }

        binding.no.setOnClickListener {
            cancel()
        }
    }

    override fun getDialogView(): View {
        return binding.root
    }

}




