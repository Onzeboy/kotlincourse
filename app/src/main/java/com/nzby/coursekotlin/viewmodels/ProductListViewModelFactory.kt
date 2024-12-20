package com.nzby.coursekotlin.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nzby.coursekotlin.dao.CartItemDao
import com.nzby.coursekotlin.dao.ProductDao

class ProductListViewModelFactory(
    private val productDao: ProductDao,
    private val cartItemDao: CartItemDao,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProductListViewModel(productDao, cartItemDao, application) as T
    }
}
