package com.nzby.coursekotlin.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "order_table",
    foreignKeys = [
        ForeignKey(
            entity = User::class,  // Замените на вашу сущность пользователя
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class OrderTable(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val city: String,
    val street: String,
    val home: String,
    val totalPrice: Double = 0.00,
    var status: OrderStatus = OrderStatus.PENDING
)