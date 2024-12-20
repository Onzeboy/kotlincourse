package com.nzby.coursekotlin.models

enum class UserRole {
    ROLE_ADMIN, // Администратор с полными правами
    ROLE_USER;  // Обычный пользователь

    fun toDisplayName(): String {
        return when (this) {
            ROLE_ADMIN -> "Администратор"
            ROLE_USER -> "Пользователь"
        }
    }
}