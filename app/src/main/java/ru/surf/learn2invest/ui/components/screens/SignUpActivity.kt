package ru.surf.learn2invest.ui.components.screens

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import ru.surf.learn2invest.databinding.ActivitySignupBinding

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

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
}