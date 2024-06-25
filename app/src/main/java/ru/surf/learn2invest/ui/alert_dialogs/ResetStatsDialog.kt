package ru.surf.learn2invest.ui.alert_dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.databinding.ResetStatsDialogBinding
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.ui.alert_dialogs.parent.CustomAlertDialog

class ResetStatsDialog(
    private val dialogContext: Context,
    private val lifecycleScope: LifecycleCoroutineScope,
    supportFragmentManager: FragmentManager,
) : CustomAlertDialog(supportFragmentManager) {
    private var binding: ResetStatsDialogBinding =
        ResetStatsDialogBinding.inflate(LayoutInflater.from(dialogContext))
    override val dialogTag: String = "restStats"

    override fun initListeners() {
        binding.apply {
            ok.setOnClickListener {
                val profile = DatabaseRepository.profile.copy(
                    fiatBalance = 0f,
                    assetBalance = 0f
                )
                lifecycleScope.launch(Dispatchers.IO) {
                    DatabaseRepository.apply {
                        clearAllTables()
                        insertAllProfile(
                            profile
                        )
                    }
                }
                cancel()
                Toast.makeText(dialogContext, "Стата сброшена", Toast.LENGTH_LONG).show()
            }

            no.setOnClickListener {
                cancel()
            }
        }
    }

    override fun getDialogView(): View = binding.root
}