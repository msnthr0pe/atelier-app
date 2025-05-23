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
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.atelierapp.databinding.FragmentProfileInfoBinding
import com.example.atelierapp.databinding.FragmentTitleBinding
import com.example.atelierapp.ktor.ApiClient
import kotlinx.coroutines.launch

class ProfileInfoFragment : Fragment() {

    private var _binding: FragmentProfileInfoBinding? = null
    private val binding get() = _binding!!

    private lateinit var etName: EditText
    private lateinit var etSurname: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnChangePassword: Button
    private lateinit var btnLogOut: Button
    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileInfoBinding.inflate(layoutInflater,
            container, false)

        etName = binding.etNameUser
        etSurname = binding.etSurnameUser
        etPhone = binding.etPhoneUser
        etEmail = binding.etEmailUser
        btnChangePassword = binding.btnChangePassword
        btnLogOut = binding.btnLogOut
        tvStatus = binding.tvStatus

        val prefs = requireContext().getSharedPreferences("credentials", Context.MODE_PRIVATE)
        val prefName = prefs.getString("name", "--")
        val prefSurname = prefs.getString("surname", "--")
        val prefPhone = prefs.getString("phone", "--")
        val prefEmail = prefs.getString("email", "--")
        val prefStatus = prefs.getString("status", "--")

        etName.setText(prefName)
        etSurname.setText(prefSurname)
        etPhone.setText(prefPhone)
        etEmail.setText(prefEmail)
        tvStatus.text = prefStatus

        btnLogOut.setOnClickListener {
            findNavController().navigate(R.id.action_profileInfoFragment_to_titleFragment)
        }

        btnChangePassword.setOnClickListener {
            findNavController().navigate(R.id.action_profileInfoFragment_to_passwordChangeFragment)
        }

        binding.customBottomBar3.iconHome.setOnClickListener {
            findNavController().navigate(R.id.action_profileInfoFragment_to_requestsFragment)
        }
        binding.customBottomBar3.iconCalendar.setOnClickListener {
            findNavController().navigate(R.id.action_profileInfoFragment_to_calendarRequestsFragment)
        }

        return binding.root
    }

    companion object {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}