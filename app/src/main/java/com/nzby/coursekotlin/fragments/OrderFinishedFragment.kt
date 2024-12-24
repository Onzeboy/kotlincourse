package com.nzby.coursekotlin.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nzby.coursekotlin.UserApplication
import com.nzby.coursekotlin.dao.AppDatabase
import com.nzby.coursekotlin.dao.OrderItemDao
import com.nzby.coursekotlin.dao.OrderTableDao
import com.nzby.coursekotlin.databinding.FragmentOrdersBinding
import com.nzby.coursekotlin.models.OrderStatus
import com.nzby.coursekotlin.models.OrderWithItems
import com.nzby.coursekotlin.utils.OrderAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderFinishedFragment : Fragment() {

    private lateinit var binding: FragmentOrdersBinding
    private lateinit var orderTableDao: OrderTableDao
    private lateinit var orderItemDao: OrderItemDao
    private lateinit var orderAdapter: OrderAdapter
    private val orders = mutableListOf<OrderWithItems>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getInstance(requireContext())
        orderTableDao = database.orderTableDao()
        orderItemDao = database.orderItemDao()

        binding.recyclerViewOrders.layoutManager = LinearLayoutManager(requireContext())
        orderAdapter = OrderAdapter(orders) { order ->
            Log.d("OrdersFragment", "Clicked order ID: ${order.order.id.toLong()}")
            val action = OrdersFragmentDirections.actionOrdersToCurrentOrderDetails(order.order.id.toLong())
            findNavController().navigate(action)
        }
        binding.recyclerViewOrders.adapter = orderAdapter
        binding.recyclerViewOrders.visibility = View.GONE

        loadOrders(listOf(OrderStatus.CANCELLED, OrderStatus.DELIVERED))
    }

    private fun loadOrders(statuses: List<OrderStatus>) {
        val userId = (requireActivity().application as UserApplication).userId

        if (userId == null) {
            Log.e("OrdersFragment", "Ошибка: userId равен null")
            Toast.makeText(
                requireContext(),
                "Не удалось загрузить заказы. Попробуйте снова.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Преобразуем статусы в строки
                val statusStrings = statuses.map { it.name }

                // Получаем заказы с учётом нескольких статусов
                val orderList = orderTableDao.getOrdersWithItemsByUserIdAndStatuses(userId, statusStrings) ?: emptyList()

                withContext(Dispatchers.Main) {
                    if (orderList.isEmpty()) {
                        Log.d("OrdersFragment", "Нет заказов с указанными статусами: $statusStrings")
                        binding.recyclerViewOrders.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Нет заказов с выбранными статусами.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.d("OrdersFragment", "Loaded orders with statuses $statusStrings: $orderList")
                        orders.clear()
                        orders.addAll(orderList)
                        orderAdapter.notifyDataSetChanged()
                        binding.recyclerViewOrders.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                // Обрабатываем ошибки
                Log.e("OrdersFragment", "Ошибка при загрузке заказов: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Произошла ошибка при загрузке заказов. Попробуйте снова.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

}

