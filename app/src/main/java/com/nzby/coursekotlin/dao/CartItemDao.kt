package com.nzby.coursekotlin.dao
import androidx.room.*

import com.nzby.coursekotlin.models.CartItem
import com.nzby.coursekotlin.models.CartItemWithProduct

@Dao
interface CartItemDao {
    @Insert
    suspend fun insert(cartItem: CartItem)

    @Update
    suspend fun update(cartItem: CartItem)

    @Delete
    suspend fun delete(cartItem: CartItem)

    @Query("DELETE FROM cart_item WHERE id = :cartItemId")
    suspend fun deleteById(cartItemId: Int)

    @Query("SELECT * FROM cart_item")
    suspend fun getAllCartItems(): List<CartItem>

    @Query("SELECT * FROM cart_item WHERE userId = :user ")
    suspend fun getAllCartItemsByID(user: Int): CartItem?

    @Transaction
    @Query("""
        SELECT 
            cart_item.id AS cartItemId,
            cart_item.userid AS cartUserId,
            cart_item.productid AS productId,
            cart_item.cartQuantity AS cartQuantity, -- Добавляем cartQuantity
            product.name AS productName,
            product.descrpt AS productDescription,
            product.price AS productPrice,
            product.image AS productImage
        FROM cart_item
        INNER JOIN product ON cart_item.productId = product.id
        WHERE cart_item.userid = :userId
    """)
    suspend fun getCartItemsWithProduct(userId: Int): List<CartItemWithProduct>

    @Query("UPDATE cart_item SET cartQuantity = cartQuantity + :quantity WHERE userId = :userId AND productId = :productId")
    suspend fun incrementQuantity(userId: Int, productId: Int, quantity: Int)

    @Query("UPDATE cart_item SET cartQuantity = :quantity WHERE userId = :userId AND productId = :productId")
    suspend fun setQuantity(userId: Int, productId: Int, quantity: Int)

    @Query("SELECT * FROM cart_item WHERE userId = :userId AND productId = :productId LIMIT 1")
    suspend fun getCartItemByUserIdAndProductId(userId: Int, productId: Int): CartItem?

    @Query("DELETE FROM cart_item WHERE userId = :user ")
    suspend fun deleteByUserId(user: Int)

    @Query("SELECT * FROM cart_item WHERE userId = :userId")
    suspend fun getCartItemsByUserId(userId: Int): List<CartItem>

    @Query("UPDATE cart_item SET cartQuantity = :newQuantity WHERE id = :cartItemId")
    suspend fun updateQuantity(cartItemId: Int, newQuantity: Int)

}

