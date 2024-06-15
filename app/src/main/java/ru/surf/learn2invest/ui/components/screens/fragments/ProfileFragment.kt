package ru.surf.learn2invest.ui.components.screens.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.databinding.FragmentProfileBinding
import ru.surf.learn2invest.noui.cryptography.FingerprintAuthenticator
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.noui.database_components.entity.Profile
import ru.surf.learn2invest.ui.alert_dialogs.AskToDeleteProfile
import ru.surf.learn2invest.ui.alert_dialogs.reset_stats.ResetStats
import ru.surf.learn2invest.ui.components.screens.sign_in.SignINActivityActions
import ru.surf.learn2invest.ui.components.screens.sign_in.SignInActivity
import ru.surf.learn2invest.ui.components.screens.trading_password.TradingPasswordActivity
import ru.surf.learn2invest.ui.components.screens.trading_password.TradingPasswordActivityActions

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    private lateinit var context: Context
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        context = requireContext()


        return binding.root
    }

    override fun onResume() {
        super.onResume()

        initListeners()
    }

    private fun updateProfile(profile: Profile) {
        lifecycleScope.launch(Dispatchers.IO) {
          DatabaseRepository.insertAllProfile(profile)
        }
    }

    private fun initListeners() {
        App.profile.let { appProfile ->
            binding.also { fr ->

                fr.firstNameLastNameTV.text = appProfile.let { pr ->
                    "${pr.firstName}\n${pr.lastName}"
                }

                fr.biometryBtnSwitcher.isChecked = appProfile.biometry

                fr.confirmDealBtnSwitcher.isChecked = appProfile.tradingPasswordHash != null



                fr.deleteProfileTV.setOnClickListener {

                    AskToDeleteProfile(
                        context = context, lifecycleScope = lifecycleScope
                    ).initDialog().show()

                }


                fr.resetStatsBtn.setOnClickListener {

                    ResetStats(
                        context = context, lifecycleScope = lifecycleScope
                    ).initDialog().show()
                }


                fr.biometryBtn.setOnClickListener {

                    if (fr.biometryBtnSwitcher.isChecked) {
                        updateProfile(appProfile.copy(biometry = false))

                        fr.biometryBtnSwitcher.isChecked = false
                    } else {

                        FingerprintAuthenticator(
                            context = requireContext() as Activity,
                            lifecycleCoroutineScope = lifecycleScope
                        ).setSuccessCallback {
                            updateProfile(appProfile.copy(biometry = true))

                            fr.biometryBtnSwitcher.isChecked = true
                        }
                            .setDesignBottomSheet(
                                title = "Биометрия"
                            ).auth()

                    }
                }

                fr.changeTradingPasswordBtn.setOnClickListener {
                    startActivity(Intent(context, TradingPasswordActivity::class.java).apply {
                        action = TradingPasswordActivityActions.ChangeTradingPassword.action
                    })
                }

                fr.changeTradingPasswordBtn.isVisible = appProfile.tradingPasswordHash != null


                val intentFoxTradingPasswordActivityByConditions =

                    Intent(context, TradingPasswordActivity::class.java)
                        .apply {
                            action = when {

                                !fr.confirmDealBtnSwitcher.isChecked -> {
                                    TradingPasswordActivityActions.CreateTradingPassword.action
                                }

                                else -> {
                                    TradingPasswordActivityActions.RemoveTradingPassword.action
                                }

                            }
                        }

                fr.confirmDealBtn.setOnClickListener {

                    fr.confirmDealBtnSwitcher.isChecked = !fr.confirmDealBtnSwitcher.isChecked

                    fr.changeTradingPasswordBtn.isVisible = fr.confirmDealBtnSwitcher.isChecked

                    startActivity(intentFoxTradingPasswordActivityByConditions)
                }

                fr.confirmDealBtnSwitcher.setOnClickListener {

                    fr.changeTradingPasswordBtn.isVisible = fr.confirmDealBtnSwitcher.isChecked

                    startActivity(intentFoxTradingPasswordActivityByConditions)

                }







                fr.changePINBtn.setOnClickListener {
                    startActivity(Intent(context, SignInActivity::class.java).let {
                        it.action = SignINActivityActions.ChangingPIN.action

                        it
                    })
                }

            }
        }
    }

}