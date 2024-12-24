package com.nzby.coursekotlin.dao

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nzby.coursekotlin.models.CartItem
import com.nzby.coursekotlin.models.OrderItem
import com.nzby.coursekotlin.models.OrderTable
import com.nzby.coursekotlin.models.Product
import com.nzby.coursekotlin.models.ProductHistory
import com.nzby.coursekotlin.models.User
import com.nzby.coursekotlin.utils.Converters
import com.nzby.coursekotlin.utils.UserRoleConverter
@Database(entities = [Product::class, ProductHistory::class, User::class, CartItem::class, OrderTable::class, OrderItem::class], version = 18)
@TypeConverters(Converters::class, UserRoleConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun productHistoryDao(): ProductHistoryDao
    abstract fun userDao(): UserDao
    abstract fun cartItemDao(): CartItemDao
    abstract fun orderTableDao(): OrderTableDao
    abstract fun orderItemDao(): OrderItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addCallback(AppDatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
    private class AppDatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Создаём триггеры
            db.execSQL(
                """
                CREATE TRIGGER IF NOT EXISTS decrease_product_quantity AFTER INSERT ON cart_item
                BEGIN
                    UPDATE Product
                    SET quantity = quantity - NEW.cartQuantity
                    WHERE id = NEW.productId AND quantity >= NEW.cartQuantity;

                    UPDATE Product
                    SET quantity = 0
                    WHERE id = NEW.productId AND quantity < 0;
                END;
            """
            )
            db.execSQL(
                """
                CREATE TRIGGER IF NOT EXISTS adjust_product_quantity AFTER UPDATE ON cart_item
                BEGIN
                    UPDATE Product
                    SET quantity = quantity + OLD.cartQuantity - NEW.cartQuantity
                    WHERE id = NEW.productId;
                END;
            """
            )
        }
    }
}
