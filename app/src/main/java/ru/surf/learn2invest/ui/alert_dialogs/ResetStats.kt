package ru.surf.learn2invest.ui.alert_dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.LifecycleCoroutineScope
import ru.surf.learn2invest.databinding.ResetStatsDialogBinding
import ru.surf.learn2invest.ui.alert_dialogs.parent.CustomAlertDialog

class ResetStats(
    context: Context,
    val lifecycleScope: LifecycleCoroutineScope
) : CustomAlertDialog(context = context) {

    private var binding: ResetStatsDialogBinding =
        ResetStatsDialogBinding.inflate(LayoutInflater.from(context))

    override fun setCancelable(): Boolean = true

    override fun initListeners() {
        binding.apply {
            okResetStatsResetStatsDialog.setOnClickListener {

                //TODO логика сброса статистики

                cancel()
            }

            noReetStatsResetStatsDialog.setOnClickListener {
                cancel()
            }
        }
    }

    override fun getDialogView(): View = binding.root
}