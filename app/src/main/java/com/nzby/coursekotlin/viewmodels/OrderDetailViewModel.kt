package com.nzby.coursekotlin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.nzby.coursekotlin.dao.OrderTableDao
import com.nzby.coursekotlin.models.OrderDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderDetailViewModel(private val orderTableDao: OrderTableDao) : ViewModel() {

    // Метод для получения деталей заказа
    suspend fun getOrderDetails(orderId: Int): List<OrderDetails> {
        return withContext(Dispatchers.IO) {
            orderTableDao.getOrderDetailsById(orderId)
        }
    }
}