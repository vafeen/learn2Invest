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

        lifecycleScope.launch(Dispatchers.Main) {

            App.profile.collect { profList ->

                if (profList.isNotEmpty()) {

                    val profile = profList[App.idOfProfile]

                    binding.also { fr ->

                        fr.firstNameLastNameTV.text = profile.let { pr ->
                            "${pr.firstName}\n${pr.lastName}"
                        }

                        fr.notificationBtnSwitcher.isChecked = profile.notification

                        fr.biometryBtnSwitcher.isChecked = profile.biometry

                        fr.confirmDealBtnSwitcher.isChecked = profile.confirmDeal



                        fr.deleteProfileTV.setOnClickListener {

                            AskToDeleteProfile(
                                context = context, lifecycleScope = lifecycleScope
                            ).initDialog().show()

                        }

                        val resetStats = {

                            ResetStats(
                                context = context, lifecycleScope = lifecycleScope
                            ).initDialog().show()

                        }
                        fr.resetStatsBtn.setOnClickListener {
                            resetStats()
                        }
                        fr.resetStatsBtnArrow.setOnClickListener {
                            resetStats()
                        }

                        fr.notificationBtn.setOnClickListener {

                            fr.notificationBtnSwitcher.isChecked =

                                if (fr.notificationBtnSwitcher.isChecked) {

                                    updateProfile(profile.copy(notification = false))

                                    false
                                } else {

                                    updateProfile(profile.copy(notification = true))

                                    true
                                }
                        }

                        fr.notificationBtnSwitcher.setOnClickListener {
                            updateProfile(
                                profile.copy(
                                    notification =
                                    fr.notificationBtnSwitcher.isChecked
                                )
                            )
                        }




                        fr.biometryBtn.setOnClickListener {

                            fr.biometryBtnSwitcher.isChecked =

                                if (fr.biometryBtnSwitcher.isChecked) {

                                    updateProfile(profile.copy(biometry = false))

                                    false
                                } else {

                                    updateProfile(profile.copy(biometry = true))

                                    true
                                }
                        }

                        fr.biometryBtnSwitcher.setOnClickListener {
                            updateProfile(
                                profile.copy(
                                    biometry =
                                    fr.biometryBtnSwitcher.isChecked
                                )
                            )
                        }





                        fr.confirmDealBtn.setOnClickListener {

                            fr.confirmDealBtnSwitcher.isChecked =

                                if (fr.confirmDealBtnSwitcher.isChecked) {
                                    updateProfile(profile.copy(confirmDeal = false))

                                    false
                                } else {
                                    updateProfile(profile.copy(confirmDeal = true))

                                    true
                                }
                        }

                        fr.confirmDealBtnSwitcher.setOnClickListener {
                            updateProfile(profile.copy(confirmDeal = fr.confirmDealBtnSwitcher.isChecked))
                        }


                        val changePin = {
                            startActivity(Intent(context, SignInActivity::class.java).let {

                                it.action = SignINActivityActions.ChangingPIN.action

                                it
                            })
                        }

                        fr.changePINBtn.setOnClickListener {
                            changePin()
                        }

                        fr.changePINBtnArrow.setOnClickListener {
                            changePin()
                        }

                    }
                }
            }


        }
    }

}