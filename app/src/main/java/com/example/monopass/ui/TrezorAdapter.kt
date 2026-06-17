package com.example.monopass.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.monopass.R
import com.example.monopass.data.PasswordEntry

class TrezorAdapter(
    private var entries: List<PasswordEntry> = emptyList(),
    private val onClick: (PasswordEntry) -> Unit
) : RecyclerView.Adapter<TrezorAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvUsername: TextView = view.findViewById(R.id.tvUsername)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.trezor_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        holder.tvName.text = entry.name
        holder.tvUsername.text = entry.username
        holder.itemView.setOnClickListener { onClick(entry) }
    }

    override fun getItemCount() = entries.size

    fun updateEntries(newEntries: List<PasswordEntry>) {
        entries = newEntries
        notifyDataSetChanged()
    }
}