package com.nzby.coursekotlin.ulits

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nzby.coursekotlin.R
import com.nzby.coursekotlin.models.OrderTable

class OrderSummaryAdapter(
    private val orders: List<OrderTable>,
    private val onOrderClick: (OrderTable) -> Unit
) : RecyclerView.Adapter<OrderSummaryAdapter.OrderSummaryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderSummaryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_order_summary, parent, false)
        return OrderSummaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderSummaryViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    inner class OrderSummaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val orderCustomerNameTextView: TextView = itemView.findViewById(R.id.orderCustomerNameTextView)
        private val orderTotalPriceTextView: TextView = itemView.findViewById(R.id.orderTotalPriceTextView)
        private val orderStatusTextView: TextView = itemView.findViewById(R.id.orderStatusTextView)

        fun bind(order: OrderTable) {
            orderCustomerNameTextView.text = "Customer Name: ${order.userId}"  // Используйте имя пользователя, если это необходимо
            orderTotalPriceTextView.text = "Total Price: $${order.totalPrice}"
            orderStatusTextView.text = "Status: ${order.status.name}"

            itemView.setOnClickListener {
                onOrderClick(order)
            }
        }
    }
}
