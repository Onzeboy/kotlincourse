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
    suspend fun getAllCartItems(): CartItem

    @Query("SELECT * FROM cart_item WHERE userId = :user ")
    suspend fun getAllCartItemsByID(user: Int): CartItem

    @Transaction
    @Query("""
    SELECT 
        cart_item.id AS cartItemId,  -- Название поля в запросе должно совпадать с тем, что в модели
        cart_item.userId AS cartUserId,  -- Убедитесь, что используете правильные имена столбцов
        product.id AS productId, 
        product.name AS productName, 
        product.descrpt AS productDescription, 
        product.price AS productPrice, 
        product.image AS productImage
    FROM cart_item 
    INNER JOIN product ON cart_item.productId = product.id 
    WHERE cart_item.userId = :userId
""")
    suspend fun getCartItemsWithProduct(userId: Int): List<CartItemWithProduct>



    @Query("DELETE FROM cart_item WHERE userId = :user ")
    suspend fun deleteByUserId(user: Int)

}

