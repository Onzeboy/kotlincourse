package com.nzby.coursekotlin.models

data class OrderDetails(
    val id: Int,
    val userId: Int,
    val createdAt: String,
    val city: String,
    val street: String,
    val home: String,
    val totalPrice: Double = 0.00,
    val itemId: Int,
    val orderId: Int,
    val productId: Int,
    val status: String
)