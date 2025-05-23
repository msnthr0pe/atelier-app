package com.example.atelierapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.atelierapp.databinding.FragmentCalendarRequestsBinding
import com.example.atelierapp.databinding.FragmentTitleBinding
import com.example.atelierapp.ktor.ApiClient
import com.example.atelierapp.ktor.AuthModels
import com.example.atelierapp.recycler.RequestAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CalendarRequestsFragment : Fragment() {

    private var _binding: FragmentCalendarRequestsBinding? = null
    private val binding get() = _binding!!

    private lateinit var calendar: CalendarView
    private lateinit var recyclerView: RecyclerView
    private lateinit var requestAdapter: RequestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarRequestsBinding.inflate(layoutInflater, container, false)

        binding.customBottomBar2.iconHome.setOnClickListener {
            findNavController().navigate(R.id.action_calendarRequestsFragment_to_requestsFragment)
        }
        binding.customBottomBar2.iconProfile.setOnClickListener {
            findNavController().navigate(R.id.action_calendarRequestsFragment_to_profileInfoFragment)
        }

        recyclerView = binding.calendarRecycler
        recyclerView.layoutManager = LinearLayoutManager(activity)
        calendar = binding.calendarView
        val selectedDateInMillis = calendar.date

        val sdf = java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault())
        var date = sdf.format(java.util.Date(selectedDateInMillis))
        updateRecycler(date)
        calendar.setOnDateChangeListener{ _, year, month, dayOfMonth ->
            date = "$dayOfMonth.${"0" + (month + 1)}.$year"
            updateRecycler(date)
        }

        return binding.root
    }

    private fun updateRecycler(date: String) {
        lifecycleScope.launch {
            try {
                val clients = withContext(Dispatchers.IO) {
                    ApiClient.authApi.getClientBy(
                        AuthModels.ClientByDTO(
                            "", date, "date"
                        )
                    )
                }

                requestAdapter = RequestAdapter(clients)
                recyclerView.adapter = requestAdapter
            } catch (e: Exception) {
                Log.e("MYCLIENT", "Ошибка: ${e.message}")
            }
        }
    }

    companion object {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}