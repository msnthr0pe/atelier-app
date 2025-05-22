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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.atelierapp.databinding.FragmentRequestsBinding
import com.example.atelierapp.databinding.FragmentTitleBinding
import com.example.atelierapp.ktor.ApiClient
import com.example.atelierapp.recycler.ClientAdapter
import com.example.atelierapp.recycler.RequestAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RequestsFragment : Fragment() {

    private var _binding: FragmentRequestsBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var requestAdapter: RequestAdapter

    private lateinit var tvSearchClients: TextView
    private lateinit var btnAdd: ImageButton

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
                requestAdapter = RequestAdapter(clients)
                recyclerView.adapter = requestAdapter

            } catch (e: Exception) {
                Log.e("CLIENT", "Ошибка: ${e.message}")
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

        return binding.root
    }

    companion object {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}