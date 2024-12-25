package com.nzby.coursekotlin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.nzby.coursekotlin.R
import com.nzby.coursekotlin.UserApplication
import com.nzby.coursekotlin.dao.AppDatabase
import com.nzby.coursekotlin.databinding.FragmentUserListBinding
import com.nzby.coursekotlin.models.UserRole
import com.nzby.coursekotlin.models.User
import com.nzby.coursekotlin.utils.UserAdapter
import kotlinx.coroutines.launch

class UserListFragment : Fragment() {

    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: UserAdapter
    private val userList = mutableListOf<User>()
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserListBinding.inflate(inflater, container, false)
        db = AppDatabase.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(requireContext())
        adapter = UserAdapter(userList) { user, newRole ->
            updateUserRole(user, newRole)
        }
        binding.recyclerViewUsers.adapter = adapter

        loadUsers()
    }

    private fun loadUsers() {
        val userId = (requireActivity().application as UserApplication).userId  // Получаем userId
        lifecycleScope.launch {
            userList.clear()
            userList.addAll(db.userDao().getAllUsersList(userId!!))
            adapter.notifyDataSetChanged()
        }
    }
    private fun updateUserRole(user: User, newRole: UserRole) {
        lifecycleScope.launch {
            try {
                // Обновляем объект user
                user.role = newRole

                // Передаем userId и строковое представление роли (name) в DAO
                db.userDao().updateUserRole(user.id, newRole.name)

                Toast.makeText(requireContext(), "Роль обновлена", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка обновления роли", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
