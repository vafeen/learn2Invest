package ru.surf.learn2invest.ui.alert_dialogs

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.databinding.RefillAccountDialogBinding
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.ui.alert_dialogs.parent.CustomAlertDialog

class RefillAccountDialog(
    context: Context, private val lifecycleScope: LifecycleCoroutineScope,
    supportFragmentManager: FragmentManager,
    private val onCloseCallback: () -> Unit
) : CustomAlertDialog(supportFragmentManager) {

    private var binding = RefillAccountDialogBinding.inflate(LayoutInflater.from(context))
    override val dialogTag: String = "refillAccount"
    private var enteredBalanceF: Float = 0f

    private fun changeVisibilityElements() {
        binding.apply {
            balanceClearRefillAccountDialog.isVisible =
                EditTextEnteringSumOfBalanceRefillAccountDialog.text.isNotEmpty()
            buttonRefillRefillAccountDialog.isVisible =
                EditTextEnteringSumOfBalanceRefillAccountDialog.text.toString().toFloatOrNull()
                    ?.let {
                        it > 0
                    } ?: false
        }

    }

    override fun initListeners() {
        binding.apply {
            changeVisibilityElements()
            balanceTextviewRefillAccountDialog.text = DatabaseRepository.profile.fiatBalance.getWithCurrency()
            buttonExitRefillAccountDialog.setOnClickListener {
                onCloseCallback()
                cancel()
            }
            balanceClearRefillAccountDialog.setOnClickListener {
                EditTextEnteringSumOfBalanceRefillAccountDialog.setText("")
            }
            EditTextEnteringSumOfBalanceRefillAccountDialog.addTextChangedListener(object :
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
                    if (EditTextEnteringSumOfBalanceRefillAccountDialog.hasFocus()) {
                        changeVisibilityElements()
                    }
                }
            })

            buttonRefillRefillAccountDialog.setOnClickListener {
                val enteredBalance =
                    binding.EditTextEnteringSumOfBalanceRefillAccountDialog.text.toString()
                        .toFloat()
                if (enteredBalance != 0f) {
                    enteredBalanceF = enteredBalance
                    lifecycleScope.launch(Dispatchers.IO) {
                        DatabaseRepository.profile.also {
                            DatabaseRepository.updateProfile(
                                it.copy(
                                    fiatBalance = it.fiatBalance + enteredBalance
                                )
                            )
                        }
                    }
                }
                balanceTextviewRefillAccountDialog.text =
                    DatabaseRepository.profile.fiatBalance.getWithCurrency()
                onCloseCallback()
                cancel()
            }
        }
    }

    override fun getDialogView(): View {
        return binding.root
    }
}