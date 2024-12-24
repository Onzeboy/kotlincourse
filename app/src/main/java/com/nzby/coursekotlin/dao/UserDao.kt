package com.nzby.coursekotlin.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nzby.coursekotlin.models.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE id = :UId LIMIT 1")
    suspend fun getUserById(UId: Int): User?

    @Query("SELECT * FROM users WHERE phone = :phonee LIMIT 1")
    suspend fun getUserByPhone(phonee: String): User?

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserUnique(phone: String): User?
}
