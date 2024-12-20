package com.nzby.coursekotlin.utils

import androidx.room.TypeConverter
import com.nzby.coursekotlin.models.UserRole

class UserRoleConverter {
    @TypeConverter
    fun fromUserRole(role: UserRole): String {
        return role.name
    }

    @TypeConverter
    fun toUserRole(role: String): UserRole {
        return UserRole.valueOf(role)
    }
}
