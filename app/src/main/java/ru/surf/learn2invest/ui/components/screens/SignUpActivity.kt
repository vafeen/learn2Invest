package ru.surf.learn2invest.ui.components.screens

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
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
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.databinding.ActivitySignupBinding
import ru.surf.learn2invest.ui.components.screens.sign_in.SignINActivityActions
import ru.surf.learn2invest.ui.components.screens.sign_in.SignInActivity

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var textWatcher: TextWatcher
    private var name: String = ""
    private var lastname: String = ""

    companion object{
        private const val EMPTY_ERROR = "Пустое поле"
        private const val SPACE_ERROR = "Содержит пробелы в начале или конце"
        private const val LENGTH_ERROR = "Превышен лимит длины"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNameEditText()
        setupLastnameEditText()

        binding.nameClear.setOnClickListener {
            nameClearIconClick()
        }

        binding.lastnameClear.setOnClickListener {
            lastnameClearIconClick()
        }

        binding.inputNameEditText.addTextChangedListener {
            validateFields()
        }

        binding.inputLastnameEditText.addTextChangedListener {
            validateFields()
        }

        binding.signupBtn.setOnClickListener {
            signUpButtonClick()
        }

        binding.inputNameEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                return@setOnEditorActionListener onNextClicked()
            }
            false
        }

        binding.inputLastnameEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                return@setOnEditorActionListener onDoneClicked()
            }
            false
        }
    }

    private fun nameClearIconClick() {
        binding.inputNameEditText.text.clear()
        binding.inputNameEditText.showKeyboard()
        binding.inputNameEditText.requestFocus()
        binding.nameErrorTextView.text = EMPTY_ERROR
        binding.nameErrorTextView.isVisible = true
    }

    private fun lastnameClearIconClick() {
        binding.inputLastnameEditText.text.clear()
        binding.inputLastnameEditText.showKeyboard()
        binding.inputLastnameEditText.requestFocus()
        binding.lastnameErrorTextView.text = EMPTY_ERROR
        binding.lastnameErrorTextView.isVisible = true
    }

    private fun setupNameEditText() {

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.nameClear.isVisible = !s.isNullOrEmpty()
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
                binding.lastnameClear.isVisible = !s.isNullOrEmpty()
                lastname = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        binding.inputLastnameEditText.addTextChangedListener(textWatcher)
    }

    private fun validateFields() {
        val nameValid = validateName()
        val lastnameValid = validateLastname()

        if (nameValid && lastnameValid) {
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

            hasLeadingOrTrailingSpaces(name) -> {
                false
            }

            name.length > 24 -> {
                binding.nameErrorTextView.text = LENGTH_ERROR
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

            hasLeadingOrTrailingSpaces(lastname) -> {
                false
            }

            lastname.length > 24 -> {
                binding.lastnameErrorTextView.text = LENGTH_ERROR
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
            binding.nameErrorTextView.text = EMPTY_ERROR
            binding.nameErrorTextView.isVisible = true
            return true
        } else {
            if (hasLeadingOrTrailingSpaces(name)) {
                binding.nameErrorTextView.text = SPACE_ERROR
                binding.nameErrorTextView.isVisible = true
                return true
            }
            binding.nameErrorTextView.isVisible = false
        }
        binding.inputLastnameEditText.requestFocus()
        return false
    }

    private fun onDoneClicked(): Boolean {
        if (lastname.isEmpty()) {
            binding.lastnameErrorTextView.text = EMPTY_ERROR
            binding.lastnameErrorTextView.isVisible = true
            return true
        } else {
            if (hasLeadingOrTrailingSpaces(lastname)) {
                binding.lastnameErrorTextView.text = SPACE_ERROR
                binding.lastnameErrorTextView.isVisible = true
                return true
            }
            binding.lastnameErrorTextView.isVisible = false
            binding.inputLastnameEditText.hideKeyboard()
            binding.inputLastnameEditText.clearFocus()
        }
        return false
    }


    private fun signUpButtonClick() {
        lifecycleScope.launch(Dispatchers.IO) {
            App.mainDB.profileDao()
                .insertAll(App.profile.copy(firstName = name, lastName = lastname))
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

    private fun hasLeadingOrTrailingSpaces(s: CharSequence?): Boolean {
        if (s.isNullOrEmpty()) return false
        return s.startsWith(" ") || s.endsWith(" ")
    }
}