package com.example.atelierapp

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.atelierapp.databinding.FragmentNewRequestBinding
import com.example.atelierapp.ktor.ApiClient
import com.example.atelierapp.ktor.AuthModels
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class NewRequestFragment : Fragment() {

    private var _binding: FragmentNewRequestBinding? = null
    private val binding get() = _binding!!

    private lateinit var etNameRequest: EditText
    private lateinit var etDateRequest: EditText
    private lateinit var etTimeRequest: EditText
    private lateinit var etPhoneRequest: EditText
    private lateinit var etEmailRequest: EditText
    private lateinit var etDescriptionRequest: EditText
    private lateinit var btnCreateRequest: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewRequestBinding.inflate(layoutInflater, container, false)

        etNameRequest = binding.etNameRequest
        etDateRequest = binding.etDateRequest
        etTimeRequest = binding.etTimeRequest
        etPhoneRequest = binding.etPhoneRequest
        etEmailRequest = binding.etEmailRequest
        etDescriptionRequest = binding.etDescriptionRequest
        btnCreateRequest = binding.btnContinueNewUser

        btnCreateRequest.setOnClickListener {
            val phone = etPhoneRequest.text.toString()
            val date = etDateRequest.text.toString()
            val name = etNameRequest.text.toString()
            val email = etEmailRequest.text.toString()
            val time = etTimeRequest.text.toString()
            val description = etDescriptionRequest.text.toString()
            if (
                phone.isNotEmpty() &&
                date.isNotEmpty() &&
                name.isNotEmpty() &&
                email.isNotEmpty() &&
                time.isNotEmpty() &&
                description.isNotEmpty()
                ) {
                addClient(date, phone, name, email, time, description)
            } else {
                Toast.makeText(activity, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

    private fun addClient(
        date: String,
        phone: String,
        name: String,
        email: String,
        time: String,
        description: String,
    ) {
        val call = ApiClient.authApi.addClient(
            AuthModels.ClientRequest(
                date = date,
                phone = phone,
                name = name,
                email = email,
                time = time,
                description = description
                )
        )
        call.enqueue(object : Callback<AuthModels.AuthResponse> {
            override fun onResponse(
                call: Call<AuthModels.AuthResponse>,
                response: Response<AuthModels.AuthResponse>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Запись добавлена", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_newRequestFragment_to_requestsFragment)
                } else {
                    Toast.makeText(requireContext(), "Ошибка добавления записи", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AuthModels.AuthResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivCalendarRequest.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = "%02d.%02d.%d".format(dayOfMonth, month + 1, year)
                binding.etDateRequest.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    companion object {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}