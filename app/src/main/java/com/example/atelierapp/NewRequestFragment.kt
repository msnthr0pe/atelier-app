package com.example.atelierapp

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.atelierapp.databinding.FragmentNewRequestBinding
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

        return binding.root
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