package com.nzby.coursekotlin.viewmodels

import android.graphics.Bitmap
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
    val productImageBitmap = MutableLiveData<Bitmap?>(null)

    fun saveProduct() {
        val name = productName.value ?: return
        val desc = productDescription.value ?: return
        val price = productPrice.value?.toDoubleOrNull() ?: return
        val imageBitmap = productImageBitmap.value ?: return

        val imageByteArray = bitmapToByteArray(imageBitmap)

        val newProduct = Product(
            name = name,
            descrpt = desc,
            price = price,
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
}

