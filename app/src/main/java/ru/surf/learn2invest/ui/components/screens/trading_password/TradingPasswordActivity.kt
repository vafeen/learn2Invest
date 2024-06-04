package ru.surf.learn2invest.ui.components.screens.trading_password

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.R
import ru.surf.learn2invest.app.Learn2InvestApp
import ru.surf.learn2invest.databinding.TradingPasswordActivityBinding
import ru.surf.learn2invest.noui.cryptography.PasswordHasher

class TradingPasswordActivity : AppCompatActivity() {
    private lateinit var binding: TradingPasswordActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = TradingPasswordActivityBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initListeners()

        checkPassword()
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
        Log.d("app", "x =${this}")
        return false

    }


    private fun checkPassword() {
        binding.apply {
            // TODO (Найдите пж норм иконки галочки и крестика
            val ok = resources.getDrawable(R.drawable.circle_plus)

            val no = resources.getDrawable(R.drawable.circle_minus)

            var rule1IsTrue = false
            var rule2IsTrue = false
            var rule3IsTrue = false
            var rule4IsTrue = false
            var rule5IsTrue = false

            when (Learn2InvestApp.profile?.tradingPasswordHash) {
                null -> {
                    imageRule5.isVisible = false

                    rulesTrpass5.isVisible = false

                    textInputLayout1.isVisible = false
                }

                else -> {
                    imageRule5.isVisible = true

                    rulesTrpass5.isVisible = true

                    textInputLayout1.isVisible = true
                }

            }


            imageRule1.setImageDrawable(
                if ((passwordEdit.text?.length ?: 0) >= 6) {
                    rule1IsTrue = true
                    ok
                } else {

                    no
                }
            )
            imageRule2.setImageDrawable(
                if (passwordEdit.text.toString().isThisContains3NumbersOfEmpty()) {

                    no
                } else {

                    rule2IsTrue = true
                    ok
                }
            )

            imageRule3.setImageDrawable(
                if (passwordEdit.text.toString().isThisContainsSequenceOrEmpty()) {
                    no
                } else {

                    rule3IsTrue = true
                    ok
                }
            )

            imageRule4.setImageDrawable(
                if (passwordEdit.text.toString() == passwordConfirm.text.toString() && passwordEdit.text?.isNotEmpty() == true) {
                    rule4IsTrue = true
                    ok
                } else {
                    no
                }
            )


            // какая-то хуйня
            imageRule5.setImageDrawable(if (Learn2InvestApp.profile?.let {
                    val x = PasswordHasher(it).verifyTradingPassword("${passwordLast.text}")
                    Log.d("password", "verify? - $x")
                    x

                } == true) {
                rule5IsTrue = true
                ok
            } else {
                no
            })

            buttonDoTrading.isVisible = (
                    rule1IsTrue && rule2IsTrue
                            && rule3IsTrue && rule4IsTrue &&
                            if (Learn2InvestApp.profile?.tradingPasswordHash
                                != null
                            ) {
                                rule5IsTrue
                            } else {
                                true
                            })

        }
    }


    private fun initListeners() {

        binding.apply {

            headerTradingPasswordActivity.text = if (Learn2InvestApp.profile?.tradingPasswordHash
                != null
            ) {
                "Изменение торгового пароля"
            } else {
                "Создание торгового пароля"
            }

            rulesTrpass1.text = "Пароль должен состоять минимум из 6 цифр"

            rulesTrpass2.text = "Не более двух одинаковых цифр"

            rulesTrpass3.text = "Нет последовательности более трех цифр"

            rulesTrpass4.text = "Пароли совпадают"

            rulesTrpass5.text = "Старый пароль верен"

            if (Learn2InvestApp.profile?.tradingPasswordHash
                != null
            ) {
                rulesTrpass5.isVisible = true

                imageRule5.isVisible = true
            } else {
                rulesTrpass5.isVisible = false

                imageRule5.isVisible = false
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

            buttonDoTrading.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    Learn2InvestApp.profile?.let { prof ->

                        Learn2InvestApp.mainDB.profileDao().insertAll(
                            prof.copy(
                                tradingPasswordHash = PasswordHasher(prof).passwordToHash(
                                    passwordConfirm.text.toString()
                                )
                            )
                        )
                    }
                }
//                this@TradingPasswordActivity.finish()
            }


        }
    }
}



