package com.nzby.coursekotlin.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nzby.coursekotlin.dao.AppDatabase
import com.nzby.coursekotlin.dao.ProductDao
import com.nzby.coursekotlin.databinding.FragmentAddProductBinding
import com.nzby.coursekotlin.viewmodels.AddProductViewModel
import com.nzby.coursekotlin.viewmodels.AddProductViewModelFactory
import java.io.InputStream

class NewProductActivity : Fragment() {

    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AddProductViewModel

    // Инициализация DAO через ленивую загрузку
    private val productDao: ProductDao by lazy {
        AppDatabase.getInstance(requireContext()).productDao()
    }

    // Регистрация контракта для выбора изображения
    private val selectImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val bitmap = getBitmapFromUri(it)
                bitmap?.let(viewModel::setSelectedImage) // Упрощенный вызов метода ViewModel
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализация binding
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)

        // Инициализация ViewModel
        viewModel = ViewModelProvider(
            this,
            AddProductViewModelFactory(productDao)
        ).get(AddProductViewModel::class.java)

        // Привязка ViewModel к Binding
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Устанавливаем обработчик нажатия для кнопки выбора изображения
        binding.buttonSelectImage.setOnClickListener {
            selectImage()
        }

        // Устанавливаем обработчик нажатия для кнопки сохранения
        binding.buttonSave.setOnClickListener {
            viewModel.saveProduct()
        }
    }

    private fun selectImage() {
        selectImageLauncher.launch("image/*") // Запуск выбора изображения
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
