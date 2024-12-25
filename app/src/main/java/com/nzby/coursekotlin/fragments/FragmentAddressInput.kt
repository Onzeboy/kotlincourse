package com.nzby.coursekotlin.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.nzby.coursekotlin.R
import com.nzby.coursekotlin.UserApplication
import com.nzby.coursekotlin.activity.MainActivity
import com.nzby.coursekotlin.dao.AppDatabase
import com.nzby.coursekotlin.models.OrderItem
import com.nzby.coursekotlin.models.OrderTable
import com.nzby.coursekotlin.models.ProductHistory
import kotlinx.coroutines.launch

class FragmentAddressInput : Fragment(R.layout.fragment_address_input) {
    private lateinit var database: AppDatabase
    private lateinit var cityInput: EditText
    private lateinit var streetInput: EditText
    private lateinit var homeInput: EditText
    private lateinit var submitButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cityInput = view.findViewById(R.id.cityInput)
        streetInput = view.findViewById(R.id.streetInput)
        homeInput = view.findViewById(R.id.homeInput)
        submitButton = view.findViewById(R.id.submitAddressButton)

        database = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "app_database"
        ).build()

        val userId = arguments?.getInt("userId") ?: run {
            Toast.makeText(requireContext(), "Ошибка: отсутствует userId", Toast.LENGTH_SHORT).show()
            return
        }

        submitButton.setOnClickListener {
            val city = cityInput.text.toString().trim()
            val street = streetInput.text.toString().trim()
            val home = homeInput.text.toString().trim()

            if (validateInputs(city, street, home)) {
                handleOrderCreation(userId, city, street, home)
            }
        }
    }

    private fun validateInputs(city: String, street: String, home: String): Boolean {
        var isValid = true
        if (city.isEmpty()) {
            cityInput.error = "Введите город"
            isValid = false
        }
        if (street.isEmpty()) {
            streetInput.error = "Введите улицу"
            isValid = false
        }
        if (home.isEmpty()) {
            homeInput.error = "Введите номер дома"
            isValid = false
        }
        return isValid
    }

    private fun handleOrderCreation(userId: Int, city: String, street: String, home: String) {
        lifecycleScope.launch {
            try {
                val newOrder = OrderTable(
                    userId = userId,
                    city = city,
                    street = street,
                    home = home
                )
                val orderId = database.orderTableDao().insert(newOrder)
                saveAndClearCartItems(orderId)
                requireContext()
                navigateToStartScreen()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка оформления заказа", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private suspend fun saveAndClearCartItems(orderId: Long) {
        try {
            val cartItems = database.cartItemDao().getAllCartItems() ?: emptyList()
            if (cartItems.isEmpty()) {
                Toast.makeText(requireContext(), "Корзина пуста", Toast.LENGTH_SHORT).show()
                return
            }
            val userId = (requireActivity().application as UserApplication).userId
            val userCartItems = cartItems.filter { it.userId == userId }
            if (userCartItems.isEmpty()) {
                Toast.makeText(requireContext(), "Ваша корзина пуста", Toast.LENGTH_SHORT).show()
                return
            }
            userCartItems.forEach { cartItem ->
                val product = database.productDao().getProductById(cartItem.productId)
                if (product != null) {
                    val productHistory = ProductHistory(
                        name = product.name,
                        descrpt = product.descrpt,
                        price = product.price,
                        quantity = cartItem.cartQuantity,
                        image = product.image
                    )
                    val productHistoryId = database.productHistoryDao().insert(productHistory)
                    val orderItem = OrderItem(
                        orderId = orderId,
                        productId = productHistoryId,
                        quantity = cartItem.cartQuantity,
                        price = productHistory.price * cartItem.cartQuantity
                    )
                    database.orderItemDao().insert(orderItem)
                }
            }
            userId?.let { database.cartItemDao().deleteByUserId(it) }
            Toast.makeText(requireContext(), "Заказ успешно оформлен", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("Cart", "Ошибка при обработке корзины: ${e.message}", e)
        }
    }

    private fun navigateToStartScreen() {
        val action = FragmentAddressInputDirections.addressInputToOrder()
        findNavController().navigate(action)
    }
}
