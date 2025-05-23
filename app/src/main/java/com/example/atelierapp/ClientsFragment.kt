package com.example.atelierapp

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.atelierapp.databinding.FragmentClientsBinding
import com.example.atelierapp.databinding.FragmentTitleBinding
import com.example.atelierapp.ktor.ApiClient
import com.example.atelierapp.recycler.ClientAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClientsFragment : Fragment() {

    private var _binding: FragmentClientsBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var clientAdapter: ClientAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClientsBinding.inflate(layoutInflater, container, false)

        recyclerView = binding.clientsRecycler
        recyclerView.layoutManager = LinearLayoutManager(activity)

        lifecycleScope.launch {
            try {
                val clients = withContext(Dispatchers.IO) {
                    ApiClient.authApi.getClients()
                }

                // Удаление дубликатов по phone:
                val uniqueClients = clients.distinctBy { it.phone }

                clientAdapter = ClientAdapter(uniqueClients) {client ->
                    val prefs = requireActivity().getSharedPreferences("client_prefs",
                        Context.MODE_PRIVATE)
                    prefs.edit().apply {
                        putString("phone", client.phone)
                        putString("name", client.name)
                        putString("email", client.email)
                        putString("date", client.date)
                        apply()
                    }
                    findNavController().navigate(R.id.action_clientsFragment_to_concreteClientFragment)
                }
                recyclerView.adapter = clientAdapter

            } catch (e: Exception) {
                Log.e("CLIENT", "Ошибка: ${e.message}")
            }
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