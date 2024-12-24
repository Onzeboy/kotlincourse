package com.nzby.coursekotlin.models

import android.graphics.Bitmap
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.nzby.coursekotlin.utils.BitmapConverters

@TypeConverters(BitmapConverters::class)
data class OrderItemWithProduct(
    val productId: Long,
    val productName: String,
    val productDescription: String?,
    val productPrice: Double,
    val productImage: ByteArray?,
    val productQuantity: Int,
    val itemTotalPrice: Double = 0.0
)