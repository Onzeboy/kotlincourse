package com.nzby.coursekotlin.fragments

import ProductAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
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
            // Здесь вызываем метод addToCart
            addToCart(product, quantity, cartItemDao, productDao)
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
            val userId = (requireActivity().application as UserApplication).userId  // Получаем userId

            // Выполняем запрос в фоновом потоке
            CoroutineScope(Dispatchers.IO).launch {

                    val cartItem = CartItem(
                        productId = product.id,
                        userId = userId!!
                    )

                    // Добавляем в корзину в фоновом потоке
                    cartItemDao.insert(cartItem)
                }
            }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
