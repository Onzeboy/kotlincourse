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


        val usernameEditText = findViewById<EditText>(R.id.usernameEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<Button>(R.id.btnRegister)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Запускаем корутину для аутентификации
            lifecycleScope.launch {
                val user = authViewModel.authenticateUser(username, password)
                if (user != null) {
                    // Сохраняем имя пользователя, роль и id
                    val sharedPrefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
                    val editor = sharedPrefs.edit()
                    editor.putString("username", username)
                    editor.putString("role", user.role.toString())  // Сохраняем роль пользователя
                    editor.putInt("user_id", user.id)  // Сохраняем id пользователя
                    editor.putBoolean("is_logged_in", true)
                    editor.apply()

                    Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
                    navigateToMainScreen()
                } else {
                    Toast.makeText(this@LoginActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
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
