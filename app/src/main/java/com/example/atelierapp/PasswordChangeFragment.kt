package com.example.atelierapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.atelierapp.databinding.FragmentPasswordChangeBinding
import com.example.atelierapp.ktor.ApiClient
import com.example.atelierapp.ktor.AuthModels
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Serializable
data class PasswordDTO (
    val email: String,
    val password: String
)

class PasswordChangeFragment : Fragment() {

    private var _binding: FragmentPasswordChangeBinding? = null
    private val binding get() = _binding!!

    private lateinit var etOldPassword: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var btnContinue: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPasswordChangeBinding.inflate(layoutInflater, container, false)

        etOldPassword = binding.etOldPassword
        etNewPassword = binding.etNewPassword
        btnContinue = binding.btnContinue
        btnContinue.setOnClickListener{
            val prefs = requireContext().getSharedPreferences("credentials", Context.MODE_PRIVATE)
            val emailPref = prefs.getString("email", "--")
            val correctOldPassword = prefs.getString("password", "--")
            val oldPassword = etOldPassword.text.toString()
            val newPassword = etNewPassword.text.toString()

            if (etOldPassword.text.isNotEmpty() && etNewPassword.text.isNotEmpty()) {

                if (oldPassword == correctOldPassword) {
                    lifecycleScope.launch {
                        editPassword(emailPref!!, newPassword)
                    }
                    prefs.edit {
                        putString("password", newPassword)
                    }
                } else {
                    Toast.makeText(activity, "Неверный старый пароль", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private suspend fun editPassword(email: String, newPassword: String) {
        ApiClient.authApi.updateUserPassword(
            PasswordDTO(email, newPassword)
        )
        findNavController().navigate(R.id.action_passwordChangeFragment_to_profileInfoFragment)
    }

    companion object {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}