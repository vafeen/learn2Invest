package ru.surf.learn2invest.ui.components.screens

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.R
import ru.surf.learn2invest.databinding.ActivitySignupBinding
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.ui.components.screens.sign_in.SignINActivityActions
import ru.surf.learn2invest.ui.components.screens.sign_in.SignInActivity

/** Активити регистрации пользователя
 */
class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private var name: String = ""
    private var lastname: String = ""
    private val lengthLimit = 24
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = ContextCompat.getColor(this, R.color.main_background)
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
        )
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNameEditText()
        setupLastnameEditText()

        binding.inputNameEditText.addTextChangedListener {
            validateFields()
        }

        binding.inputLastnameEditText.addTextChangedListener {
            validateFields()
        }

        binding.signupBtn.setOnClickListener {
            signUpButtonClick()
        }

        binding.inputNameEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                return@setOnEditorActionListener onNextClicked()
            }
            false
        }

        binding.inputLastnameEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                return@setOnEditorActionListener onDoneClicked()
            }
            false
        }
    }

    private fun setupNameEditText() {
        binding.inputNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                name = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        with(binding.inputNameEditText) {
            requestFocus()
            showKeyboard()
        }
    }

    private fun setupLastnameEditText() {
        binding.inputLastnameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                lastname = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun validateFields() {
        if (validateName() && validateLastname()) {
            binding.signupBtn.isEnabled = true
            binding.signupBtn.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.main_background)
        } else {
            binding.signupBtn.isEnabled = false
            binding.signupBtn.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.btn_asset)
        }
    }

    private fun validateName(): Boolean {
        return when {
            name.isEmpty() -> {
                false
            }

            name.trim() != name -> {
                binding.nameErrorTextView.text =
                    ContextCompat.getString(this, R.string.contains_spaces)
                binding.nameErrorTextView.isVisible = true
                false
            }

            name.length > lengthLimit -> {
                binding.nameErrorTextView.text =
                    ContextCompat.getString(this, R.string.limit_len_exceeded)
                binding.nameErrorTextView.isVisible = true
                false
            }

            else -> {
                binding.nameErrorTextView.isVisible = false
                true
            }
        }
    }

    private fun validateLastname(): Boolean {
        return when {
            lastname.isEmpty() -> {
                false
            }

            lastname.trim() != lastname -> {
                binding.lastnameErrorTextView.text =
                    ContextCompat.getString(this, R.string.contains_spaces)
                binding.lastnameErrorTextView.isVisible = true
                false
            }

            lastname.length > lengthLimit -> {
                binding.lastnameErrorTextView.text =
                    ContextCompat.getString(this, R.string.limit_len_exceeded)
                binding.lastnameErrorTextView.isVisible = true
                false
            }

            else -> {
                binding.lastnameErrorTextView.isVisible = false
                true
            }
        }
    }

    private fun onNextClicked(): Boolean {
        if (name.isEmpty()) {
            binding.nameErrorTextView.text = ContextCompat.getString(this, R.string.empty_error)
            binding.nameErrorTextView.isVisible = true
            return true
        }
        binding.inputLastnameEditText.requestFocus()
        return false
    }

    private fun onDoneClicked(): Boolean {
        if (lastname.isEmpty()) {
            binding.lastnameErrorTextView.text = ContextCompat.getString(this, R.string.empty_error)
            binding.lastnameErrorTextView.isVisible = true
            return true
        } else {
            binding.inputLastnameEditText.hideKeyboard()
            binding.inputLastnameEditText.clearFocus()
        }
        return false
    }

    private fun signUpButtonClick() {
        lifecycleScope.launch(Dispatchers.IO) {
            val prof = DatabaseRepository.profile.copy(firstName = name, lastName = lastname)
            DatabaseRepository
                .updateProfile(prof)
        }.invokeOnCompletion {
            startActivity(Intent(this@SignUpActivity, SignInActivity::class.java).let {
                it.action = SignINActivityActions.SignUP.action

                it
            })
            this@SignUpActivity.finish()
        }
    }

    private fun View.hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun View.showKeyboard() = ViewCompat.getWindowInsetsController(this)
        ?.show(WindowInsetsCompat.Type.ime())
}