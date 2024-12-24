package com.nzby.coursekotlin.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.nzby.coursekotlin.R
import com.nzby.coursekotlin.UserApplication
import com.nzby.coursekotlin.dao.AppDatabase
import com.nzby.coursekotlin.databinding.ActivityMainBinding
import com.nzby.coursekotlin.models.UserRole
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPrefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isLoggedIn = sharedPrefs.getBoolean("is_logged_in", false)

        if (!isLoggedIn) {
            navigateToLogin()
            return
        }

        val userId = sharedPrefs.getInt("user_id", -1)
        if (userId == -1) {
            navigateToLogin()
            return
        }

        // Сохраняем userId в UserApplication
        (application as UserApplication).userId = userId

        val username = sharedPrefs.getString("name", "Неизвестный пользователь")
        val roleString = sharedPrefs.getString("role", "ROLE_USER") ?: "ROLE_USER"
        val userRole = UserRole.valueOf(roleString)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        val userNameTextView = navView.getHeaderView(0).findViewById<TextView>(R.id.userName)
        userNameTextView.text = username

        val userRoleTextView = navView.getHeaderView(0).findViewById<TextView>(R.id.userRole)
        userRoleTextView.text = userRole.toDisplayName()

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onStart() {
        super.onStart()

        val sharedPrefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val roleString = sharedPrefs.getString("role", "ROLE_USER")
        val userRole = UserRole.valueOf(roleString ?: "ROLE_USER")

        val navView: NavigationView = binding.navView
        val menu = navView.menu
        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)
            val itemId = menuItem.itemId
            when (itemId) {
                R.id.nav_order -> menuItem.isVisible = userRole == UserRole.ROLE_USER
                R.id.nav_finished_order -> menuItem.isVisible = userRole == UserRole.ROLE_USER
                R.id.nav_product_list -> menuItem.isVisible = userRole == UserRole.ROLE_USER
                R.id.nav_cart -> menuItem.isVisible = userRole == UserRole.ROLE_USER
                R.id.nav_add_product -> menuItem.isVisible = userRole == UserRole.ROLE_ADMIN
                R.id.nav_order_summary -> menuItem.isVisible = userRole == UserRole.ROLE_ADMIN
                R.id.nav_admin_product -> menuItem.isVisible = userRole == UserRole.ROLE_ADMIN
                R.id.nav_logout -> {
                    menuItem.setOnMenuItemClickListener {
                        performLogout() // Выполнить логаут
                        true
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }


    private fun performLogout() {
        val sharedPrefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            clear() // Очищаем данные авторизации
            apply()
        }
        navigateToLogin()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
