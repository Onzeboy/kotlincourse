package com.nzby.coursekotlin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.nzby.coursekotlin.dao.AppDatabase
import com.nzby.coursekotlin.dao.ProductDao
import com.nzby.coursekotlin.databinding.FragmentAdminProductListBinding
import com.nzby.coursekotlin.models.Product
import com.nzby.coursekotlin.utils.AdminProductAdapter
import kotlinx.coroutines.launch

class FragmentAdminProductList : Fragment() {

    private var _binding: FragmentAdminProductListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AdminProductAdapter
    private lateinit var productDao: ProductDao
    private val productList = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminProductListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productDao = AppDatabase.getInstance(requireContext()).productDao()

        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(requireContext())
        adapter = AdminProductAdapter(productList,
            onQuantityChanged = { product, newQuantity ->
                updateProductQuantity(product, newQuantity)
            },
            onDelete = { product ->
                deleteProduct(product)
            }
        )
        binding.recyclerViewProducts.adapter = adapter

        loadProducts()
    }

    private fun loadProducts() {
        lifecycleScope.launch {
            productList.clear()
            productList.addAll(productDao.getAllProducts())
            adapter.notifyDataSetChanged()
        }
    }

    private fun updateProductQuantity(product: Product, newQuantity: Int) {
        lifecycleScope.launch {
            productDao.updateQuantity(product.id, newQuantity)
            product.quantity = newQuantity
            adapter.notifyDataSetChanged()
            Toast.makeText(requireContext(), "Количество обновлено", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteProduct(product: Product) {
        lifecycleScope.launch {
            try {
                productDao.deleteProduct(product)
                productList.remove(product)
                adapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "Продукт удален", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка удаления продукта", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

