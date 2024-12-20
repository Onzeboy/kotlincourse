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
import com.nzby.coursekotlin.models.CartItem
import com.nzby.coursekotlin.models.OrderItem
import com.nzby.coursekotlin.models.OrderTable
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

            if (city.isEmpty() || street.isEmpty() || home.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Логика создания заказа
            handleOrderCreation(userId, city, street, home)
        }
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
            val cartItems: List<CartItem> = listOf(database.cartItemDao().getAllCartItems())

            // Если cartItems пустой, логируем
            if (cartItems.isEmpty()) {
                Log.d("Cart", "Корзина пуста.")
            }

            val userCartItems = mutableListOf<CartItem>()
            val userId = (requireActivity().application as UserApplication).userId  // Получаем userId

            // Извлекаем все элементы корзины для указанного userId
            for (cartItem in cartItems) {
                if (cartItem.userId == userId) {
                    userCartItems.add(cartItem)
                }
            }

            // Если нет товаров в корзине для пользователя, выводим сообщение
            if (userCartItems.isEmpty()) {
                Log.d("Cart", "Нет товаров в корзине для пользователя с id: $userId")
            }

            // Для каждого элемента корзины создаем OrderItem
            userCartItems.forEach { cartItem ->
                // Получаем цену товара
                val price = database.productDao().getProductById(productId = cartItem.productId)

                if (price != null) {
                    // Создаем новый объект OrderItem
                    val orderItem = OrderItem(
                        orderId = orderId,
                        productId = cartItem.productId,
                        price = price.price,
                    )

                    // Вставляем OrderItem в базу данных
                    try {
                        database.orderItemDao().insert(orderItem)
                    } catch (e: Exception) {
                        // Обрабатываем ошибку вставки
                        Toast.makeText(
                            requireContext(),
                            "Ошибка при сохранении товара в заказе",
                            Toast.LENGTH_SHORT
                        ).show()
                        e.printStackTrace()
                    }
                } else {
                    // Продукт не найден, логируем или обрабатываем ошибку
                    Toast.makeText(
                        requireContext(),
                        "Продукт с id ${cartItem.productId} не найден.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: Exception) {
            // Логируем общие ошибки
            Log.e("Cart", "Ошибка при сохранении товаров из корзины", e)
            Toast.makeText(requireContext(), "Произошла ошибка при обработке корзины", Toast.LENGTH_SHORT).show()
        }
    }

}
