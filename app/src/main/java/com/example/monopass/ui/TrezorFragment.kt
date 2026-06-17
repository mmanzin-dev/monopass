package com.example.monopass.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.monopass.R
import com.example.monopass.data.AppDatabase

class TrezorFragment : Fragment() {
    private lateinit var adapter: TrezorAdapter
    private val db by lazy { AppDatabase.getInstance(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trezor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TrezorAdapter { entry ->
            val intent = Intent(requireContext(), AddEditEntryActivity::class.java)
            intent.putExtra("ENTRY_ID", entry.id)
            startActivity(intent)
        }

        val rv = view.findViewById<RecyclerView>(R.id.rvEntries)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        db.passwordEntryDao().getAll().observe(viewLifecycleOwner) { entries ->
            adapter.updateEntries(entries)
        }

        view.findViewById<Button>(R.id.fabAdd).setOnClickListener {
            startActivity(Intent(requireContext(), AddEditEntryActivity::class.java))
        }
    }
}