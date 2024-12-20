package com.nzby.coursekotlin.fragments
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nzby.coursekotlin.R
import com.nzby.coursekotlin.utils.OrderDetailsStateAdapter
import com.nzby.coursekotlin.dao.OrderTableDao
import com.nzby.coursekotlin.models.OrderDetails
import kotlinx.coroutines.launch

class OrderDetailsStateFragment : Fragment() {

    private lateinit var orderIdTextView: TextView
    private lateinit var orderCustomerNameTextView: TextView
    private lateinit var orderDateTextView: TextView
    private lateinit var orderTotalPriceTextView: TextView
    private lateinit var orderStatusTextView: TextView
    private lateinit var statusSpinner: Spinner
    private lateinit var confirmStatusButton: Button
    private lateinit var orderSummaryProductsRecyclerView: RecyclerView

    private var orderTableDao: OrderTableDao? = null
    private var orderId: Long = -1 // Здесь хранится ID заказа, который передаётся в фрагмент

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Инфлейтим макет для этого фрагмента
        return inflater.inflate(R.layout.fragment_order_details_state, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализируем представления (вьюшки)
        orderIdTextView = view.findViewById(R.id.orderIdTextView)
        orderCustomerNameTextView = view.findViewById(R.id.orderCustomerNameTextView)
        orderDateTextView = view.findViewById(R.id.orderDateTextView)
        orderTotalPriceTextView = view.findViewById(R.id.orderTotalPriceTextView)
        orderStatusTextView = view.findViewById(R.id.orderStatusTextView)
        statusSpinner = view.findViewById(R.id.statusSpinner)
        confirmStatusButton = view.findViewById(R.id.confirmStatusButton)
        orderSummaryProductsRecyclerView = view.findViewById(R.id.orderSummatyProductsRecyclerView)

        // Настроим RecyclerView для отображения списка продуктов в заказе
        orderSummaryProductsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        orderSummaryProductsRecyclerView.adapter = OrderDetailsStateAdapter()

        // Настроим Spinner для выбора нового статуса заказа
        val statusOptions = listOf("PENDING", "PROCESSING", "COMPLETED", "CANCELED")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statusOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        statusSpinner.adapter = spinnerAdapter

        // Загружаем детали заказа
        loadOrderDetails()

        // Обработчик для кнопки подтверждения изменения статуса
        confirmStatusButton.setOnClickListener {
            val selectedStatus = statusSpinner.selectedItem.toString()
            updateOrderStatus(selectedStatus)  // Обновляем статус заказа
        }
    }

    private fun updateOrderStatus(newStatus: String) {
        // Обновляем текст статуса на экране
        orderStatusTextView.text = "Status: $newStatus"

        // Меняем цвет текста в зависимости от статуса
        val color = when (newStatus) {
            "COMPLETED" -> android.R.color.holo_green_dark  // Зеленый для завершенного
            "CANCELED" -> android.R.color.holo_red_dark   // Красный для отмененного
            else -> android.R.color.black  // Черный для прочих статусов
        }
        orderStatusTextView.setTextColor(resources.getColor(color, null))
    }

    private fun loadOrderDetails() {
        lifecycleScope.launch {
            // Запрос к DAO для получения информации о заказе по ID
            val orderDetails = orderTableDao?.getOrderDetailsById(orderId.toInt()) ?: emptyList()
        }
    }
}
