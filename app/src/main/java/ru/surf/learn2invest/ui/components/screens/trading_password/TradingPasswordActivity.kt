package ru.surf.learn2invest.ui.components.screens.trading_password

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.R
import ru.surf.learn2invest.databinding.TradingPasswordActivityBinding
import ru.surf.learn2invest.noui.cryptography.PasswordHasher
import ru.surf.learn2invest.noui.cryptography.verifyTradingPassword
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.noui.database_components.DatabaseRepository.profile
import ru.surf.learn2invest.utils.hideKeyboard
import ru.surf.learn2invest.utils.isOk
import ru.surf.learn2invest.utils.isThisContains3NumbersOfEmpty
import ru.surf.learn2invest.utils.isThisContainsSequenceOrEmpty
import ru.surf.learn2invest.utils.no
import ru.surf.learn2invest.utils.ok
import ru.surf.learn2invest.utils.showKeyboard
import ru.surf.learn2invest.utils.updateProfile


class TradingPasswordActivity : AppCompatActivity() {
    private lateinit var binding: TradingPasswordActivityBinding
    private lateinit var viewModel: TradingPasswordActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this, R.color.white
                )
            )
        )
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
        binding = TradingPasswordActivityBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[TradingPasswordActivityViewModel::class.java]
        setContentView(binding.root)
        // TODO (Найдите пж норм иконки галочки и крестика
        ok = ContextCompat.getDrawable(this@TradingPasswordActivity, R.drawable.circle_plus)
        no = ContextCompat.getDrawable(this@TradingPasswordActivity, R.drawable.circle_minus)
        viewModel.action = when (intent.action.toString()) {
            TradingPasswordActivityActions.CreateTradingPassword.action -> {
                viewModel.apply {
                    actionName = ContextCompat.getString(
                        this@TradingPasswordActivity,
                        R.string.create_trading_password
                    )
                    mainButtonAction =
                        ContextCompat.getString(this@TradingPasswordActivity, R.string.create)
                }
                TradingPasswordActivityActions.CreateTradingPassword
            }

            TradingPasswordActivityActions.ChangeTradingPassword.action -> {
                viewModel.apply {
                    actionName = ContextCompat.getString(
                        this@TradingPasswordActivity,
                        R.string.change_trading_password
                    )
                    mainButtonAction =
                        ContextCompat.getString(this@TradingPasswordActivity, R.string.change)
                }
                TradingPasswordActivityActions.ChangeTradingPassword
            }

            TradingPasswordActivityActions.RemoveTradingPassword.action -> {
                viewModel.apply {
                    actionName = ContextCompat.getString(
                        this@TradingPasswordActivity,
                        R.string.remove_trading_password
                    )
                    mainButtonAction =
                        ContextCompat.getString(this@TradingPasswordActivity, R.string.remove)
                }
                TradingPasswordActivityActions.RemoveTradingPassword
            }

            else -> {
                // finish if action is not defined
                this@TradingPasswordActivity.finish()
                TradingPasswordActivityActions.CreateTradingPassword
            }
        }
        configureVisibilities()
        initListeners()
        checkPassword()
    }

    private fun configureVisibilities() {
        binding.apply {
            headerTradingPasswordActivity.text = viewModel.actionName
            buttonDoTrading.text = viewModel.mainButtonAction
            rulesTrpass1.text = ContextCompat.getString(
                this@TradingPasswordActivity, R.string.min_len_trading_password
            )
            rulesTrpass2.text =
                ContextCompat.getString(this@TradingPasswordActivity, R.string.not_more_than_2)
            rulesTrpass3.text =
                ContextCompat.getString(this@TradingPasswordActivity, R.string.no_seq_more_than_2)
            rulesTrpass4.text =
                ContextCompat.getString(this@TradingPasswordActivity, R.string.pass_match)
            rulesTrpass5.text =
                ContextCompat.getString(this@TradingPasswordActivity, R.string.old_pas_correct)
            when (viewModel.action) {

                TradingPasswordActivityActions.CreateTradingPassword -> {
                    textInputLayout1.isVisible = false
                    rulesTrpass5.isVisible = false
                    imageRule5.isVisible = false
                }

                TradingPasswordActivityActions.ChangeTradingPassword -> {
                    textInputLayout1.isVisible = true
                    rulesTrpass5.isVisible = true
                    imageRule5.isVisible = true
                }

                TradingPasswordActivityActions.RemoveTradingPassword -> {
                    textInputLayout1.isVisible = false
                    rulesTrpass1.isVisible = false
                    imageRule1.isVisible = false
                    rulesTrpass2.isVisible = false
                    imageRule2.isVisible = false
                    rulesTrpass3.isVisible = false
                    imageRule3.isVisible = false
                    rulesTrpass5.isVisible = false
                    imageRule5.isVisible = false
                }
            }
        }
    }

    private fun checkPassword() {
        binding.apply {
            imageRule1.setImageDrawable(
                if ((passwordEdit.text?.length ?: 0) >= 6) ok else no
            )

            imageRule2.setImageDrawable(
                if (passwordEdit.text.toString().isThisContains3NumbersOfEmpty()) no else ok
            )

            imageRule3.setImageDrawable(
                if (passwordEdit.text.toString().isThisContainsSequenceOrEmpty()) no else ok
            )

            imageRule4.setImageDrawable(
                if (viewModel.action != TradingPasswordActivityActions.RemoveTradingPassword) {
                    if (passwordEdit.text == passwordConfirm.text && passwordEdit.text?.isNotEmpty() == true) ok else no
                } else {
                    if (passwordEdit.text == passwordConfirm.text && verifyTradingPassword(
                            user = profile, password = passwordEdit.text.toString()
                        )
                    ) ok else no
                }
            )

            imageRule5.setImageDrawable(
                if (verifyTradingPassword(
                        user = profile, password = "${passwordLast.text}"
                    )
                ) ok else no
            )

            buttonDoTrading.isVisible = mainButtonIsVisible()

            buttonDoTrading.setOnClickListener {
                profile =
                    if (viewModel.action == TradingPasswordActivityActions.CreateTradingPassword || viewModel.action == TradingPasswordActivityActions.ChangeTradingPassword) {
                        profile.copy(
                            tradingPasswordHash = PasswordHasher(
                                firstName = profile.firstName, lastName = profile.lastName
                            ).passwordToHash(
                                password = "${passwordConfirm.text}"
                            )
                        )
                    } else profile.copy(tradingPasswordHash = null)
                updateProfile(lifecycleCoroutineScope = lifecycleScope)
                this@TradingPasswordActivity.finish()
            }

        }
    }

    private fun mainButtonIsVisible(): Boolean {
        binding.let {
            return when (viewModel.action) {
                TradingPasswordActivityActions.CreateTradingPassword -> {
                    it.imageRule1.isOk() && it.imageRule2.isOk() && it.imageRule3.isOk() && it.imageRule4.isOk()
                }

                TradingPasswordActivityActions.ChangeTradingPassword -> {
                    it.imageRule1.isOk() && it.imageRule2.isOk() && it.imageRule3.isOk() && it.imageRule4.isOk() && it.imageRule5.isOk()
                }

                TradingPasswordActivityActions.RemoveTradingPassword -> {
                    it.imageRule4.isOk()
                }
            }
        }
    }

    private fun initListeners() {
        binding.apply {
            when (viewModel.action) {
                TradingPasswordActivityActions.CreateTradingPassword -> {
                    passwordEdit
                }

                TradingPasswordActivityActions.ChangeTradingPassword -> {
                    passwordLast
                }

                TradingPasswordActivityActions.RemoveTradingPassword -> {
                    passwordEdit
                }
            }.apply {
                requestFocus()
                showKeyboard()
            }

            arrowBackTpactivity.setOnClickListener {
                this@TradingPasswordActivity.finish()
            }
            passwordLast.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?, start: Int, before: Int, count: Int
                ) {
                    checkPassword()
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })

            passwordLast.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    passwordLast.clearFocus()
                    passwordEdit.requestFocus()
                    return@OnKeyListener true
                }
                false
            })

            passwordEdit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?, start: Int, before: Int, count: Int
                ) {
                    checkPassword()
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })

            passwordEdit.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    passwordEdit.clearFocus()
                    passwordConfirm.requestFocus()
                    return@OnKeyListener true
                }
                false
            })

            passwordConfirm.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?, start: Int, before: Int, count: Int
                ) {
                    checkPassword()
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })

            passwordConfirm.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    passwordConfirm.clearFocus()
                    passwordConfirm.hideKeyboard(activity = this@TradingPasswordActivity)
                    return@OnKeyListener true
                }
                false
            })

            buttonDoTrading.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    DatabaseRepository.updateProfile(
                        profile.copy(
                            tradingPasswordHash = PasswordHasher(
                                firstName = profile.firstName, lastName = profile.lastName
                            ).passwordToHash(
                                passwordConfirm.text.toString()
                            )
                        )
                    )
                }.invokeOnCompletion {
                    this@TradingPasswordActivity.finish()
                }
            }
        }
    }
}
