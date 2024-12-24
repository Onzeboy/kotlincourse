package com.nzby.coursekotlin.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "order_item",
    foreignKeys = [
        ForeignKey(
            entity = OrderTable::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductHistory::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class OrderItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderId: Long,
    val productId: Long,
    val quantity: Int,
    val price: Double = 0.00,
)