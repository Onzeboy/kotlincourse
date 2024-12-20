package com.nzby.coursekotlin.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.nzby.coursekotlin.R
import com.nzby.coursekotlin.viewmodels.AuthViewModel

class RegisterActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val usernameEditText = findViewById<EditText>(R.id.usernameEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText) // Поле email
        val registerButton = findViewById<Button>(R.id.registerButton)

        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val email = emailEditText.text.toString()

            // Проверка, что все обязательные поля заполнены
            if (username.isNotEmpty() && password.isNotEmpty()) {
                // Если email пустой, передаем null
                authViewModel.registerUser(username, password, email.takeIf { it.isNotEmpty() })
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                finish() // Закрываем экран регистрации и возвращаемся на экран входа
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
