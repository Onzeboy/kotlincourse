package com.nzby.coursekotlin.models

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val descrpt: String,
    val price: Double = 0.0,
    val image: ByteArray?
)
