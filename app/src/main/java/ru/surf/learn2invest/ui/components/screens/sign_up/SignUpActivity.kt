package ru.surf.learn2invest.ui.components.screens.sign_up

import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.R
import ru.surf.learn2invest.databinding.ActivitySignUpBinding
import ru.surf.learn2invest.ui.components.screens.sign_in.SignINActivityActions
import ru.surf.learn2invest.ui.components.screens.sign_in.SignInActivity

/** Активити регистрации пользователя
 */

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    private val viewModel: SignUpActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val color =
            if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                R.color.main_background_dark
            } else {
                R.color.white
            }
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(this, color)
            )
        )
        window.navigationBarColor = ContextCompat.getColor(this, color)
        window.statusBarColor = ContextCompat.getColor(
            this,
            if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                R.color.accent_background_dark
            } else {
                R.color.accent_background
            }
        )

        binding = ActivitySignUpBinding.inflate(layoutInflater)
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

        applyThemeColors()
    }

    private fun setupNameEditText() {
        binding.inputNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.name = s.toString()
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
                viewModel.lastname = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun validateFields() {
        binding.signupBtn.isEnabled = validateName() && validateLastname()
        applyThemeColors()
    }

    private fun applyThemeColors() {
        val isDarkTheme =
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        binding.signupBtn.backgroundTintList = ColorStateList.valueOf(
            resources.getColor(
                if (binding.signupBtn.isEnabled) {
                    if (isDarkTheme)
                        R.color.accent_background_dark
                    else
                        R.color.accent_background
                } else {
                    if (isDarkTheme)
                        R.color.accent_button_dark
                    else
                        R.color.btn_asset
                }
            )
        )
    }

    private fun validateName(): Boolean {
        return when {
            viewModel.name.isEmpty() -> {
                false
            }

            viewModel.name.trim() != viewModel.name -> {
                binding.nameErrorTextView.text =
                    ContextCompat.getString(this, R.string.contains_spaces)
                binding.nameErrorTextView.isVisible = true
                false
            }

            viewModel.name.length > viewModel.lengthLimit -> {
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
            viewModel.lastname.isEmpty() -> {
                false
            }

            viewModel.lastname.trim() != viewModel.lastname -> {
                binding.lastnameErrorTextView.text =
                    ContextCompat.getString(this, R.string.contains_spaces)
                binding.lastnameErrorTextView.isVisible = true
                false
            }

            viewModel.lastname.length > viewModel.lengthLimit -> {
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
        if (viewModel.name.isEmpty()) {
            binding.nameErrorTextView.text = ContextCompat.getString(this, R.string.empty_error)
            binding.nameErrorTextView.isVisible = true
            return true
        }
        binding.inputLastnameEditText.requestFocus()
        return false
    }

    private fun onDoneClicked(): Boolean {
        if (viewModel.lastname.isEmpty()) {
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
            viewModel.databaseRepository.apply {
                updateProfile(
                    profile.copy(
                        firstName = viewModel.name,
                        lastName = viewModel.lastname
                    )
                )
            }
        }.invokeOnCompletion {
            startActivity(Intent(this@SignUpActivity, SignInActivity::class.java).apply {
                action = SignINActivityActions.SignUP.action
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