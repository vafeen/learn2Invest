package ru.surf.learn2invest.ui.components.screens.trading_password

import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.R
import ru.surf.learn2invest.app.App.Companion.profile
import ru.surf.learn2invest.databinding.TradingPasswordActivityBinding
import ru.surf.learn2invest.noui.cryptography.PasswordHasher
import ru.surf.learn2invest.noui.cryptography.verifyTradingPassword
import ru.surf.learn2invest.noui.database_components.DatabaseRepository


class TradingPasswordActivity : AppCompatActivity() {


    private lateinit var binding: TradingPasswordActivityBinding


    private lateinit var action: TradingPasswordActivityActions
    private lateinit var actionName: String
    private lateinit var mainButtonAction: String

    private var ok: Drawable? = null

    private var no: Drawable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = TradingPasswordActivityBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // TODO (Найдите пж норм иконки галочки и крестика
        ok = ContextCompat.getDrawable(this@TradingPasswordActivity, R.drawable.circle_plus)

        no = ContextCompat.getDrawable(this@TradingPasswordActivity, R.drawable.circle_minus)


        when (intent.action.toString()) {
            TradingPasswordActivityActions.ChangeTradingPassword.action -> {
                action = TradingPasswordActivityActions.ChangeTradingPassword

                actionName = ContextCompat.getString(this, R.string.change_trading_password)

                mainButtonAction = ContextCompat.getString(this, R.string.change)
            }

            TradingPasswordActivityActions.CreateTradingPassword.action -> {
                action = TradingPasswordActivityActions.CreateTradingPassword

                actionName = ContextCompat.getString(this, R.string.create_trading_password)

                mainButtonAction = ContextCompat.getString(this, R.string.create)
            }

            TradingPasswordActivityActions.RemoveTradingPassword.action -> {
                action = TradingPasswordActivityActions.RemoveTradingPassword

                actionName = ContextCompat.getString(this, R.string.remove_trading_password)

                mainButtonAction = ContextCompat.getString(this, R.string.remove)
            }

            else -> {
                // finish if action is not defined
                this@TradingPasswordActivity.finish()

                Log.e("TradingPasswordActivity", "FATAL: Intent.action is not defined ")

            }
        }

        configureVisibilities()

        initListeners()

