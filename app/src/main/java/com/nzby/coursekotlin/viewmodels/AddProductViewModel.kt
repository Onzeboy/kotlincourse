package com.nzby.coursekotlin.viewmodels

import android.graphics.Bitmap
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nzby.coursekotlin.dao.ProductDao
import com.nzby.coursekotlin.models.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.math.RoundingMode

class AddProductViewModel(private val productDao: ProductDao) : ViewModel() {

    val productName = MutableLiveData<String>("")
    val productDescription = MutableLiveData<String>("")
    val productPrice = MutableLiveData<String>("")
    val productQuantity = MutableLiveData<Int>(0)
    val productImageBitmap = MutableLiveData<Bitmap?>(null)

    fun saveProduct() {
        val name = productName.value ?: return
        val desc = productDescription.value ?: return
        val price = productPrice.value?.toDoubleOrNull() ?: return
        val imageBitmap = productImageBitmap.value ?: return
        val quantity = productQuantity.value ?: return

        val imageByteArray = bitmapToByteArray(imageBitmap)

        val newProduct = Product(
            name = name,
            descrpt = desc,
            price = price,
            quantity = quantity,
            image = imageByteArray
        )

        viewModelScope.launch(Dispatchers.IO) {
            productDao.insert(newProduct)
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    fun setSelectedImage(bitmap: Bitmap) {
        productImageBitmap.value = bitmap
    }

    companion object {

        // Адаптер для работы с ценой (String)
        @BindingAdapter("app:priceText")
        @JvmStatic
        fun setPriceToEditText(view: EditText, value: String?) {
            if (view.text.toString() != value) {
                view.setText(value)
            }
        }

        @InverseBindingAdapter(attribute = "app:priceText")
        @JvmStatic
        fun getPriceFromEditText(view: EditText): String {
            val text = view.text.toString()
            return try {
                val value = BigDecimal(text).setScale(2, RoundingMode.HALF_UP)
                value.toString()
            } catch (e: Exception) {
                "0.00"
            }
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

        // Адаптер для работы с количеством (Int)
        @BindingAdapter("app:quantityText")
        @JvmStatic
        fun setIntToEditText(view: EditText, value: Int?) {
            if (view.text.toString() != value?.toString()) {
                view.setText(value?.toString() ?: "")
            }
        }

        @InverseBindingAdapter(attribute = "app:quantityText")
        @JvmStatic
        fun getIntFromEditText(view: EditText): Int {
            return view.text.toString().toIntOrNull() ?: 0
        }

        @BindingAdapter("app:quantityTextAttrChanged")
        @JvmStatic
        fun setIntTextListener(view: EditText, listener: InverseBindingListener?) {
            view.addTextChangedListener {
                listener?.onChange()
            }
        }
    }
}
