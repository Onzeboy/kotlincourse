package com.nzby.coursekotlin.dao
import androidx.room.*
import com.nzby.coursekotlin.models.OrderDetails
import com.nzby.coursekotlin.models.OrderItemWithProduct
import com.nzby.coursekotlin.models.OrderTable
import com.nzby.coursekotlin.models.OrderWithItems


@Dao
interface OrderTableDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: OrderTable): Long

    @Query("SELECT * FROM order_table")
    suspend fun getAllOrders(): List<OrderTable>

    @Query("SELECT * FROM order_table")
    suspend fun getAllOrdersWithItems(): List<OrderWithItems>

    @Query("SELECT * FROM order_table")
    fun getAllOrdersNonSusp(): List<OrderTable>

    @Query("SELECT * FROM order_table WHERE id = :id")
    suspend fun getOrderById(id: kotlin.Long): OrderTable?

    @Query("UPDATE order_table SET userId = :userId, city = :city, street = :street, home = :home WHERE id = :id")
    suspend fun updateOrder(id: Int, userId: Int, city: String, street: String, home: String)

    @Query("DELETE FROM order_table WHERE id = :id")
    suspend fun deleteOrder(id: Int)

    @Transaction // Указывает, что запрос использует несколько таблиц
    @Query("SELECT * FROM order_table WHERE userId = :userId")
    suspend fun getOrdersWithItemsByUserId(userId: Int): List<OrderWithItems>

    @Transaction
    @Query("""
        SELECT 
            o.id as id,
            o.userId as userId,
            o.createdAt as createdAt,
            o.city as city,
            o.street as street,
            o.home as home,
            o.totalPrice as totalPrice,
            oi.id as itemId,
            oi.orderId as orderId,
            oi.productId as productId,
            o.status as status
        FROM order_table o
        JOIN order_item oi ON o.id = oi.orderId
        WHERE o.id = :orderId
    """)
    suspend fun getOrderDetailsById(orderId: Int): List<OrderDetails>
}

