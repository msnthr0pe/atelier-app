package com.example.atelierapp.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.atelierapp.R
import com.example.atelierapp.ktor.AuthModels

class ClientAdapter(private var clients: List<AuthModels.ClientRequest>,
                    private val onItemClick: (AuthModels.ClientRequest) -> Unit) :
    RecyclerView.Adapter<ClientAdapter.ClientViewHolder>() {

    inner class ClientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.recyclerClientName)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(clients[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_client, parent, false)
        return ClientViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val client = clients[position]
        holder.nameText.text = client.name
    }

    override fun getItemCount(): Int = clients.size

    fun updateList(newList: List<AuthModels.ClientRequest>) {
        clients = newList
        notifyDataSetChanged()
    }

}
