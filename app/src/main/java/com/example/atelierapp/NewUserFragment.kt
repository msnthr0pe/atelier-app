package com.example.atelierapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.atelierapp.databinding.FragmentNewUserBinding
import com.example.atelierapp.databinding.FragmentTitleBinding
import com.example.atelierapp.ktor.ApiClient
import com.example.atelierapp.ktor.AuthModels
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewUserFragment : Fragment() {

    private var _binding: FragmentNewUserBinding? = null
    private val binding get() = _binding!!

    lateinit var etName: EditText
    lateinit var etSurname: EditText
    lateinit var etPhone: EditText
    lateinit var etEmail: EditText
    lateinit var etPassword: EditText
    lateinit var etRetypePassword: EditText
    lateinit var btnContinue: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewUserBinding.inflate(layoutInflater, container, false)

        etName = binding.etNameNewUser
        etSurname = binding.etSurnameNewUser
        etPhone = binding.etPhoneNewUser
        etEmail = binding.etEmailNewUser
        etPassword = binding.etPasswordNewUser
        etRetypePassword = binding.etRetypePasswordNewUser
        btnContinue = binding.btnContinueNewUser

        btnContinue.setOnClickListener {
            val name = etName.text.toString()
            val surname = etSurname.text.toString()
            val phone = etPhone.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val retypePassword = etRetypePassword.text.toString()

            if (
                name.isNotEmpty() &&
                surname.isNotEmpty() &&
                phone.isNotEmpty() &&
                email.isNotEmpty() &&
                password.isNotEmpty() &&
                retypePassword.isNotEmpty()
                ) {
                if (password == retypePassword) {
                    registerUser(name, surname, phone, email, password)
                } else {
                    Toast.makeText(activity, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }


        return binding.root
    }

    private fun registerUser(
        name: String,
        surname: String,
        phone: String,
        email: String,
        password: String
        ) {
        val call = ApiClient.authApi.register(AuthModels.RegisterRequest(
            email, password, name, surname, phone)
        )
        call.enqueue(object : Callback<AuthModels.AuthResponse> {
            override fun onResponse(
                call: Call<AuthModels.AuthResponse>,
                response: Response<AuthModels.AuthResponse>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Учётная запись создана", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_newUserFragment_to_loginFragment)
                } else {
                    Toast.makeText(requireContext(), "Ошибка создания учётной записи", Toast.LENGTH_SHORT).show()
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