package com.nzby.coursekotlin.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nzby.coursekotlin.models.Product

@Dao
interface ProductDao {
    @Update
    suspend fun update(product: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product) // Добавлен `suspend`

    @Query("SELECT * FROM product")
    suspend fun getAllProducts(): List<Product> // Добавлен `suspend`

    @Query("SELECT * FROM product")
    fun getAllProductsFlow(): kotlinx.coroutines.flow.Flow<List<Product>>

    @Query("SELECT * FROM product WHERE id = :productId LIMIT 1")
    suspend fun getProductById(productId: Int): Product?

    @Query("UPDATE product SET name = :name, price = :price, image = :image WHERE id = :id")
    suspend fun updateProduct(
        id: Int,
        name: String,
        price: Int,
        image: ByteArray?
    ) // Добавлен `suspend`

    @Query("DELETE FROM product WHERE id = :id")
    suspend fun deleteProduct(id: Int) // Добавлен `suspend`companion object

    @Query("UPDATE Product SET quantity = :newQuantity WHERE id = :productId")
    suspend fun updateQuantity(productId: Int, newQuantity: Int)
}