        checkPassword()
    }

    private fun configureVisibilities() {

        binding.apply {

            headerTradingPasswordActivity.text = actionName

            buttonDoTrading.text = mainButtonAction

            rulesTrpass1.text = resources.getText(R.string.min_len_trading_password)

            rulesTrpass2.text = resources.getText(R.string.not_more_than_2)

            rulesTrpass3.text = resources.getText(R.string.no_seq_more_than_2)

            rulesTrpass4.text = resources.getText(R.string.pass_match)

            rulesTrpass5.text = resources.getText(R.string.old_pas_correct)

            when (action) {

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
                if ((passwordEdit.text?.length ?: 0) >= 6) {
                    ok
                } else {
                    no
                }
            )

            imageRule2.setImageDrawable(
                if (passwordEdit.text.toString().isThisContains3NumbersOfEmpty()) {
                    no
                } else {
                    ok
                }
            )

            imageRule3.setImageDrawable(
                if (passwordEdit.text.toString().isThisContainsSequenceOrEmpty()) {
                    no
                } else {
                    ok
                }
            )

            imageRule4.setImageDrawable(
                if (action != TradingPasswordActivityActions.RemoveTradingPassword) {
                    if (passwordEdit.text.toString() == passwordConfirm.text.toString() && passwordEdit.text?.isNotEmpty() == true) {
                        ok
                    } else {
                        no
                    }
                } else {
                    if (passwordEdit.text.toString() == passwordConfirm.text.toString() && verifyTradingPassword(
                            user = profile, password = passwordEdit.text.toString()
                        )
                    ) {
                        ok
                    } else {
                        no
                    }
                }
            )

            imageRule5.setImageDrawable(
                if (profile.let {
                        val x = verifyTradingPassword(
                            user = profile, password = "${passwordLast.text}"
                        )

                        Log.d("password", "verify? - $x")

                        x
                    }) {
                    ok
                } else {
                    no
                }
            )

            buttonDoTrading.isVisible = mainButtonIsVisible()

            buttonDoTrading.setOnClickListener {
                when (action) {

                    TradingPasswordActivityActions.CreateTradingPassword -> {
                        profile = profile.copy(
                            tradingPasswordHash = PasswordHasher(
                                firstName = profile.firstName, lastName = profile.lastName
                            ).passwordToHash(
                                password = "${passwordConfirm.text}"
                            )
                        )

                        updateProfile()
                    }

                    TradingPasswordActivityActions.ChangeTradingPassword -> {
                        profile = profile.copy(
                            tradingPasswordHash = PasswordHasher(
                                firstName = profile.firstName, lastName = profile.lastName
                            ).passwordToHash(
                                password = "${passwordConfirm.text}"
                            )
                        )

                        updateProfile()
                    }

                    TradingPasswordActivityActions.RemoveTradingPassword -> {
                        profile = profile.copy(tradingPasswordHash = null)

                        updateProfile()
                    }

                }
                this@TradingPasswordActivity.finish()
            }

        }
    }

    private fun ImageView.isOk(): Boolean = this.drawable == ok


    private fun mainButtonIsVisible(): Boolean {

        return when (action) {
//            rulesTrpass1.text = "Пароль должен состоять минимум из 6 цифр"
//
//                    rulesTrpass2.text = "Не более двух одинаковых цифр рядом"
//
//                rulesTrpass3.text = "Нет последовательности более трех цифр"
//
//            rulesTrpass4.text = "Пароли совпадают"
//
//                    rulesTrpass5.text = "Старый пароль верен"
            TradingPasswordActivityActions.CreateTradingPassword -> {
                binding.let {

                    it.imageRule1.isOk() && it.imageRule2.isOk() && it.imageRule3.isOk() && it.imageRule4.isOk()

                }
            }


            TradingPasswordActivityActions.ChangeTradingPassword -> {

                binding.let {
                    it.imageRule1.isOk() && it.imageRule2.isOk() && it.imageRule3.isOk() && it.imageRule4.isOk() && it.imageRule5.isOk()

                }
            }

            TradingPasswordActivityActions.RemoveTradingPassword -> {
                binding.imageRule4.isOk()
            }

        }
    }

    private fun View.showKeyboard() = ViewCompat.getWindowInsetsController(this)
        ?.show(WindowInsetsCompat.Type.ime())

    private fun View.hideKeyboard() {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun initListeners() {

        binding.apply {
            when (action) {
                TradingPasswordActivityActions.CreateTradingPassword -> {
                    passwordEdit.apply {
                        requestFocus()

                        showKeyboard()
                    }
                }

                TradingPasswordActivityActions.ChangeTradingPassword -> {
                    passwordLast.apply {
                        requestFocus()

                        showKeyboard()
                    }
                }

                TradingPasswordActivityActions.RemoveTradingPassword -> {
                    passwordEdit.apply {
                        requestFocus()

                        showKeyboard()
                    }
                }
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

            passwordLast.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    // Ваш код для обработки нажатия клавиши "Enter" здесь
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

            passwordEdit.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    // Ваш код для обработки нажатия клавиши "Enter" здесь
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

            passwordConfirm.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    // Ваш код для обработки нажатия клавиши "Enter" здесь

                    passwordConfirm.clearFocus()

                    passwordConfirm.hideKeyboard()

                    return@OnKeyListener true
                }
                false
            })
            buttonDoTrading.setOnClickListener {

                lifecycleScope.launch(Dispatchers.IO) {

                    DatabaseRepository.insertAllProfile(
                        profile.copy(
                            tradingPasswordHash = PasswordHasher(
                                firstName = profile.firstName, lastName = profile.lastName
                            ).passwordToHash(
                                passwordConfirm.text.toString()
                            )
                        )
                    )
                }.invokeOnCompletion {
                    Log.d("app", "finish")
                    this@TradingPasswordActivity.finish()
                }


            }


        }
    }

    private fun updateProfile() {
        lifecycleScope.launch(Dispatchers.IO) {
            DatabaseRepository.insertAllProfile(profile)
        }
    }

    private fun String.isThisContains3NumbersOfEmpty(): Boolean {

        Log.d("empty", ifEmpty { "empty" })
        if (this == "") {
            return true
        }
        for (number in 0..9) {
            if (contains("$number".repeat(3))) {
                return true
            }
        }
        Log.d("et", this)

        return false
    }

    private fun String.isThisContainsSequenceOrEmpty(): Boolean {

        for (number in 0..6) {
            if (contains(
                    "$number${number + 1}${number + 2}${number + 3}"
                ) || isEmpty()
            ) {
                return true
            }
        }
        return false

    }

}



