package com.nzby.coursekotlin.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nzby.coursekotlin.dao.OrderTableDao

class OrderDetailsViewModelFactory(
    private val orderTableDao: OrderTableDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderDetailViewModel::class.java)) {
            return OrderDetailViewModel(orderTableDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}