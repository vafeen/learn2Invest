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
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.databinding.RefillAccountDialogBinding
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.ui.alert_dialogs.parent.CustomAlertDialog

class RefillAccount(
    context: Context, private val lifecycleScope: LifecycleCoroutineScope,
    supportFragmentManager: FragmentManager
) : CustomAlertDialog(supportFragmentManager) {
    private var binding = RefillAccountDialogBinding.inflate(LayoutInflater.from(context))
    override val dialogTag: String = "refillAccount"

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

            buttonExitRefillAccountDialog.setOnClickListener {
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
                        //Loher.d("entering sum")

                        changeVisibilityElements()
                    }
                }
            })

            buttonRefillRefillAccountDialog.setOnClickListener {

                val enteredBalance =
                    binding.EditTextEnteringSumOfBalanceRefillAccountDialog.text.toString()
                        .toFloat()

                if (enteredBalance != 0f) {

                    lifecycleScope.launch(Dispatchers.IO) {

                        App.profile.also {

                            DatabaseRepository.updateProfile(
                                it.copy(
                                    fiatBalance = it.fiatBalance + enteredBalance,
                                    assetBalance = it.assetBalance + enteredBalance
                                )
                            )
                        }
                    }

                }

                cancel()
            }

            balanceTextviewRefillAccountDialog.text = App.profile.fiatBalance.getWithCurrency()

        }
    }

    override fun getDialogView(): View {
        return binding.root
    }

}