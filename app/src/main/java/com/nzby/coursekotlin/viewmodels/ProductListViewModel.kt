package com.nzby.coursekotlin.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.nzby.coursekotlin.dao.CartItemDao
import com.nzby.coursekotlin.dao.ProductDao
import com.nzby.coursekotlin.models.CartItem
import com.nzby.coursekotlin.models.Product // Убедитесь, что используете правильный класс для глобальных данных
import com.nzby.coursekotlin.UserApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductListViewModel(
    private val productDao: ProductDao,
    private val cartItemDao: CartItemDao,
    application: Application
) : AndroidViewModel(application) {

    private val appContext = application as UserApplication// Приводим контекст к нужному классу

    val productList: LiveData<List<Product>> = productDao.getAllProductsFlow().asLiveData()

    fun addToCart(product: Product, quantity: Int) {
        val userId = appContext.userId  // Получаем userId из глобального объекта
        if (userId != -1) {
            val cartItem = userId?.let {
                CartItem(
                    productId = product.id,
                    userId = it,
                    cartQuantity = quantity
                )
            }
            CoroutineScope(Dispatchers.IO).launch {
                cartItem?.let { cartItemDao.insert(it) }
            }
        }
    }
}
