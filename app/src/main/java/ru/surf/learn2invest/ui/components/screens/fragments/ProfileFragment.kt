package ru.surf.learn2invest.ui.components.screens.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.databinding.FragmentProfileBinding
import ru.surf.learn2invest.noui.database_components.entity.Profile
import ru.surf.learn2invest.ui.alert_dialogs.AskToDeleteProfile
import ru.surf.learn2invest.ui.alert_dialogs.ResetStats
import ru.surf.learn2invest.ui.components.screens.sign_in.SignINActivityActions
import ru.surf.learn2invest.ui.components.screens.sign_in.SignInActivity
import ru.surf.learn2invest.ui.components.screens.trading_password.TradingPasswordActivity

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    private lateinit var context: Context
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        context = requireContext()

        initListeners()

        return binding.root
    }


    private fun updateProfile(profile: Profile) {
        lifecycleScope.launch(Dispatchers.IO) {
            App.mainDB.profileDao().insertAll(profile)
        }
    }

    private fun initListeners() {
        App.profile.let { appProfile ->
            lifecycleScope.launch(Dispatchers.Main) {

                binding.also { fr ->

                    fr.firstNameLastNameTV.text = appProfile.let { pr ->
                        "${pr.firstName}\n${pr.lastName}"
                    }

                    fr.notificationBtnSwitcher.isChecked = appProfile.notification

                    fr.biometryBtnSwitcher.isChecked = appProfile.biometry

                    fr.confirmDealBtnSwitcher.isChecked = appProfile.confirmDeal



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


                    fr.notificationBtn.setOnClickListener {

                        fr.notificationBtnSwitcher.isChecked =

                            if (fr.notificationBtnSwitcher.isChecked) {

                                updateProfile(appProfile.copy(notification = false))

                                false
                            } else {

                                updateProfile(appProfile.copy(notification = true))

                                true
                            }
                    }




                    fr.biometryBtn.setOnClickListener {

                        fr.biometryBtnSwitcher.isChecked =

                            if (fr.biometryBtnSwitcher.isChecked) {

                                updateProfile(appProfile.copy(biometry = false))

                                false
                            } else {

                                updateProfile(appProfile.copy(biometry = true))

                                true
                            }
                    }


                    fr.confirmDealBtn.setOnClickListener {

                        fr.confirmDealBtnSwitcher.isChecked =

                            if (fr.confirmDealBtnSwitcher.isChecked) {
                                updateProfile(appProfile.copy(confirmDeal = false))

                                false
                            } else {
                                updateProfile(appProfile.copy(confirmDeal = true))

                                true
                            }
                    }

                    fr.confirmDealBtnSwitcher.setOnClickListener {
                        updateProfile(appProfile.copy(confirmDeal = fr.confirmDealBtnSwitcher.isChecked))
                    }

                    fr.changePINBtn.setOnClickListener {
                        startActivity(Intent(context, SignInActivity::class.java).let {

                            it.action = SignINActivityActions.ChangingPIN.action

                            it
                        })
                    }

                    fr.changeTradingPasswordBtn.setOnClickListener {

                        startActivity(Intent(context, TradingPasswordActivity::class.java))

                    }
                }
            }


        }
    }

}