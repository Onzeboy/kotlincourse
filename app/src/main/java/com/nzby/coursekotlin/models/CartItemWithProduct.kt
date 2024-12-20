package com.nzby.coursekotlin.models

data class CartItemWithProduct(
    val cartItemId: Int,
    val cartUserId: Int,  // Rename from userId to cartUserId
    val productId: Int,
    val productName: String,
    val productDescription: String,
    val productPrice: Double,
    val productImage: ByteArray?
)
