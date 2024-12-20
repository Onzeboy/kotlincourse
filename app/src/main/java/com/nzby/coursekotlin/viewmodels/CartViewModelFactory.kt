package com.nzby.coursekotlin.viewmodels

import CartViewModel
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nzby.coursekotlin.dao.CartItemDao
import com.nzby.coursekotlin.dao.ProductDao

class CartViewModelFactory(
    private val cartItemDao: CartItemDao,
    private val productDao: ProductDao,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(cartItemDao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
