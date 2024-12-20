package com.nzby.coursekotlin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.nzby.coursekotlin.dao.AppDatabase
import com.nzby.coursekotlin.dao.OrderItemDao
import com.nzby.coursekotlin.dao.OrderTableDao
import com.nzby.coursekotlin.databinding.FragmentOrderDetailsBinding
import com.nzby.coursekotlin.models.OrderItemWithProduct
import com.nzby.coursekotlin.utils.OrderItemAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderDetailsFragment : Fragment() {

    private lateinit var database: AppDatabase
    private lateinit var binding: FragmentOrderDetailsBinding
    private lateinit var orderTableDao: OrderTableDao
    private lateinit var orderItemDao: OrderItemDao
    private lateinit var orderItemAdapter: OrderItemAdapter
    private val orderItems = mutableListOf<OrderItemWithProduct>()

    private val args: OrderDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация базы данных
        database = AppDatabase.getInstance(requireContext())
        orderTableDao = database.orderTableDao()
        orderItemDao = database.orderItemDao()

        // Инициализация RecyclerView
        orderItemAdapter = OrderItemAdapter(orderItems)
        binding.orderProductsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderItemAdapter
        }

        // Загрузка данных заказа
        loadOrderDetails(args.orderId)
    }

    private fun loadOrderDetails(orderId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val order = orderTableDao.getOrderById(orderId)
                val itemsArray = orderItemDao.getOrderItemsWithProduct(orderId)

                val orderItemsWithProduct = itemsArray.map { item ->
                    OrderItemWithProduct(
                        productId = item.productId,
                        productName = item.productName,
                        productDescription = item.productDescription,
                        productImage = item.productImage,
                        productPrice = item.productPrice,
                        itemTotalPrice = item.itemTotalPrice
                    )
                }
                val totalPrice = orderItemsWithProduct.sumOf { it.itemTotalPrice }
                launch(Dispatchers.Main) {
                    if (order != null) {
                        binding.orderDate.text = "Дата  ${DateConverter.fromTimestamp(order.createdAt)}"
                        binding.orderAddress.text =
                            "Адрес: ${order.city}, ${order.street}, ${order.home}"
                        binding.orderTotalPrice.text = "Итог: ₽${"%.2f".format(totalPrice)}"
                        binding.orderStatus.text = "Статус: ${order.status}"

                        // Обновление списка продуктов
                        orderItems.clear()
                        orderItems.addAll(orderItemsWithProduct)
                        orderItemAdapter.notifyDataSetChanged()
                    } else {
                        binding.orderStatus.text = "Заказ не найден"
                    }
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    binding.orderStatus.text = "Ошибка загрузки данных"
                }
                e.printStackTrace()
            }
        }
    }
    object DateConverter {
        private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

        fun fromTimestamp(timestamp: Long): String {
            return dateFormat.format(Date(timestamp))
        }
    }
}

