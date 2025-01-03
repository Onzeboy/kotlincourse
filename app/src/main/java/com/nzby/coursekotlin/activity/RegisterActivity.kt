package com.nzby.coursekotlin.activity

import android.os.Bundle
import android.text.InputFilter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nzby.coursekotlin.R
import com.nzby.coursekotlin.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val usernameEditText = findViewById<EditText>(R.id.usernameEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val phoneEditText = findViewById<EditText>(R.id.phoneEditText)
        val registerButton = findViewById<Button>(R.id.registerButton)

        // Устанавливаем ограничения длины пароля
        passwordEditText.filters = arrayOf(InputFilter.LengthFilter(40))

        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val phone = phoneEditText.text.toString()

            // Проверка, что все обязательные поля заполнены
            if (username.isEmpty()) {
                usernameEditText.error = "Введите имя"
                return@setOnClickListener
            }

            if (!username.matches(Regex("^[a-zA-Zа-яА-ЯёЁ\\s]+$"))) {
                usernameEditText.error = "Имя может содержать только буквы (латиница и кириллица)"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                passwordEditText.error = "Введите пароль"
                return@setOnClickListener
            }

            // Проверка длины пароля
            if (password.length < 3) {
                passwordEditText.error = "Пароль должен содержать минимум 3 символа"
                return@setOnClickListener
            }

            if (phone.isEmpty()) {
                phoneEditText.error = "Номер телефона отсутствует"
                return@setOnClickListener
            }

            // Регулярное выражение для русского номера телефона
            val phoneRegex = Regex("^(\\+7|8)?\\d{10}$")
            if (!phone.matches(phoneRegex)) {
                phoneEditText.error =
                    "Неверный формат телефона. Пример: +7XXXXXXXXXX или 8XXXXXXXXXX"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val isUnique = authViewModel.isPhoneUnique(phone)
                if (!isUnique) {
                    phoneEditText.error = "Номер телефона уже зарегистрирован"
                    return@launch
                }

                // Если номер уникален, регистрируем пользователя
                authViewModel.registerUser(username, password, phone)
                Toast.makeText(this@RegisterActivity, "Регистрация прошла успешно", Toast.LENGTH_SHORT).show()
                finish() // Закрываем экран регистрации
            }
        }
    }
}
