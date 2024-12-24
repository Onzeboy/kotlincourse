package com.nzby.coursekotlin.dao

import androidx.room.Dao
import androidx.room.Insert
import com.nzby.coursekotlin.models.ProductHistory

@Dao
interface ProductHistoryDao {
    @Insert
    suspend fun insert(productHistory: ProductHistory): Long

}