package ru.surf.learn2invest.ui.components.screens.fragments.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.R
import ru.surf.learn2invest.databinding.FragmentProfileBinding
import ru.surf.learn2invest.noui.cryptography.FingerprintAuthenticator
import ru.surf.learn2invest.noui.database_components.entity.Profile
import ru.surf.learn2invest.ui.components.alert_dialogs.ask_to_delete_profile.AskToDeleteProfileDialog
import ru.surf.learn2invest.ui.components.alert_dialogs.reset_stats.ResetStatsDialog
import ru.surf.learn2invest.ui.components.screens.sign_in.SignINActivityActions
import ru.surf.learn2invest.ui.components.screens.sign_in.SignInActivity
import ru.surf.learn2invest.ui.components.screens.trading_password.TradingPasswordActivity
import ru.surf.learn2invest.ui.components.screens.trading_password.TradingPasswordActivityActions
import ru.surf.learn2invest.utils.isBiometricAvailable

/**
 * Фрагмент профиля в [HostActivity][ru.surf.learn2invest.ui.components.screens.host.HostActivity]
 */
@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    val viewModel: ProfileFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        initListeners()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.databaseRepository.profile.apply {
            binding.apply {
                biometryBtnSwitcher.isChecked = biometry
                confirmDealBtnSwitcher.isChecked = tradingPasswordHash != null
                changeTradingPasswordBtn.isVisible = tradingPasswordHash != null
            }
        }
    }

    private fun intentFoxTradingPasswordActivityByConditions(): Intent =
        Intent(requireContext(), TradingPasswordActivity::class.java).apply {
            action = when {
                binding.confirmDealBtnSwitcher.isChecked -> {
                    TradingPasswordActivityActions.CreateTradingPassword.action
                }

                else -> {
                    TradingPasswordActivityActions.RemoveTradingPassword.action
                }
            }
        }


    private fun updateProfile(profile: Profile) {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.databaseRepository.insertAllProfile(profile)
        }
    }

    private fun initListeners() {
        viewModel.databaseRepository.profile.let { profile ->
            binding.also { fr ->
                fr.firstNameLastNameTV.text = profile.let { pr ->
                    "${pr.firstName}\n${pr.lastName}"
                }
                binding.biometryBtn.isVisible = isBiometricAvailable(context = requireContext())
                fr.deleteProfileTV.setOnClickListener {

                    AskToDeleteProfileDialog(
                        context = requireContext(),
                        lifecycleScope = lifecycleScope,
                        supportFragmentManager = parentFragmentManager
                    ).show()

                }

                fr.resetStatsBtn.setOnClickListener {

                    ResetStatsDialog(
                        context = requireContext(),
                        lifecycleScope = lifecycleScope,
                        supportFragmentManager = parentFragmentManager
                    ).show()
                }

                fr.biometryBtn.setOnClickListener {

                    if (fr.biometryBtnSwitcher.isChecked) {
                        updateProfile(profile.copy(biometry = false))

                        fr.biometryBtnSwitcher.isChecked = false
                    } else {
                        FingerprintAuthenticator(
                            context = requireContext() as Activity,
                            lifecycleCoroutineScope = lifecycleScope
                        ).setSuccessCallback {
                            updateProfile(profile.copy(biometry = true))

                            fr.biometryBtnSwitcher.isChecked = true
                        }.setDesignBottomSheet(
                            title = ContextCompat.getString(requireContext(), R.string.biometry)
                        ).auth()

                    }
                }

                fr.changeTradingPasswordBtn.setOnClickListener {
                    startActivity(
                        Intent(
                            requireContext(),
                            TradingPasswordActivity::class.java
                        ).apply {
                            action = TradingPasswordActivityActions.ChangeTradingPassword.action
                        })
                }

                fr.confirmDealBtn.setOnClickListener {
                    fr.confirmDealBtnSwitcher.isChecked = !fr.confirmDealBtnSwitcher.isChecked
                    fr.changeTradingPasswordBtn.isVisible = fr.confirmDealBtnSwitcher.isChecked
                    startActivity(intentFoxTradingPasswordActivityByConditions())
                }

                fr.confirmDealBtnSwitcher.setOnClickListener {
                    fr.changeTradingPasswordBtn.isVisible = fr.confirmDealBtnSwitcher.isChecked
                    startActivity(intentFoxTradingPasswordActivityByConditions())
                }

                fr.changePINBtn.setOnClickListener {
                    startActivity(Intent(requireContext(), SignInActivity::class.java).let {
                        it.action = SignINActivityActions.ChangingPIN.action

                        it
                    })
                }

            }
        }
    }
}