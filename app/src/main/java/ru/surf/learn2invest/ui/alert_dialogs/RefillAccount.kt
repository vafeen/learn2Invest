package ru.surf.learn2invest.ui.alert_dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.databinding.RefillAccountDialogBinding
import ru.surf.learn2invest.main.Learn2InvestApp
import ru.surf.learn2invest.noui.database_components.entity.Profile
import ru.surf.learn2invest.ui.alert_dialogs.parent.CustomAlertDialog

class RefillAccount(
    val context: Context,
    layoutInflater: LayoutInflater,
    private val lifecycleScope: LifecycleCoroutineScope
) : CustomAlertDialog(context = context) {

    private var binding =
        RefillAccountDialogBinding.inflate(layoutInflater)

    override fun setCancelable(): Boolean {
        return true
    }

    override fun initListeners() {
        val profileDao = Learn2InvestApp.mainDB.profileDao()

        var profile: Profile? = null

        lifecycleScope.launch(Dispatchers.IO) {

            profileDao.getProfile().let {

                if (it.isNotEmpty()) {
                    profile = it[0]
                }
            }
        }

        binding.buttonExitRefillAccountDialog.setOnClickListener {
            cancel()
        }

        binding.EditTextEnteringSumOfBalanceRefillAccountDialog.setText("0")

        binding.buttonRefillRefillAccountDialog.setOnClickListener {

            val enteredBalance =
                binding.EditTextEnteringSumOfBalanceRefillAccountDialog.text.toString().toFloat()

            if (enteredBalance != 0f) {

                lifecycleScope.launch(Dispatchers.IO) {

                    profile?.also {
                        profileDao.insertAll(
                            it.copy(
                                //                                assetBalance = profile.assetBalance + enteredBalance,
                                //                                fiatBalance = profile.assetBalance + enteredBalance
                                // TODO(Володь, какой тут баланс профиля пополнять, и почему все балансы блин интовые, если должны быть вещественными?????????????????)
                            )
                        )
                    }

                }

            }

            cancel()
        }

//        TODO(Помогите, я не пойму, как ту взаимодействовать с иконкой(((( ) Сюда нужно добавить очистку поля по клику на эту иконку

        binding.balanceTextviewRefillAccountDialog.text =
            "${
                profile?.fiatBalance // TODO(Какой тут баланс из профиля?)
                    ?: "balance error"
            }"

    }

    override fun getDialogView(): View {
        return binding.root
    }

}