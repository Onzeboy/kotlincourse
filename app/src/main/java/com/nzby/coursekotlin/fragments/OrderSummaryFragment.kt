package com.nzby.coursekotlin.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nzby.coursekotlin.UserApplication
import com.nzby.coursekotlin.dao.AppDatabase
import com.nzby.coursekotlin.dao.OrderItemDao
import com.nzby.coursekotlin.dao.OrderTableDao
import com.nzby.coursekotlin.databinding.FragmentOrdersBinding
import com.nzby.coursekotlin.models.OrderWithItems
import com.nzby.coursekotlin.utils.OrderAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderSummaryFragment : Fragment() {

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
            // Используем правильное имя действия для перехода в OrderDetailsStateFragment
            val action = OrderSummaryFragmentDirections.actionOrdersToOrderDetailState(order.order.id.toLong())
            findNavController().navigate(action)
        }
        binding.recyclerViewOrders.adapter = orderAdapter
        binding.recyclerViewOrders.visibility = View.GONE

        loadOrders()
    }

    private fun loadOrders() {
        val userId = (requireActivity().application as UserApplication).userId
        CoroutineScope(Dispatchers.IO).launch {
            val orderList = orderTableDao.getAllOrdersWithItems()
            launch(Dispatchers.Main) {
                Log.d("OrdersFragment", "Loaded orders: $orderList")
                orders.clear()
                orders.addAll(orderList)
                orderAdapter.notifyDataSetChanged()
                binding.recyclerViewOrders.visibility = View.VISIBLE
            }
        }
    }

}

