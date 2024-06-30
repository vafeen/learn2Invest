package ru.surf.learn2invest.ui.components.alert_dialogs.refill_account_dialog

import android.content.Context
import android.content.DialogInterface
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.surf.learn2invest.R
import ru.surf.learn2invest.databinding.DialogRefillAccountBinding
import ru.surf.learn2invest.ui.components.alert_dialogs.parent.CustomBottomSheetDialog
import ru.surf.learn2invest.utils.getWithCurrency
import ru.surf.learn2invest.utils.tapOn

/**
 * Диалог пополнения баланса
 * @param dialogContext [Контекст открытия диалога]
 * @param supportFragmentManager [Менеджер открытия диалогов]
 * @param onCloseCallback [Callback по закрытию диалога]
 */
@AndroidEntryPoint
class RefillAccountDialog(
    val dialogContext: Context, private val onCloseCallback: () -> Unit,
) : CustomBottomSheetDialog() {

    private var binding = DialogRefillAccountBinding.inflate(LayoutInflater.from(dialogContext))
    override val dialogTag: String = "refillAccount"
    private val viewModel: RefillAccountDialogViewModel by viewModels()

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onCloseCallback()
    }

    override fun initListeners() {
        binding.apply {
            updateVisibilityMainItems()
            balanceTextview.text =
                viewModel.databaseRepository.profile.fiatBalance.getWithCurrency()
            balanceClear.setOnClickListener {
                TVEnteringSumOfBalance.text =
                    ContextCompat.getString(dialogContext, R.string.enter_sum)
            }

            buttonRefill.setOnClickListener {
                viewModel.enteredBalanceF = binding.TVEnteringSumOfBalance.text.toString().toFloat()
                if (viewModel.enteredBalanceF != 0f) viewModel.refill()
                onCloseCallback()
                cancel()
            }
            binding.apply {
                val numberButtons = listOf(
                    button0,
                    button1,
                    button2,
                    button3,
                    button4,
                    button5,
                    button6,
                    button7,
                    button8,
                    button9,
                )

                for (index in 0..numberButtons.lastIndex) {
                    numberButtons[index].setOnClickListener {
                        updatePin("$index")
                        (it as TextView).tapOn()
                    }
                }

                buttonDot.setOnClickListener {
                    updatePin(".")
                    (it as TextView).tapOn()
                }

                TVEnteringSumOfBalance.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun afterTextChanged(p0: Editable?) {
                        updateVisibilityMainItems()
                    }

                })

                backspace.setOnClickListener {
                    TVEnteringSumOfBalance.apply {
                        TVEnteringSumOfBalance.text =
                            if (text.length > 1) TVEnteringSumOfBalance.text.substring(
                                0, TVEnteringSumOfBalance.text.lastIndex
                            )
                            else ContextCompat.getString(dialogContext, R.string.enter_sum)
                    }
                }
            }
        }
    }

    private fun updatePin(s: String) {
        binding.apply {
            TVEnteringSumOfBalance.apply {
                text = if (text.toString() == ContextCompat.getString(
                        requireContext(), R.string.enter_sum
                    )
                ) {
                    s
                } else text.toString() + s
            }
        }
    }

    private fun updateVisibilityMainItems() {

        binding.apply {
            val isThisTemplate = TVEnteringSumOfBalance.text.toString() == ContextCompat.getString(
                requireContext(), R.string.enter_sum
            )
            buttonDot.isVisible =
                TVEnteringSumOfBalance.text.let { !isThisTemplate && !it.contains(".") }
            backspace.isVisible = !isThisTemplate
            buttonRefill.isVisible = TVEnteringSumOfBalance.text.let {
                it.isNotEmpty() && it[it.lastIndex].toString() != "." && !isThisTemplate && it.toString()
                    .toFloat() > 0f
            }
            balanceClear.isVisible = !isThisTemplate
            button0.visibility =
                if (TVEnteringSumOfBalance.text.toString() != "0") View.VISIBLE else View.INVISIBLE // здесь так нужно, иначе верстка сломается
        }
    }

    override fun getDialogView(): View {
        return binding.root
    }

    fun cancel() {
        dismiss()
    }
}