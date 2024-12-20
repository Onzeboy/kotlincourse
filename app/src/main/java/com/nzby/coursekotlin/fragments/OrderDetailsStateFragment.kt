package com.nzby.coursekotlin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nzby.coursekotlin.R
import com.nzby.coursekotlin.models.OrderStatus

class OrderDetailStateFragment : Fragment() {

    private lateinit var orderIdTextView: TextView
    private lateinit var orderCustomerNameTextView: TextView
    private lateinit var orderDateTextView: TextView
    private lateinit var orderTotalPriceTextView: TextView
    private lateinit var orderStatusTextView: TextView
    private lateinit var statusSpinner: Spinner
    private lateinit var confirmStatusButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order_details_state, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Привязка элементов
        orderIdTextView = view.findViewById(R.id.orderIdTextView)
        orderCustomerNameTextView = view.findViewById(R.id.orderCustomerNameTextView)
        orderDateTextView = view.findViewById(R.id.orderDateTextView)
        orderTotalPriceTextView = view.findViewById(R.id.orderTotalPriceTextView)
        orderStatusTextView = view.findViewById(R.id.orderStatusTextView)
        statusSpinner = view.findViewById(R.id.statusSpinner)
        confirmStatusButton = view.findViewById(R.id.confirmStatusButton)

        // Получаем ID заказа из аргументов
        val orderId = arguments?.getInt("ORDER_ID", 0) ?: return

        // Инициализация Spinner для выбора статуса
        val statusAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            OrderStatus.values()
        )
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        statusSpinner.adapter = statusAdapter

        // Загружаем подробности заказа
        loadOrderDetails(orderId)

        // Обработчик нажатия на кнопку "Confirm"
        confirmStatusButton.setOnClickListener {
            updateOrderStatus(orderId)
        }
    }

    private fun loadOrderDetails(orderId: Int) {
        // Пример загрузки данных (здесь вы можете получить данные из вашей базы)
        // Загружаем подробности о заказе (эти данные должны быть реальными)
        orderIdTextView.text = "Order ID: $orderId"
        orderCustomerNameTextView.text = "Customer Name: John Doe" // Пример имени
        orderDateTextView.text = "Order Date: 2024-12-20" // Пример даты
        orderTotalPriceTextView.text = "Total Price: $99.99" // Пример суммы

        // Пример статуса, обычно будет передан из базы данных
        val status = OrderStatus.PENDING
        orderStatusTextView.text = "Status: ${status.name}"

        // Устанавливаем текущий статус в Spinner
        val statusPosition = OrderStatus.values().indexOf(status)
        statusSpinner.setSelection(statusPosition)
    }

    private fun updateOrderStatus(orderId: Int) {
        // Получаем выбранный статус из Spinner
        val selectedStatus = statusSpinner.selectedItem as OrderStatus

        // Обновляем статус заказа в базе данных
        // Это нужно будет сделать через ваш DAO

        // Пример обновления (вам нужно будет внедрить реальную логику)
        Toast.makeText(requireContext(), "Status updated to: ${selectedStatus.name}", Toast.LENGTH_SHORT).show()

        // Обновление UI
        orderStatusTextView.text = "Status: ${selectedStatus.name}"
    }
}
