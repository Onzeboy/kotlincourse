package com.nzby.coursekotlin.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users", indices = [Index(value = ["phone"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val password: String, // Храните хеш пароля, а не сам пароль
    val phone: String? = null,
    val role: UserRole = UserRole.ROLE_USER
)