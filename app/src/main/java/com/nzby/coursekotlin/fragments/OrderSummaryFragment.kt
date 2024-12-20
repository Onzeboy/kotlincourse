package com.nzby.coursekotlin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nzby.coursekotlin.R
import com.nzby.coursekotlin.dao.AppDatabase
import com.nzby.coursekotlin.dao.OrderTableDao
import com.nzby.coursekotlin.databinding.FragmentOrderSummaryBinding
import com.nzby.coursekotlin.models.OrderTable
import com.nzby.coursekotlin.models.OrderStatus
import com.nzby.coursekotlin.ulits.OrderSummaryAdapter
import com.nzby.coursekotlin.utils.OrderAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderSummaryFragment : Fragment() {
    private lateinit var binding: FragmentOrderSummaryBinding
    private lateinit var orderTableDao: OrderTableDao // Ваш DAO

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderSummaryBinding.inflate(inflater, container, false)

        // Инициализация DAO
        val db = AppDatabase.getInstance(requireContext())
        orderTableDao = db.orderTableDao()

        // Загружаем данные асинхронно
        loadOrders()

        return binding.root
    }

    private fun loadOrders() {
        // Используем lifecycleScope для асинхронной работы в корутине
        lifecycleScope.launch {
            // Фоновый поток для запроса данных
            val orders = withContext(Dispatchers.IO) {
                orderTableDao.getAllOrders() // Получаем заказы из базы данных
            }

            // Обновляем UI на главном потоке
            val adapter = OrderSummaryAdapter(orders) { order ->
                val action = OrderSummaryFragmentDirections.actionOrderSummaryToOrderDetailState(order.id.toLong())
                findNavController().navigate(action)
            }

            // Устанавливаем адаптер и layout manager
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerView.adapter = adapter
        }
    }
}


