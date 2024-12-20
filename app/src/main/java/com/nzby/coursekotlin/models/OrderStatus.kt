package com.nzby.coursekotlin.models

enum class OrderStatus {
    PENDING,    // Ожидает подтверждения
    CONFIRMED,  // Подтвержден
    SHIPPED,    // Отправлен
    DELIVERED,  // Доставлен
    CANCELLED   // Отменен
}