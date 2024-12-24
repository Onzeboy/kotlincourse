package com.nzby.coursekotlin.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nzby.coursekotlin.dao.AppDatabase
import com.nzby.coursekotlin.dao.UserDao
import com.nzby.coursekotlin.models.User
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao: UserDao = AppDatabase.getInstance(application).userDao()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    fun getUserByPhone(phone: String) {
        viewModelScope.launch {
            val userFromDb = userDao.getUserByPhone(phone)
            _user.postValue(userFromDb)
        }
    }
}

