package com.nzby.coursekotlin.utils

import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.bumptech.glide.Glide

object BindingAdapters {

    // Сделайте метод статическим, чтобы избежать ошибки
    @JvmStatic
    @BindingAdapter("imageUri")
    fun bindImageUri(imageView: ImageView, imageUri: Uri?) {
        imageUri?.let {
            Glide.with(imageView.context)
                .load(it)
                .into(imageView)
        }
    }
    // Адаптер для работы с ценой (формат x.xx)
    @BindingAdapter("app:priceText")
    @JvmStatic
    fun setPriceToEditText(view: EditText, value: Double?) {
        val text = value?.let { String.format("%.2f", it) } ?: ""
        if (view.text.toString() != text) {
            view.setText(text)
        }
    }

    @InverseBindingAdapter(attribute = "app:priceText")
    @JvmStatic
    fun getPriceFromEditText(view: EditText): Double {
        return view.text.toString().toDoubleOrNull() ?: 0.00
    }

    @BindingAdapter("app:priceTextAttrChanged")
    @JvmStatic
    fun setPriceTextListener(view: EditText, listener: InverseBindingListener?) {
        view.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                listener?.onChange()
            }
        })
    }

    // Адаптер для работы с количеством (только числа)
    @BindingAdapter("app:quantityText")
    @JvmStatic
    fun setQuantityToEditText(view: EditText, value: Int?) {
        val text = value?.toString() ?: ""
        if (view.text.toString() != text) {
            view.setText(text)
        }
    }

    @InverseBindingAdapter(attribute = "app:quantityText")
    @JvmStatic
    fun getQuantityFromEditText(view: EditText): Int {
        return view.text.toString().toIntOrNull() ?: 0
    }

    @BindingAdapter("app:quantityTextAttrChanged")
    @JvmStatic
    fun setQuantityTextListener(view: EditText, listener: InverseBindingListener?) {
        view.addTextChangedListener {
            listener?.onChange()
        }
    }
}