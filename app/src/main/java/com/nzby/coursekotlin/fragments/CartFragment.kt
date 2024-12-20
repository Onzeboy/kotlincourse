package com.nzby.coursekotlin.fragments

import CartAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nzby.coursekotlin.UserApplication
import com.nzby.coursekotlin.dao.AppDatabase
import com.nzby.coursekotlin.dao.CartItemDao
import com.nzby.coursekotlin.dao.ProductDao
import com.nzby.coursekotlin.databinding.FragmentCartBinding
import com.nzby.coursekotlin.models.CartItem
import com.nzby.coursekotlin.models.CartItemWithProduct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var cartItemDao: CartItemDao
    private lateinit var productDao: ProductDao
    private var cartItems: MutableList<CartItem> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUserId = 1 // Пример: замените на реальный способ получения userID

        // Получаем DAO
        cartItemDao = AppDatabase.getInstance(requireContext()).cartItemDao()
        productDao = AppDatabase.getInstance(requireContext()).productDao()

        // Настройка RecyclerView
        binding.recyclerViewCartItems.layoutManager = LinearLayoutManager(requireContext())

        // Загрузка данных корзины
        loadCartItems(currentUserId)

        // Нажатие на кнопку оформления заказа
        binding.btnCheckout.setOnClickListener {
            handleCheckout()
        }
    }

    private fun loadCartItems(currentUserId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val cartItemsFromDb = cartItemDao.getAllCartItems()

            // Загружаем товары из базы и обновляем UI
            launch(Dispatchers.Main) {
                cartItems.clear()
                cartItems.addAll(listOf(cartItemsFromDb))
                updateTotalPrice(currentUserId)
                updateRecyclerView(currentUserId)
            }
        }
    }

    private suspend fun updateTotalPrice(currentUserId: Int) {
        val totalPrice = cartItems
            .filter { it.userId == currentUserId }
            .sumByDouble { cartItem ->
                val product = productDao.getProductById(cartItem.productId)
                (product?.price ?: 0.0)
            }
        binding.tvTotalPrice.text = "Общая стоимость: ₽$totalPrice"
    }

    private suspend fun updateRecyclerView(currentUserId: Int) {
        val cartItemList = cartItems
            .filter { it.userId == currentUserId }
            .map { cartItem ->
                val product = productDao.getProductById(cartItem.productId)
                CartItemWithProduct(
                    cartItemId = cartItem.id,
                    cartUserId = cartItem.userId,
                    productId = cartItem.productId,
                    productName = product?.name ?: "Unknown",
                    productDescription = product?.descrpt ?: "No description",
                    productPrice = product?.price ?: 0.0,
                    productImage = product?.image
                )
            }

        val adapter = CartAdapter(cartItemList) { cartItemWithProduct ->
            removeItemFromCart(cartItemWithProduct)
        }
        binding.recyclerViewCartItems.adapter = adapter
    }

    private fun removeItemFromCart(cartItem: CartItemWithProduct) {
        CoroutineScope(Dispatchers.IO).launch {
            cartItemDao.deleteById(cartItem.cartItemId)

            launch(Dispatchers.Main) {
                cartItems.removeIf { it.id == cartItem.cartItemId }
                updateTotalPrice(cartItem.cartUserId)
                updateRecyclerView(cartItem.cartUserId)
            }
        }
    }

    private fun handleCheckout() {
        val userId = (requireActivity().application as UserApplication).userId  // Получаем userId
        val action = CartFragmentDirections.actionCartFragmentToAddressInputFragment(userId!!)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
