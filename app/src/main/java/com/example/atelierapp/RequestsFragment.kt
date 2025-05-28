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
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.atelierapp.databinding.FragmentRequestsBinding
import com.example.atelierapp.databinding.FragmentTitleBinding
import com.example.atelierapp.ktor.ApiClient
import com.example.atelierapp.ktor.AuthModels
import com.example.atelierapp.recycler.ClientAdapter
import com.example.atelierapp.recycler.RequestAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RequestsFragment : Fragment() {

    private var _binding: FragmentRequestsBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var requestAdapter: RequestAdapter

    private lateinit var tvSearchClients: TextView
    private lateinit var btnAdd: ImageButton

    private lateinit var searchEditText: EditText
    private val searchQuery = MutableStateFlow("")

    private var originalClients: List<AuthModels.ClientRequest> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestsBinding.inflate(layoutInflater, container, false)

        recyclerView = binding.allRequestsRecycler
        recyclerView.layoutManager = LinearLayoutManager(activity)

        lifecycleScope.launch {
            try {
                val clients = withContext(Dispatchers.IO) {
                    ApiClient.authApi.getClients()
                }

                originalClients = clients
                requestAdapter = RequestAdapter(clients)
                recyclerView.adapter = requestAdapter

            } catch (e: Exception) {
                Log.e("CLIENT", "Ошибка: ${e.message}")
            }
        }

        searchEditText = binding.etSearchRequests

        searchEditText.addTextChangedListener {
            searchQuery.value = it.toString()
        }

        lifecycleScope.launch {
            searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .flowOn(Dispatchers.Default)
                .collectLatest { query ->
                    filterCards(query)
                }
        }

        tvSearchClients = binding.tvSearchClientsRequests
        tvSearchClients.setOnClickListener {
            findNavController().navigate(R.id.action_requestsFragment_to_clientsFragment)
        }

        btnAdd = binding.btnAdd
        btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_requestsFragment_to_newRequestFragment)
        }

        val prefs = requireContext().getSharedPreferences("credentials", Context.MODE_PRIVATE)
        val prefStatus = prefs.getString("status", "--")
        Log.d("PREFS", prefStatus!!)
        if (prefStatus == "manager") {
            btnAdd.visibility = View.GONE
        }


        binding.customBottomBar.iconCalendar.setOnClickListener {
            findNavController().navigate(R.id.action_requestsFragment_to_calendarRequestsFragment)
        }
        binding.customBottomBar.iconProfile.setOnClickListener {
            findNavController().navigate(R.id.action_requestsFragment_to_profileInfoFragment)
        }

        return binding.root
    }

    private fun filterCards(query: String) {
        val filtered = if (query.isEmpty()) {
            originalClients
        } else {
            originalClients.filter { it.name.contains(query, ignoreCase = true) }
        }
        try {
            requestAdapter.updateList(filtered)
        } catch(_: Exception) {
            Log.e("VETUSLUGI", "Exception occurred")
        }
    }

    companion object {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}