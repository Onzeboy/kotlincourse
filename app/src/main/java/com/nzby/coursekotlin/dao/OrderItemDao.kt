package com.nzby.coursekotlin.dao
import androidx.room.*
import com.nzby.coursekotlin.models.OrderItem
import com.nzby.coursekotlin.models.OrderItemWithProduct

@Dao
interface OrderItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(orderItem: OrderItem)

//    @Query("SELECT * FROM order_item WHERE userId = :id")
//    suspend fun getOrderItemsByUserId(id: Int): List<OrderItem>

    @Query("SELECT * FROM order_item WHERE id = :id")
    suspend fun getOrderItemById(id: kotlin.Long): OrderItem?


    @Query("UPDATE order_item SET orderId = :orderId, productId = :productId, price = :price WHERE id = :id")
    suspend fun updateOrderItem(id: Int, orderId: Int, productId: Int, price: Int)

    @Query("DELETE FROM order_item WHERE id = :id")
    suspend  fun deleteOrderItem(id: Int)


    @Query("SELECT * FROM order_item WHERE orderId = :order")
    suspend fun getOrderItemsByOrderId(order: Long) : List<OrderItem>

    @Transaction
    @Query("""
    SELECT 
        order_item.id AS orderItemId,  
        order_item.orderId AS orderId,  
        order_item.productId AS productId, 
        product.name AS productName, 
        product.descrpt AS productDescription, 
        product.price AS productPrice, 
        product.image AS productImage, 
        order_item.price AS itemTotalPrice,
        order_item.quantity AS productQuantity
    FROM order_item 
    INNER JOIN product ON order_item.productId = product.id 
    WHERE order_item.orderId = :orderId
""")
    suspend fun getOrderItemsWithProduct(orderId: Long?): List<OrderItemWithProduct>

}
