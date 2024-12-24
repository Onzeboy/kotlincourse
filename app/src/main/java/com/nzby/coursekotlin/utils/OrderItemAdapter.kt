package com.nzby.coursekotlin.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nzby.coursekotlin.R
import com.nzby.coursekotlin.models.OrderItemWithProduct
import java.io.ByteArrayOutputStream

class OrderItemAdapter(private val orderItems: MutableList<OrderItemWithProduct>) :
    RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder>() {

    // ViewHolder для отображения элементов
    inner class OrderItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.product_name)
        private val productPrice: TextView = itemView.findViewById(R.id.product_price)
        private val productQuantity: TextView = itemView.findViewById(R.id.product_quantity)
        private val productImage: ImageView = itemView.findViewById(R.id.product_image)
        fun toBitmap(bytes: ByteArray?): Bitmap? {
            return bytes?.let {
                BitmapFactory.decodeByteArray(it, 0, it.size)
            }
        }
        // Привязка данных к элементам View
        fun bind(orderItem: OrderItemWithProduct) {
            orderItem.productImage?.let { bytes ->
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                productImage.setImageBitmap(bitmap)
            } ?: run {
                // Если изображения нет, ставим заглушку
                productImage.setImageResource(R.drawable.placeholder_image)
            }
            productName.text = orderItem.productName
            productPrice.text = "Цена: ₽${orderItem.productPrice}"
            productQuantity.text = "Количество ${orderItem.productQuantity}"
        }
    }
    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
    // Создание ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_product, parent, false)
        return OrderItemViewHolder(view)
    }

    // Привязка данных к ViewHolder
    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(orderItems[position])  // Передаем объект OrderItemWithProduct
    }

    // Количество элементов в списке
    override fun getItemCount(): Int = orderItems.size
}
