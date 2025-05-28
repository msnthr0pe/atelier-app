package com.example.atelierapp

import android.content.Context
import android.content.SharedPreferences
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
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.atelierapp.databinding.FragmentClientsBinding
import com.example.atelierapp.databinding.FragmentTitleBinding
import com.example.atelierapp.ktor.ApiClient
import com.example.atelierapp.ktor.AuthModels
import com.example.atelierapp.ktor.HistoryAdapter
import com.example.atelierapp.recycler.ClientAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClientsFragment : Fragment() {

    private var _binding: FragmentClientsBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var clientAdapter: ClientAdapter

    private lateinit var searchEditText: EditText
    private val searchQuery = MutableStateFlow("")

    private var originalClients: List<AuthModels.ClientRequest> = emptyList()

    private lateinit var noResultsTextView: View
    private lateinit var errorLayout: View
    private lateinit var retryButton: View

    private var lastQueryFailed = false


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

        noResultsTextView = binding.tvNoResults
        errorLayout = binding.layoutError
        retryButton = binding.btnRetry

        loadClients()

        retryButton.setOnClickListener {
            lastQueryFailed = false
            loadClients()
        }

        searchEditText = binding.etSearch
        val restoredQuery = savedInstanceState?.getString("search_query") ?: ""
        searchEditText.setText(restoredQuery)
        searchQuery.value = restoredQuery

        val clearButton = binding.btnClearSearch
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery.value = s.toString()
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                binding.progressBar.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            clearButton.visibility = View.GONE
            hideKeyboard()
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

        val historyRecycler = binding.historyRecycler
        historyRecycler.layoutManager = LinearLayoutManager(activity)
        val clearHistoryButton = binding.btnClearHistory

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val history = loadSearchHistory()
                if (history.isNotEmpty()) {
                    historyRecycler.visibility = View.VISIBLE
                    clearHistoryButton.visibility = View.VISIBLE
                    historyRecycler.adapter = HistoryAdapter(history) { selected ->
                        searchEditText.setText(selected)
                        searchEditText.setSelection(selected.length)
                        searchEditText.clearFocus()
                        historyRecycler.visibility = View.GONE
                        clearHistoryButton.visibility = View.GONE
                    }
                }
            } else {
                historyRecycler.visibility = View.GONE
                clearHistoryButton.visibility = View.GONE
            }
        }

        clearHistoryButton.setOnClickListener {
            clearSearchHistory()
            historyRecycler.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
        }

        return binding.root
    }

    private fun loadClients() {
        lifecycleScope.launch {
            try {
                errorLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                val clients = withContext(Dispatchers.IO) {
                    ApiClient.authApi.getClients()
                }

                val uniqueClients = clients.distinctBy { it.phone }

                originalClients = uniqueClients
                clientAdapter = ClientAdapter(uniqueClients) { client ->
                    val prefs = requireActivity().getSharedPreferences("client_prefs", Context.MODE_PRIVATE)
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
                filterCards(searchQuery.value)

            } catch (e: Exception) {
                Log.e("CLIENT", "Ошибка: ${e.message}")
                recyclerView.visibility = View.GONE
                noResultsTextView.visibility = View.GONE
                errorLayout.visibility = View.VISIBLE
                lastQueryFailed = true
            }
        }
    }


    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("search_query", searchEditText.text.toString())
    }


    companion object {

    }

    private fun filterCards(query: String) {
        val filtered = if (query.isEmpty()) {
            originalClients
        } else {
            originalClients.filter { it.name.contains(query, ignoreCase = true) }
        }

        if (query.isNotEmpty()) {
            val history = loadSearchHistory()
            history.remove(query)
            history.add(0, query)
            val trimmed = history.take(10)
            saveSearchHistory(trimmed)
        }

        try {
            clientAdapter.updateList(filtered)
            noResultsTextView.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
            recyclerView.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
        } catch (e: Exception) {
            Log.e("ATELIER", "Exception occurred")
        }
        binding.progressBar.visibility = View.GONE
    }

    private fun getHistoryPrefs(): SharedPreferences {
        return requireContext().getSharedPreferences("search_history", Context.MODE_PRIVATE)
    }

    private fun loadSearchHistory(): MutableList<String> {
        val json = getHistoryPrefs().getString("history", null) ?: return mutableListOf()
        return Gson().fromJson(json, object : TypeToken<MutableList<String>>() {}.type)
    }

    private fun saveSearchHistory(history: List<String>) {
        val json = Gson().toJson(history)
        getHistoryPrefs().edit().putString("history", json).apply()
    }

    private fun clearSearchHistory() {
        getHistoryPrefs().edit().remove("history").apply()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}