package com.example.atelierapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.atelierapp.databinding.FragmentLoginBinding
import com.example.atelierapp.ktor.ApiClient
import com.example.atelierapp.ktor.AuthModels
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    lateinit var etEmail: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogin: Button
    lateinit var tvRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        etEmail = binding.etLoginEmail
        etPassword = binding.etLoginPassword
        btnLogin = binding.loginBtn
        tvRegister = binding.tvRegister
        btnLogin.setOnClickListener {
            val login = etEmail.text.toString()
            val password = etPassword.text.toString()
            if (login.isNotEmpty() && password.isNotEmpty()) {
                loginUser(login, password)
            } else {
                Toast.makeText(activity, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_newUserFragment)
        }

        return binding.root
    }

    private fun loginUser(email: String, password: String) {
        val call = ApiClient.authApi.login(AuthModels.LoginRequest(email, password))
        call.enqueue(object : Callback<AuthModels.AuthResponse> {
            override fun onResponse(
                call: Call<AuthModels.AuthResponse>,
                response: Response<AuthModels.AuthResponse>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Успешный вход", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_loginFragment_to_requestsFragment)
                } else {
                    Toast.makeText(requireContext(), "Ошибка входа", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AuthModels.AuthResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}