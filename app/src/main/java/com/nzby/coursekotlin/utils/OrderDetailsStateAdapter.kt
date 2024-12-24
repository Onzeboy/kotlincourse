package com.nzby.coursekotlin.utils

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nzby.coursekotlin.R
import com.nzby.coursekotlin.databinding.ItemOrderProductBinding
import com.nzby.coursekotlin.models.OrderItemWithProduct

class OrderDetailsStateAdapter(
    private val onProductClick: (OrderItemWithProduct) -> Unit = {}
) : RecyclerView.Adapter<OrderDetailsStateAdapter.OrderDetailsStateViewHolder>() {

    private var orderItemList = listOf<OrderItemWithProduct>()

    fun submitList(items: List<OrderItemWithProduct>) {
        orderItemList = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailsStateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemOrderProductBinding.inflate(inflater, parent, false)
        return OrderDetailsStateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderDetailsStateViewHolder, position: Int) {
        val item = orderItemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = orderItemList.size

    inner class OrderDetailsStateViewHolder(private val binding: ItemOrderProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OrderItemWithProduct) {
            binding.productName.text = item.productName ?: "Unknown Product"
            binding.productPrice.text = "Цена: ₽${item.productPrice ?: "0.00"}"
            item.productImage?.let { bytes ->
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                binding.productImage.setImageBitmap(bitmap)
            } ?: run {
                binding.productImage.setImageResource(R.drawable.placeholder_image)
            }

            // Обработчик клика
            binding.root.setOnClickListener {
                onProductClick(item)
            }
        }
    }
}
