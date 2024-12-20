package com.nzby.coursekotlin.utils

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
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
}