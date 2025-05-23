package com.example.atelierapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.atelierapp.databinding.FragmentConcreteClientBinding
import com.example.atelierapp.ktor.ApiClient
import com.example.atelierapp.ktor.AuthModels
import com.example.atelierapp.recycler.ClientAdapter
import com.example.atelierapp.recycler.RequestAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConcreteClientFragment : Fragment() {

    private var _binding: FragmentConcreteClientBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var requestAdapter: RequestAdapter

    private lateinit var tvNameConcrete: TextView
    private lateinit var tvPhoneConcrete: TextView
    private lateinit var tvEmailConcrete: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConcreteClientBinding.inflate(layoutInflater, container, false)

        tvNameConcrete = binding.tvNameConcrete
        tvPhoneConcrete = binding.tvPhoneConcrete
        tvEmailConcrete = binding.tvEmailConcrete
        recyclerView = binding.concreteRecycler
        recyclerView.layoutManager = LinearLayoutManager(activity)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireActivity().getSharedPreferences("client_prefs",
            Context.MODE_PRIVATE)
        val phone = prefs.getString("phone", "не указано")
        val name = prefs.getString("name", "не указано")
        val email = prefs.getString("email", "не указано")
        val date = prefs.getString("date", "не указано")
        tvNameConcrete.text = name
        tvPhoneConcrete.text = phone
        tvEmailConcrete.text = email

        lifecycleScope.launch {
            try {
                if (name != null && date != null) {
                    val clients = withContext(Dispatchers.IO) {
                        ApiClient.authApi.getClientBy(
                            AuthModels.ClientByDTO(
                                name, date, "name"
                            )
                        )
                    }

                    requestAdapter = RequestAdapter(clients)
                    recyclerView.adapter = requestAdapter
                }
            } catch (e: Exception) {
                Log.e("MYCLIENT", "Ошибка: ${e.message}")
            }
        }

        /*lifecycleScope.launch {
            try {
                val clients = withContext(Dispatchers.IO) {
                    ApiClient.authApi.getClients()
                }

                requestAdapter = RequestAdapter(clients)
                recyclerView.adapter = requestAdapter

            } catch (e: Exception) {
                Log.e("MYCLIENT", "Ошибка: ${e.message}")
            }
        }*/
    }

    companion object {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}