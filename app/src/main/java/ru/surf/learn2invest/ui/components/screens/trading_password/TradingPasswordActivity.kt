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
import ru.surf.learn2invest.R
import ru.surf.learn2invest.databinding.TradingPasswordActivityBinding
import ru.surf.learn2invest.noui.cryptography.verifyTradingPassword
import ru.surf.learn2invest.noui.database_components.DatabaseRepository.profile
import ru.surf.learn2invest.utils.Icons.error
import ru.surf.learn2invest.utils.Icons.ok
import ru.surf.learn2invest.utils.hideKeyboard
import ru.surf.learn2invest.utils.isOk
import ru.surf.learn2invest.utils.isThisContains3NumbersOfEmpty
import ru.surf.learn2invest.utils.isThisContainsSequenceOrEmpty
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
        if (!viewModel.initAction(intentAction = intent.action.toString(), context = this))
            this.finish()
        setContentView(binding.root)

        ok = ContextCompat.getDrawable(this@TradingPasswordActivity, R.drawable.ok_24dp)
        error = ContextCompat.getDrawable(this@TradingPasswordActivity, R.drawable.error_24dp)
        configureVisibilities()
        initListeners()
        checkPassword()
    }

    private fun configureVisibilities() {
        binding.apply {
            header.text = viewModel.actionName
            buttonDoTrading.text = viewModel.mainButtonAction
            rules1.text =
                ContextCompat.getString(
                    this@TradingPasswordActivity,
                    R.string.min_len_trading_password
                )
            rules2.text =
                ContextCompat.getString(
                    this@TradingPasswordActivity,
                    R.string.not_more_than_2
                )
            rules3.text =
                ContextCompat.getString(
                    this@TradingPasswordActivity,
                    R.string.no_seq_more_than_2
                )
            rules4.text =
                ContextCompat.getString(
                    this@TradingPasswordActivity,
                    R.string.pass_match
                )
            rules5.text =
                ContextCompat.getString(
                    this@TradingPasswordActivity,
                    R.string.old_pas_correct
                )
            when (viewModel.action) {

                TradingPasswordActivityActions.CreateTradingPassword -> {
                    textInputLayout1.isVisible = false
                    rules5.isVisible = false
                    imageRule5.isVisible = false
                }

                TradingPasswordActivityActions.ChangeTradingPassword -> {
                    textInputLayout1.isVisible = true
                    rules5.isVisible = true
                    imageRule5.isVisible = true
                }

                TradingPasswordActivityActions.RemoveTradingPassword -> {
                    textInputLayout1.isVisible = false
                    rules1.isVisible = false
                    imageRule1.isVisible = false
                    rules2.isVisible = false
                    imageRule2.isVisible = false
                    rules3.isVisible = false
                    imageRule3.isVisible = false
                    rules5.isVisible = false
                    imageRule5.isVisible = false
                }
            }
        }
    }

    private fun checkPassword() {

        binding.apply {
            imageRule1.setImageDrawable(
                if ((passwordEdit.text?.length ?: 0) >= 6) ok else error
            )

            imageRule2.setImageDrawable(
                if ("${passwordEdit.text}".isThisContains3NumbersOfEmpty()) error else ok
            )

            imageRule3.setImageDrawable(
                if (passwordEdit.text.toString().isThisContainsSequenceOrEmpty()) error else ok
            )

            imageRule4.setImageDrawable(
                when (viewModel.action) {
                    TradingPasswordActivityActions.RemoveTradingPassword -> {
                        if (("${passwordEdit.text}" == "${passwordConfirm.text}") && verifyTradingPassword(
                                user = profile, password = "${passwordEdit.text}"
                            )
                        ) ok else error
                    }

                    TradingPasswordActivityActions.ChangeTradingPassword -> {
                        if ("${passwordEdit.text}" == "${passwordConfirm.text}"
                            && passwordEdit.text?.isNotEmpty() == true
                        ) ok else error
                    }

                    TradingPasswordActivityActions.CreateTradingPassword -> {
                        if (("${passwordEdit.text}" == "${passwordConfirm.text}")
                            && passwordEdit.text?.isNotEmpty() == true
                        ) ok else error
                    }
                }
            )

            imageRule5.setImageDrawable(
                if (verifyTradingPassword(
                        user = profile, password = "${passwordLast.text}"
                    )
                ) ok else error
            )

            buttonDoTrading.isVisible = mainButtonIsVisible()
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

            arrowBack.setOnClickListener {
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
                    passwordLast.apply {
                        hint = if (text?.isEmpty() != true) ContextCompat.getString(
                            this@TradingPasswordActivity,
                            R.string.enter_last_trading_password
                        ) else ""
                    }
                    checkPassword()
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
            passwordEdit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?, start: Int, before: Int, count: Int
                ) {
                    passwordEdit.apply {
                        hint = if (text?.isEmpty() != true) ContextCompat.getString(
                            this@TradingPasswordActivity,
                            R.string.enter_last_trading_password
                        ) else ""
                    }
                    checkPassword()
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
            passwordConfirm.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?, start: Int, before: Int, count: Int
                ) {
                    passwordConfirm.apply {
                        hint = if (text?.isEmpty() != true) ContextCompat.getString(
                            this@TradingPasswordActivity,
                            R.string.enter_last_trading_password
                        ) else ""
                    }
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
            passwordEdit.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    passwordEdit.clearFocus()
                    passwordConfirm.requestFocus()
                    return@OnKeyListener true
                }
                false
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
                viewModel.saveTradingPassword(password = "${passwordConfirm.text}")
                updateProfile(lifecycleCoroutineScope = lifecycleScope)
                this@TradingPasswordActivity.finish()
            }
        }
    }
}
