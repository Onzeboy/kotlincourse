package com.nzby.coursekotlin.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.nzby.coursekotlin.R
import com.nzby.coursekotlin.UserApplication
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

        // Инициализация элементов UI
        cityInput = view.findViewById(R.id.cityInput)
        streetInput = view.findViewById(R.id.streetInput)
        homeInput = view.findViewById(R.id.homeInput)
        submitButton = view.findViewById(R.id.submitAddressButton)

        // Инициализация базы данных
        database = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "app_database"  // Имя вашей базы данных
        ).build()

        // Получение userId из arguments
        val userId = arguments?.getInt("userId") ?: run {
            Toast.makeText(requireContext(), "Ошибка: отсутствует userId", Toast.LENGTH_SHORT)
                .show()
            return
        }

        submitButton.setOnClickListener {
            val city = cityInput.text.toString().trim()
            val street = streetInput.text.toString().trim()
            val home = homeInput.text.toString().trim()

            if (validateInputs(city, street, home)) {
                // Логика создания заказа
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
                // Создаем новый заказ
                val newOrder = OrderTable(
                    userId = userId,
                    city = city,
                    street = street,
                    home = home
                )

                // Вставка нового заказа в базу данных и получение orderId
                val orderId = database.orderTableDao().insert(newOrder)

                // Сохраняем каждый элемент из корзины как OrderItem
                saveAndClearCartItems(orderId)

                // Вывод сообщения об успешном создании заказа
                Toast.makeText(
                    requireContext(),
                    "Заказ успешно создан! ID: $orderId",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка оформления заказа", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private suspend fun saveAndClearCartItems(orderId: Long) {
        try {
            // Получаем все элементы корзины
            val cartItems = database.cartItemDao().getAllCartItems() ?: emptyList()

            if (cartItems.isEmpty()) {
                Log.d("Cart", "Корзина пуста.")
                Toast.makeText(requireContext(), "Корзина пуста", Toast.LENGTH_SHORT).show()
                return
            }

            val userId = (requireActivity().application as UserApplication).userId
            val userCartItems = cartItems.filter { it.userId == userId }

            if (userCartItems.isEmpty()) {
                Log.d("Cart", "Нет товаров в корзине для пользователя с id: $userId")
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
                    try {
                        val productHistoryId = database.productHistoryDao().insert(productHistory)
                        Log.d(
                            "Cart",
                            "Товар ${product.name} добавлен в историю с ID $productHistoryId"
                        )
                        val orderItem = OrderItem(
                            orderId = orderId,
                            productId = productHistoryId,
                            quantity = cartItem.cartQuantity,
                            price = productHistory.price * cartItem.cartQuantity
                        )
                        database.orderItemDao().insert(orderItem)
                    }
                    catch (e: Exception){
                        Log.e("Cart", "Ошибка при сохранении товара в истории: ${e.message}", e)
                    }
                } else {
                    Log.w("Cart", "Продукт с id ${cartItem.productId} не найден")
                }
            }

            // Удаляем товары из корзины после их обработки
            if (userId != null) {
                database.cartItemDao().deleteByUserId(userId)
            }
            Log.d("Cart", "Корзина пользователя с id: $userId очищена")

            Toast.makeText(requireContext(), "Заказ успешно оформлен", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("Cart", "Ошибка при обработке корзины: ${e.message}", e)
        }
    }

}
