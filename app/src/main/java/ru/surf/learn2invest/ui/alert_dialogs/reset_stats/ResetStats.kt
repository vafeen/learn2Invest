package ru.surf.learn2invest.ui.alert_dialogs.reset_stats

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import ru.surf.learn2invest.R
import ru.surf.learn2invest.databinding.ResetStatsDialogBinding
import ru.surf.learn2invest.ui.alert_dialogs.parent.CustomAlertDialog

class ResetStats(
    val context: Context,
    val lifecycleScope: LifecycleCoroutineScope
) : CustomAlertDialog(context = context) {

    private var binding: ResetStatsDialogBinding =
        ResetStatsDialogBinding.inflate(LayoutInflater.from(context))

    override fun setCancelable(): Boolean = true

    override fun initListeners() {
        binding.apply {
            okResetStatsResetStatsDialog.setOnClickListener {

                resetStats(lifecycleScope = lifecycleScope)

                cancel()

                Toast.makeText(
                    context,
                    ContextCompat.getString(context, R.string.stat_reset),
                    Toast.LENGTH_LONG
                ).show()
            }

            noReetStatsResetStatsDialog.setOnClickListener {
                cancel()
            }
        }
    }

    override fun getDialogView(): View = binding.root
}