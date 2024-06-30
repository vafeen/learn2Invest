package ru.surf.learn2invest.ui.components.screens.fragments.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import ru.surf.learn2invest.noui.database_components.entity.Profile
import ru.surf.learn2invest.ui.components.alert_dialogs.parent.SimpleDialog
import ru.surf.learn2invest.ui.components.screens.sign_in.SignINActivityActions
import ru.surf.learn2invest.ui.components.screens.sign_in.SignInActivity
import ru.surf.learn2invest.ui.components.screens.trading_password.TradingPasswordActivity
import ru.surf.learn2invest.ui.components.screens.trading_password.TradingPasswordActivityActions
import ru.surf.learn2invest.ui.main.MainActivity
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
                binding.biometryBtn.isVisible =
                    isBiometricAvailable(activity = activity as AppCompatActivity)
                fr.deleteProfileTV.setOnClickListener {
                    SimpleDialog(
                        context = requireContext(),
                        messageRes = R.string.asking_to_delete_profile,
                        positiveButtonTitleRes = R.string.yes_exactly,
                        negativeButtonTitleRes = R.string.no,
                        isCancelable = true,
                        onPositiveButtonClick = {
                            lifecycleScope.launch(Dispatchers.IO) {
                                viewModel.databaseRepository.clearAllTables()
                            }
                            activity?.finish()
                            activity?.startActivity(
                                Intent(
                                    context,
                                    MainActivity::class.java
                                )
                            )
                        },
                        onNegativeButtonClick = {}
                    ).show()
                }

                fr.resetStatsBtn.setOnClickListener {
                    SimpleDialog(
                        context = requireContext(),
                        messageRes = R.string.reset_stats,
                        positiveButtonTitleRes = R.string.yes_exactly,
                        negativeButtonTitleRes = R.string.no,
                        isCancelable = true,
                        onPositiveButtonClick = {
                            val savedProfile = viewModel.databaseRepository.profile.copy(
                                fiatBalance = 0f,
                                assetBalance = 0f
                            )
                            lifecycleScope.launch(Dispatchers.IO) {
                                viewModel.databaseRepository.apply {
                                    clearAllTables()
                                    insertAllProfile(savedProfile)
                                }
                            }
                            Toast.makeText(
                                context,
                                context?.let { it1 ->
                                    ContextCompat.getString(
                                        it1,
                                        R.string.stat_reset
                                    )
                                },
                                Toast.LENGTH_LONG
                            ).show()
                        },
                        onNegativeButtonClick = {}
                    ).show()
                }

                fr.biometryBtn.setOnClickListener {

                    if (fr.biometryBtnSwitcher.isChecked) {
                        updateProfile(profile.copy(biometry = false))

                        fr.biometryBtnSwitcher.isChecked = false
                    } else {
                        viewModel.fingerprintAuthenticator.setSuccessCallback {
                            updateProfile(profile.copy(biometry = true))

                            fr.biometryBtnSwitcher.isChecked = true
                        }.setDesignBottomSheet(
                            title = ContextCompat.getString(
                                requireContext(),
                                R.string.biometry
                            ),
                            cancelText = ContextCompat.getString(
                                requireContext(),
                                R.string.cancel
                            ),
                        ).auth(
                            lifecycleCoroutineScope = lifecycleScope,
                            activity = activity as AppCompatActivity
                        )

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