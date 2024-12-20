package com.nzby.coursekotlin.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nzby.coursekotlin.dao.AppDatabase
import com.nzby.coursekotlin.models.User
import com.nzby.coursekotlin.models.UserRole
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = AppDatabase.getInstance(application).userDao()

    // Регистрация нового пользователя с хешированием пароля, email и ролью
    fun registerUser(username: String, password: String, email: String?, role: UserRole = UserRole.ROLE_USER) {
        val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt()) // Хешируем пароль
        val user = User(username = username, password = hashedPassword, email = email, role = role)
        viewModelScope.launch {
            userDao.insertUser(user)
        }
    }

    // Проверка данных пользователя при авторизации
    suspend fun authenticateUser(username: String, password: String): User? {
        val user = userDao.getUserByUsername(username)
        return if (user != null && BCrypt.checkpw(password, user.password)) { // Сравниваем хеш пароля
            user
        } else {
            null
        }
    }
}