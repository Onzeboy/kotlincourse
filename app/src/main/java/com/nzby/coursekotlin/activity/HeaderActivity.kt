package com.nzby.coursekotlin.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.nzby.coursekotlin.R
import com.nzby.coursekotlin.dao.AppDatabase
import com.nzby.coursekotlin.models.UserRole
import com.nzby.coursekotlin.viewmodels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HeaderActivity : AppCompatActivity() {

    private lateinit var userNameTextView: TextView
    private lateinit var userRoleTextView: TextView
    private lateinit var userAvatarImageView: ImageView
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nav_header_main) // Замените на свой layout

        // Инициализация UI элементов
        userNameTextView = findViewById(R.id.userName)
        userRoleTextView = findViewById(R.id.userRole)

        // Инициализация ViewModel
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        // Наблюдаем за данными пользователя
        userViewModel.user.observe(this, Observer { user ->
            if (user != null) {
                // Если пользователь найден, отображаем его имя и роль
                userNameTextView.text = user.username
                userRoleTextView.text = if (user.role == UserRole.ROLE_ADMIN) "Администратор" else "Пользователь"
            } else {
                // Если пользователь не найден, отображаем "Не авторизован"
                userNameTextView.text = "Не авторизован"
                userRoleTextView.visibility = View.GONE
            }
        })

        // Пример: Получаем пользователя по имени "username_example"
        userViewModel.getUserByUsername("username_example")
    }
}

