package com.nzby.coursekotlin.viewmodels

import android.graphics.Bitmap
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nzby.coursekotlin.dao.ProductDao
import com.nzby.coursekotlin.models.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

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
        // Устанавливаем значение из Int в строку для EditText
        @BindingAdapter("android:text")
        @JvmStatic
        fun setIntToEditText(view: EditText, value: Int?) {
            if (view.text.toString() != value?.toString()) {
                view.setText(value?.toString() ?: "")
            }
        }

        // Получаем значение из строки и преобразуем его в Int
        @InverseBindingAdapter(attribute = "android:text")
        @JvmStatic
        fun getIntFromEditText(view: EditText): Int {
            return view.text.toString().toIntOrNull() ?: 0
        }

        // Связываем изменения текста с двусторонним биндингом
        @BindingAdapter("android:textAttrChanged")
        @JvmStatic
        fun setIntTextListener(view: EditText, listener: InverseBindingListener?) {
            view.addTextChangedListener {
                listener?.onChange()
            }
        }
    }
}
