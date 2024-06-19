package ru.surf.learn2invest.ui.alert_dialogs.reset_stats

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleCoroutineScope
import ru.surf.learn2invest.databinding.ResetStatsDialogBinding
import ru.surf.learn2invest.ui.alert_dialogs.parent.CustomAlertDialog

class ResetStats(
    val dialogContext: Context,
    val lifecycleScope: LifecycleCoroutineScope,
    supportFragmentManager: FragmentManager,
) : CustomAlertDialog(supportFragmentManager) {

    private var binding: ResetStatsDialogBinding =
        ResetStatsDialogBinding.inflate(LayoutInflater.from(dialogContext))

    override val dialogTag: String = "restStats"

    override fun initListeners() {
        binding.apply {
            okResetStatsResetStatsDialog.setOnClickListener {

                resetStats(lifecycleScope = lifecycleScope)

                cancel()

                Toast.makeText(dialogContext, "Стата сброшена", Toast.LENGTH_LONG).show()
            }

            noReetStatsResetStatsDialog.setOnClickListener {
                cancel()
            }
        }
    }

    override fun getDialogView(): View = binding.root
}