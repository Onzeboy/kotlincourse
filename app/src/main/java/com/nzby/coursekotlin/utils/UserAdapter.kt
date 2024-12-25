package com.nzby.coursekotlin.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nzby.coursekotlin.databinding.ItemUserBinding
import com.nzby.coursekotlin.models.User
import com.nzby.coursekotlin.models.UserRole

class UserAdapter(
    private val users: MutableList<User>,
    private val onRoleChanged: (User, UserRole) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    inner class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.textViewUsername.text = user.username
            binding.textViewPhone.text = user.phone

            // Получение списка ролей из Enum
            val roles = UserRole.values()
            val roleNames = roles.map { it.name }

            // Создание адаптера для Spinner
            val spinnerAdapter = ArrayAdapter(
                binding.root.context,
                android.R.layout.simple_spinner_item,
                roleNames
            )
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerRole.adapter = spinnerAdapter

            // Установка текущей роли
            binding.spinnerRole.setSelection(roles.indexOf(user.role))

            // Обработка изменения роли
            binding.spinnerRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedRole = roles[position]
                    if (selectedRole != user.role) {
                        onRoleChanged(user, selectedRole)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Ничего не делаем
                }
            }
        }
    }
}
