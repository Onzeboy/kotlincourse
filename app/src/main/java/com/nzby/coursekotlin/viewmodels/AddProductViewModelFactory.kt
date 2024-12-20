package com.nzby.coursekotlin.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nzby.coursekotlin.dao.ProductDao

class AddProductViewModelFactory(private val productDao: ProductDao) : ViewModelProvider.Factory {
    // Исправляем сигнатуру метода
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddProductViewModel(productDao) as T
    }
}
