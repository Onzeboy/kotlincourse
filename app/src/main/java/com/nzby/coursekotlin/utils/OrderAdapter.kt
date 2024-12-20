package com.nzby.coursekotlin.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nzby.coursekotlin.R
import com.nzby.coursekotlin.models.OrderWithItems
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderAdapter(
    private val orders: List<OrderWithItems>,
    private val onOrderClick: (OrderWithItems) -> Unit // Callback для кликов
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val orderDate: TextView = itemView.findViewById(R.id.order_date)
        private val orderAddress: TextView = itemView.findViewById(R.id.order_address)
        private val orderStatus: TextView = itemView.findViewById(R.id.order_status)
        private val orderID: TextView = itemView.findViewById(R.id.order_id)

        fun bind(order: OrderWithItems) {


            orderDate.text = "Дата заказа: ${DateConverter.fromTimestamp(order.order.createdAt)}"
            orderAddress.text =
                "Адрес: ${order.order.city}, ${order.order.street}, ${order.order.home}"
            orderStatus.text = "Статус: ${order.order.status}"
            orderID.text = "${order.order.id}"

            // Обработчик клика по элементу
            itemView.setOnClickListener {
                onOrderClick(order) // Передача данных при клике
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size
    object DateConverter {
        private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

        fun fromTimestamp(timestamp: Long): String {
            return dateFormat.format(Date(timestamp))
        }
    }
}
