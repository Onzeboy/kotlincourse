package com.nzby.coursekotlin.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nzby.coursekotlin.R
import com.nzby.coursekotlin.databinding.FragmentOrderDetailsStateBinding
import com.nzby.coursekotlin.dao.AppDatabase
import com.nzby.coursekotlin.dao.OrderItemDao
import com.nzby.coursekotlin.dao.OrderTableDao
import com.nzby.coursekotlin.models.OrderItemWithProduct
import com.nzby.coursekotlin.utils.OrderDetailsStateAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.nzby.coursekotlin.fragments.OrderDetailsStateFragmentArgs
import com.nzby.coursekotlin.models.OrderStatus

class OrderDetailsStateFragment : Fragment() {

    private lateinit var binding: FragmentOrderDetailsStateBinding
    private lateinit var adapter: OrderDetailsStateAdapter
    private val orderItemList = mutableListOf<OrderItemWithProduct>()

    private val args: OrderDetailsStateFragmentArgs by navArgs()
    private var orderId: Long = -1 // Переданный ID заказа

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Получаем orderId из аргументов
        arguments?.let {
            orderId = it.getLong("ORDER_ID", -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailsStateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getInstance(requireContext())
        val orderTableDao = database.orderTableDao()
        val orderItemDao = database.orderItemDao()

        // Настраиваем Spinner
        setupStatusSpinner()

        // Настраиваем RecyclerView и адаптер
        adapter = OrderDetailsStateAdapter()
        binding.orderSummatyProductsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.orderSummatyProductsRecyclerView.adapter = adapter

        // Загружаем данные
        loadOrderDetails(args.orderId, orderTableDao, orderItemDao)

        // Обработка кнопки подтверждения статуса
        binding.confirmStatusButton.setOnClickListener {
            val selectedStatusString = binding.statusSpinner.selectedItem.toString()

            try {
                // Преобразуем строку в OrderStatus
                val selectedStatus = OrderStatus.valueOf(selectedStatusString)

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // Получаем текущий заказ из базы данных
                        val order = orderTableDao.getOrderById(args.orderId)
                        if (order != null) {
                            // Обновляем статус в базе данных
                            order.status = selectedStatus
                            orderTableDao.updateOrderWithoutParameters(order)

                            // Обновляем UI на главном потоке
                            launch(Dispatchers.Main) {
                                binding.orderStatusTextView.text = "Статус: ${selectedStatus}"
                                Log.d("OrderDetails", "Статус заказа обновлен: $selectedStatus")
                            }
                        } else {
                            launch(Dispatchers.Main) {
                                Log.e("OrderDetails", "Ошибка: заказ не найден")
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        launch(Dispatchers.Main) {
                            Log.e("OrderDetails", "Ошибка при обновлении статуса: ${e.message}")
                        }
                    }
                }
            } catch (e: IllegalArgumentException) {
                Log.e("OrderDetails", "Ошибка: Неверный статус: $selectedStatusString")
            }
        }
    }



    private fun setupStatusSpinner() {
        // Массив со статусами заказа
        val statusList = OrderStatus.values().map { it.name }
        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            statusList
        )

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.statusSpinner.adapter = spinnerAdapter

        // Установка значения по умолчанию
        binding.statusSpinner.setSelection(0)
    }
    private fun loadOrderDetails(orderId: Long, orderTableDao: OrderTableDao, orderItemDao: OrderItemDao) {
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
                        productQuantity = item.productQuantity,
                        itemTotalPrice = item.itemTotalPrice
                    )
                }

                val totalPrice = orderItemsWithProduct.sumOf { it.itemTotalPrice }

                launch(Dispatchers.Main) {
                    if (order != null) {
                        val database = AppDatabase.getInstance(requireContext())
                        val userDao = database.userDao()
                        val UID = userDao.getUserById(order.userId)
                        binding.orderDateTextView.text = "Дата: ${DateConverter.fromTimestamp(order.createdAt)}"
                        binding.orderAddressTextView.text = "Адрес: ${order.city}, ${order.street}, ${order.home}"
                        binding.orderTotalPriceTextView.text = "Итог: ₽${"%.2f".format(totalPrice)}"
                        binding.orderStatusTextView.text = "Статус: ${order.status}"
                        binding.orderCustomerNameTextView.text = "Заказчик: ${UID!!.username}"

                        // Обновление списка продуктов
                        orderItemList.clear()
                        orderItemList.addAll(orderItemsWithProduct)
                        adapter.submitList(orderItemList.toList())
                    } else {
                        binding.orderStatusTextView.text = "Заказ не найден"
                    }
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    binding.orderStatusTextView.text = "Ошибка загрузки данных"
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