package com.nzby.coursekotlin.models

import androidx.room.Embedded
import androidx.room.Relation

data class OrderWithItems(
    @Embedded val order: OrderTable, // Указывает на основную таблицу
    @Relation(
        parentColumn = "id", // Колонка, связывающая OrderTable
        entityColumn = "orderId", // Колонка в OrderItem
        entity = OrderItem::class // Указывает на связанную сущность
    )
    val items: List<OrderItem> // Список связанных элементов заказа
)