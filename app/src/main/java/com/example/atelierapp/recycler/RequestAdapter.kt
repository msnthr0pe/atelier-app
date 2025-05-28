package com.example.atelierapp.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.atelierapp.R
import com.example.atelierapp.ktor.AuthModels

class RequestAdapter(private var clients: List<AuthModels.ClientRequest>) :
    RecyclerView.Adapter<RequestAdapter.RequestViewHolder>() {

    inner class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.recyclerRequestName)
        val timeText: TextView = itemView.findViewById(R.id.timeTextView)
        val descText: TextView = itemView.findViewById(R.id.descriptionTextView)
        val dateText: TextView = itemView.findViewById(R.id.dateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_request, parent, false)
        return RequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val client = clients[position]
        holder.nameText.text = client.name
        holder.timeText.text = client.time
        holder.descText.text = client.description
        holder.dateText.text = client.date
    }

    override fun getItemCount(): Int = clients.size

    fun updateList(newList: List<AuthModels.ClientRequest>) {
        clients = newList
        notifyDataSetChanged()
    }

}
