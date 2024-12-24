package com.nzby.coursekotlin.fragments

import CartAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

        val currentUserId = (requireActivity().application as UserApplication).userId  // Получаем userId

        // Получаем DAO
        cartItemDao = AppDatabase.getInstance(requireContext()).cartItemDao()
        productDao = AppDatabase.getInstance(requireContext()).productDao()

        // Настройка RecyclerView
        binding.recyclerViewCartItems.layoutManager = LinearLayoutManager(requireContext())

        // Загрузка данных корзины
        if (currentUserId != null) {
            loadCartItems(currentUserId)
        }

        // Нажатие на кнопку оформления заказа
        binding.btnCheckout.setOnClickListener {
            handleCheckout()
        }
    }

    private fun loadCartItems(currentUserId: Int) {
        Log.d("CartFragment", "Метод loadCartItems вызван с userId: $currentUserId")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val cartItemsFromDb = cartItemDao.getAllCartItems() ?: emptyList()

                if (cartItemsFromDb.isEmpty()) {
                    Log.w("CartFragment", "Корзина пуста")
                    launch(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Корзина пуста", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("CartFragment", "Загруженные товары: $cartItemsFromDb")
                    cartItems.clear()
                    cartItems.addAll(cartItemsFromDb)

                    launch(Dispatchers.Main) {
                        updateTotalPrice(currentUserId)
                        updateRecyclerView(currentUserId)
                    }
                }
            } catch (e: Exception) {
                Log.e("CartFragment", "Ошибка загрузки данных корзины", e)
                launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Ошибка загрузки корзины", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun updateTotalPrice(currentUserId: Int) {
        val totalPrice = cartItems
            .filter { it.userId == currentUserId }
            .sumByDouble { cartItem ->
                val product = productDao.getProductById(cartItem.productId )
                (product?.price?.times(cartItem.cartQuantity) ?: 0.0)
            }
        binding.tvTotalPrice.text = "Общая стоимость: ₽$totalPrice"
    }

    private suspend fun updateRecyclerView(currentUserId: Int) {
        if (_binding == null) {
            Log.e("CartFragment", "Binding больше недоступен.")
            return
        }

        val cartItemList = cartItems
            .filter { it.userId == currentUserId }
            .map { cartItem ->
                val product = productDao.getProductById(cartItem.productId)
                CartItemWithProduct(
                    cartItemId = cartItem.id,
                    cartUserId = cartItem.userId,
                    cartQuantity = cartItem.cartQuantity,
                    productId = cartItem.productId,
                    productName = product?.name ?: "Unknown",
                    productDescription = product?.descrpt ?: "No description",
                    productPrice = product?.price ?: 0.0,
                    productImage = product?.image
                )
            }

        if (cartItemList.isEmpty()) {
            Log.w("CartFragment", "Нет данных для отображения.")
            return
        }

        val adapter = CartAdapter(cartItemList) { cartItemWithProduct ->
            removeItemFromCart(cartItemWithProduct)
        }

        binding.recyclerViewCartItems.adapter = adapter
    }

    private fun removeItemFromCart(cartItem: CartItemWithProduct) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Удаляем элемент из базы данных
                cartItemDao.deleteById(cartItem.cartItemId)

                // Обновляем количество продукта в базе данных
                val product = productDao.getProductById(cartItem.productId)
                if (product != null) {
                    product.quantity += cartItem.cartQuantity
                    productDao.update(product)
                }

                // Обновляем UI на главном потоке
                launch(Dispatchers.Main) {
                    cartItems.removeIf { it.id == cartItem.cartItemId }
                    updateTotalPrice(cartItem.cartUserId)
                    updateRecyclerView(cartItem.cartUserId)
                    Toast.makeText(requireContext(), "Товар удален из корзины", Toast.LENGTH_SHORT).show()
                    refreshFragment()
                }
            } catch (e: Exception) {
                // Обрабатываем ошибки
                launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Ошибка при удалении товара: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                Log.e("CartFragment", "Ошибка при удалении товара из корзины", e)
            }
        }
    }
    private fun refreshFragment() {
        parentFragmentManager.beginTransaction()
            .detach(this)  // Отсоединяем текущий фрагмент
            .attach(this)  // Присоединяем его заново
            .commit()       // Применяем изменения
    }

    private fun handleCheckout() {
        val userId = (requireActivity().application as UserApplication).userId
        val action = CartFragmentDirections.actionCartToAddressInput(userId!!)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}