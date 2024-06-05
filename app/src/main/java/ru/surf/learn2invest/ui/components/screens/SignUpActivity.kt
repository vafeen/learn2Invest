package ru.surf.learn2invest.ui.components.screens

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.R
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.databinding.ActivitySignupBinding
import ru.surf.learn2invest.noui.cryptography.PasswordHasher
import ru.surf.learn2invest.noui.database_components.entity.Profile
import ru.surf.learn2invest.ui.components.screens.sign_in.SignINActivityActions
import ru.surf.learn2invest.ui.components.screens.sign_in.SignInActivity
import ru.surf.learn2invest.ui.tests.data.testProfile

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var textWatcher: TextWatcher
    private var name: String = ""
    private var lastname: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNameEditText()
        setupLastnameEditText()

        setupNameClearIconImageView()
        setupLastnameClearIconImageView()

        binding.inputNameEditText.addTextChangedListener {
            validateFields()
        }

        binding.inputLastnameEditText.addTextChangedListener {
            validateFields()
        }

        binding.signupBtn.setOnClickListener {
            val profile = Profile(
                id = 0,
                firstName = name,
                lastName = lastname,
                pin = 0,
                notification = false,
                biometry = false,
                confirmDeal = false,
                fiatBalance = 0,
                assetBalance = 0,
            )
            lifecycleScope.launch(Dispatchers.IO) {
                App.mainDB.profileDao().insertAll(profile)
            }
            startActivity(Intent(this@SignUpActivity, SignInActivity::class.java).let {
                it.action = SignINActivityActions.SignUP.action

                it
            })
            this@SignUpActivity.finish()
        }
    }

    private fun setupNameClearIconImageView() {
        binding.nameClear.setOnClickListener {
            binding.inputNameEditText.setText("")
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.inputNameEditText.windowToken, 0)
        }
    }

    private fun setupLastnameClearIconImageView() {
        binding.lastnameClear.setOnClickListener {
            binding.inputLastnameEditText.setText("")
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.inputLastnameEditText.windowToken, 0)
        }
    }

    private fun setupNameEditText() {

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.nameClear.visibility = clearButtonVisibility(s)
                name = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        binding.inputNameEditText.addTextChangedListener(textWatcher)
    }

    private fun setupLastnameEditText() {

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.lastnameClear.visibility = clearButtonVisibility(s)
                lastname = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        binding.inputLastnameEditText.addTextChangedListener(textWatcher)
    }

    private fun validateFields() {
        if (name.isNotEmpty() && lastname.isNotEmpty()) {
            binding.signupBtn.isEnabled = true
            binding.signupBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.main_background)
        } else {
            binding.signupBtn.isEnabled = false
            binding.signupBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.btn_asset)
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
}