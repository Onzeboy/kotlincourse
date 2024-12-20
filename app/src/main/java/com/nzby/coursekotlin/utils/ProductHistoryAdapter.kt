package com.nzby.coursekotlin.utils

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nzby.coursekotlin.R
import com.nzby.coursekotlin.models.Product

class ProductHistoryAdapter(private val products: List<Product>) :
    RecyclerView.Adapter<ProductHistoryAdapter.ProductHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHistoryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductHistoryViewHolder, position: Int) {
        val product = products[position]
        holder.productName.text = product.name
        holder.productPrice.text = "Цена: ${product.price} ₽"

        // Если изображение есть, конвертируем его из ByteArray в Bitmap
        product.image?.let {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            holder.productImage.setImageBitmap(bitmap)
        }
    }

    override fun getItemCount(): Int = products.size

    class ProductHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.product_image)
        val productName: TextView = view.findViewById(R.id.product_name)
        val productPrice: TextView = view.findViewById(R.id.product_price)
    }
}
