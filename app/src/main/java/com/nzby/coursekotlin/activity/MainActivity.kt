package com.nzby.coursekotlin.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.nzby.coursekotlin.R
import com.nzby.coursekotlin.UserApplication
import com.nzby.coursekotlin.databinding.ActivityMainBinding
import com.nzby.coursekotlin.models.UserRole

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPrefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isLoggedIn = sharedPrefs.getBoolean("is_logged_in", false)

        if (!isLoggedIn) {
            // Если пользователь не авторизован, отправляем его на экран авторизации
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Завершаем MainActivity, чтобы не возвращаться к ней
            return
        }

        // Получаем userID из SharedPreferences
        val userID = sharedPrefs.getInt("user_id", -1)
        if (userID == -1) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Сохраняем userId в UserApplication
        val app = applicationContext as UserApplication
        app.userId = userID

        // Если авторизован, продолжаем обычную работу MainActivity
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Получаем имя пользователя и роль из SharedPreferences
        val username = sharedPrefs.getString("username", "Не авторизован")
        val roleString = sharedPrefs.getString("role", "ROLE_USER") // Роль по умолчанию

        // Преобразуем строковую роль в перечисление UserRole
        val userRole = UserRole.valueOf(roleString ?: "ROLE_USER")

        // Обновляем TextView с именем пользователя в боковом меню
        val userNameTextView = navView.getHeaderView(0).findViewById<TextView>(R.id.userName)
        userNameTextView.text = username

        // Обновляем TextView с ролью пользователя
        val userRoleTextView = navView.getHeaderView(0).findViewById<TextView>(R.id.userRole)
        userRoleTextView.text = userRole.toDisplayName() // Отображаем роль с помощью toDisplayName()

        // Настройка навигации через боковое меню
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onStart() {
        super.onStart()

        val sharedPrefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val roleString = sharedPrefs.getString("role", "ROLE_USER")
        val userRole = UserRole.valueOf(roleString ?: "ROLE_USER")

        // Получаем NavigationView
        val navView: NavigationView = binding.navView
        val menu = navView.menu
        // Пробегаемся по всем элементам меню и настраиваем их видимость
        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)
            // Получаем группу, в которой находится этот элемент
            val itemId = menuItem.itemId
            when (itemId) {
                R.id.nav_order -> {
                    menuItem.isVisible = userRole == UserRole.ROLE_USER
                }
                R.id.nav_product_list -> {
                    menuItem.isVisible = userRole == UserRole.ROLE_USER
                }
                R.id.nav_cart -> {
                    menuItem.isVisible = userRole == UserRole.ROLE_USER
                }
                R.id.nav_add_product -> {
                    menuItem.isVisible = userRole == UserRole.ROLE_ADMIN
                }
                // Добавьте другие элементы меню по необходимости
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu) // Здесь R.menu.main должен указывать на правильный XML-файл
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
