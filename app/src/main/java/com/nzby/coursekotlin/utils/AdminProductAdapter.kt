package com.nzby.coursekotlin.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.nzby.coursekotlin.databinding.ItemAdminProductBinding
import com.nzby.coursekotlin.models.Product

class AdminProductAdapter(
    private val products: MutableList<Product>,
    private val onQuantityChanged: (Product, Int) -> Unit,
    private val onDelete: (Product) -> Unit
) : RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemAdminProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    inner class ProductViewHolder(private val binding: ItemAdminProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.textViewProductName.text = product.name
            binding.editTextProductPrice.setText(product.price.toString())
            binding.editTextProductQuantity.setText(product.quantity.toString())

            // Сохранение изменений
            binding.buttonSaveChanges.setOnClickListener {
                val newQuantity = binding.editTextProductQuantity.text.toString().toIntOrNull()
                if (newQuantity != null) {
                    onQuantityChanged(product, newQuantity)
                } else {
                    Toast.makeText(
                        binding.root.context,
                        "Введите корректное количество",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            // Удаление продукта
            binding.buttonDeleteProduct.setOnClickListener {
                onDelete(product)
            }
        }
    }
}
