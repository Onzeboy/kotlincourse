package com.nzby.coursekotlin.fragments

import ProductAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nzby.coursekotlin.dao.AppDatabase
import com.nzby.coursekotlin.databinding.FragmentProductListBinding
import com.nzby.coursekotlin.models.Product
import com.nzby.coursekotlin.models.CartItem
import com.nzby.coursekotlin.UserApplication
import com.nzby.coursekotlin.dao.CartItemDao
import com.nzby.coursekotlin.dao.ProductDao
import com.nzby.coursekotlin.viewmodels.ProductListViewModel
import com.nzby.coursekotlin.viewmodels.ProductListViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductListFragment : Fragment() {

    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProductListViewModel
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем контекст приложения и DAO
        val application = requireActivity().application
        val cartItemDao = AppDatabase.getInstance(requireContext()).cartItemDao()
        val productDao = AppDatabase.getInstance(requireContext()).productDao()

        // Получаем ViewModel
        val viewModelFactory = ProductListViewModelFactory(
            productDao,
            cartItemDao,
            application
        )
        viewModel = ViewModelProvider(this, viewModelFactory).get(ProductListViewModel::class.java)

        // Инициализация адаптера
        adapter = ProductAdapter { product, quantity ->
            addToCart(product, quantity, cartItemDao, productDao) // Передаем продукт и количество в корзину
        }

        // Настройка RecyclerView
        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewProducts.adapter = adapter

        // Наблюдаем за изменением данных в списке продуктов
        viewModel.productList.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)
        }
    }

    private fun addToCart(product: Product, quantity: Int, cartItemDao: CartItemDao, productDao: ProductDao) {
        val userId = (requireActivity().application as UserApplication).userId

        if (userId == null) {
            Toast.makeText(requireContext(), "Ошибка: пользователь не авторизован", Toast.LENGTH_SHORT).show()
            return
        }

        // Выполняем запрос в фоновом потоке
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Проверяем, достаточно ли товара на складе
                val productInDb = productDao.getProductById(product.id)
                if (productInDb == null || productInDb.quantity < quantity) {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(requireContext(), "Недостаточно товара на складе", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // Проверяем, существует ли уже этот продукт в корзине
                val existingCartItem = cartItemDao.getCartItemByUserIdAndProductId(userId, product.id)
                if (existingCartItem != null) {
                    // Если продукт уже в корзине, увеличиваем его количество
                    val newQuantity = existingCartItem.cartQuantity + quantity
                    cartItemDao.updateQuantity(existingCartItem.id, newQuantity)
                } else {
                    // Если продукта нет в корзине, добавляем новый элемент
                    val newCartItem = CartItem(
                        productId = product.id,
                        userId = userId,
                        cartQuantity = quantity
                    )
                    cartItemDao.insert(newCartItem)
                }

                // Обновляем количество товара в таблице Product
                productDao.updateQuantity(product.id, productInDb.quantity - quantity)

                // Уведомляем пользователя на главном потоке
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(), "Товар добавлен в корзину", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                // Обрабатываем ошибки
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(), "Ошибка при добавлении товара: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
