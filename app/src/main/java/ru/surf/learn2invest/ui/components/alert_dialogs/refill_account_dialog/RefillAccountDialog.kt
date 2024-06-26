package ru.surf.learn2invest.ui.components.alert_dialogs.refill_account_dialog

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModelProvider
import ru.surf.learn2invest.databinding.RefillAccountDialogBinding
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.ui.components.alert_dialogs.getWithCurrency
import ru.surf.learn2invest.ui.components.alert_dialogs.parent.CustomAlertDialog

class RefillAccountDialog(
    context: Context, private val lifecycleScope: LifecycleCoroutineScope,
    supportFragmentManager: FragmentManager,
    private val onCloseCallback: () -> Unit
) : CustomAlertDialog(supportFragmentManager) {

    private var binding = RefillAccountDialogBinding.inflate(LayoutInflater.from(context))
    override val dialogTag: String = "refillAccount"
    private lateinit var viewModel: RefillAccountDialogViewModel
    private fun changeVisibilityElements() {
        viewModel = ViewModelProvider(this)[RefillAccountDialogViewModel::class.java]
        binding.apply {
            balanceClear.isVisible =
                EditTextEnteringSumOfBalance.text.isNotEmpty()
            buttonRefill.isVisible =
                EditTextEnteringSumOfBalance.text.toString().toFloatOrNull()
                    ?.let {
                        it > 0
                    } ?: false
        }

    }

    override fun initListeners() {
        binding.apply {
            changeVisibilityElements()
            balanceTextview.text =
                DatabaseRepository.profile.fiatBalance.getWithCurrency()
            buttonExit.setOnClickListener {
                onCloseCallback()
                cancel()
            }
            balanceClear.setOnClickListener {
                EditTextEnteringSumOfBalance.setText("")
            }
            EditTextEnteringSumOfBalance.addTextChangedListener(object :
                TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?, start: Int, before: Int, count: Int
                ) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (EditTextEnteringSumOfBalance.hasFocus()) {
                        changeVisibilityElements()
                    }
                }
            })

            buttonRefill.setOnClickListener {
                viewModel.enteredBalanceF =
                    binding.EditTextEnteringSumOfBalance.text.toString()
                        .toFloat()
                if (viewModel.enteredBalanceF != 0f) viewModel.refill()
                onCloseCallback()
                cancel()
            }
        }
    }

    override fun getDialogView(): View {
        return binding.root
    }
}