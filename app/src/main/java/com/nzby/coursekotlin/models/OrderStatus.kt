package com.nzby.coursekotlin.models

enum class OrderStatus {
    PENDING {
        override fun toString(): String = "Ожидает подтверждения"
    },
    CONFIRMED {
        override fun toString(): String = "Подтвержден"
    },
    SHIPPED {
        override fun toString(): String = "Отправлен"
    },
    DELIVERED {
        override fun toString(): String = "Доставлен"
    },
    CANCELLED {
        override fun toString(): String = "Отменен"
    };

    abstract override fun toString(): String
}