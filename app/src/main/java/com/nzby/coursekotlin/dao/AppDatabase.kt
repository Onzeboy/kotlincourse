package com.nzby.coursekotlin.dao

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nzby.coursekotlin.models.Cart
import com.nzby.coursekotlin.models.CartItem
import com.nzby.coursekotlin.models.OrderItem
import com.nzby.coursekotlin.models.OrderTable
import com.nzby.coursekotlin.models.Product
import com.nzby.coursekotlin.models.User
import com.nzby.coursekotlin.utils.Converters
import com.nzby.coursekotlin.utils.UserRoleConverter
@Database(entities = [Product::class, User::class, Cart::class, CartItem::class, OrderTable::class, OrderItem::class], version = 8)
@TypeConverters(Converters::class, UserRoleConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun userDao(): UserDao
    abstract fun cartDao(): CartDao
    abstract fun cartItemDao(): CartItemDao
    abstract fun orderTableDao(): OrderTableDao
    abstract fun orderItemDao(): OrderItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Функция получения экземпляра базы данных
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    // Если версия базы данных изменилась, база будет пересоздана
                    .fallbackToDestructiveMigration()  // Удаляет старую базу данных при ошибке миграции
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
