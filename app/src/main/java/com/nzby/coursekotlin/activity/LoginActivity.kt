package com.nzby.coursekotlin.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nzby.coursekotlin.R
import com.nzby.coursekotlin.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val phoneEditText = findViewById<EditText>(R.id.usernameEditText) // Поле ввода телефона
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<Button>(R.id.btnRegister)

        loginButton.setOnClickListener {
            val phone = phoneEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Простая валидация полей
            if (phone.isEmpty()) {
                phoneEditText.error = "Введите номер телефона"
                return@setOnClickListener
            }

            // Проверка формата телефона
            val phoneRegex = Regex("^(\\+7|8)?\\d{10}$")
            if (!phone.matches(phoneRegex)) {
                phoneEditText.error = "Неверный формат телефона. Пример: +7XXXXXXXXXX или 8XXXXXXXXXX"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                passwordEditText.error = "Введите пароль"
                return@setOnClickListener
            }

            // Запускаем корутину для аутентификации
            lifecycleScope.launch {
                val user = authViewModel.authenticateUser(phone, password)
                if (user != null) {
                    // Сохраняем данные пользователя
                    val sharedPrefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
                    val editor = sharedPrefs.edit()
                    editor.putString("phone", phone)
                    editor.putString("role", user.role.toString())  // Сохраняем роль пользователя
                    editor.putInt("user_id", user.id)
                    editor.putString("name", user.username)// Сохраняем id пользователя
                    editor.putBoolean("is_logged_in", true)
                    editor.apply()

                    Toast.makeText(this@LoginActivity, "Успешный вход", Toast.LENGTH_SHORT).show()
                    navigateToMainScreen()
                } else {
                    Toast.makeText(this@LoginActivity, "Неверный логин/пароль", Toast.LENGTH_SHORT).show()
                }
            }
        }

        registerButton.setOnClickListener {
            navigateToRegisterScreen()  // Переход на экран регистрации
        }
    }

    private fun navigateToMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToRegisterScreen() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}
