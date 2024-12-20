package com.nzby.coursekotlin.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import com.nzby.coursekotlin.models.Cart
import com.nzby.coursekotlin.models.CartItem

@Dao
interface CartDao{

    // Вставка новой корзины
    @Insert
    suspend fun insert(cart: Cart): Long  // Возвращаем ID новой корзины

    // Получение корзины по ID
    @Query("SELECT * FROM cart WHERE id = :cartId LIMIT 1")
    suspend fun getCartById(cartId: Int): Cart?

    @Query("SELECT * FROM cart WHERE userId = :userId")
    suspend fun getCartByUserId(userId: Int): Cart?

    @Insert
    suspend fun insertCart(cart: Cart): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItem)

    @Query("SELECT * FROM cart WHERE userId = :userId")
    suspend fun getCart(userId: Int): Cart?
}
