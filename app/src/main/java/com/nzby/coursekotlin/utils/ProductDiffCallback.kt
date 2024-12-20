package com.nzby.coursekotlin.utils

import androidx.recyclerview.widget.DiffUtil
import com.nzby.coursekotlin.models.Product

class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {

    // Сравниваем два объекта по уникальному идентификатору (id)
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.id == newItem.id
    }

    // Проверка, одинаковы ли данные объектов (например, имя, цена и т. д.)
    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }
}
