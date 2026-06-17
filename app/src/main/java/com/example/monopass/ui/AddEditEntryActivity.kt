package com.example.monopass.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.monopass.R
import com.example.monopass.data.AppDatabase
import com.example.monopass.data.PasswordEntry
import kotlinx.coroutines.launch
import android.widget.ImageButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddEditEntryActivity : AppCompatActivity() {
    private lateinit var etName: EditText
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etUrl: EditText
    private lateinit var btnSave: Button
    private lateinit var btnDelete: Button
    private lateinit var btnTogglePassword: ImageButton
    private var isPasswordVisible = false

    private val db by lazy {
        AppDatabase.getInstance(this)
    }

    private var existingEntry: PasswordEntry? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.trezor_add_edit_entry)

        etName = findViewById(R.id.etName)
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        etUrl = findViewById(R.id.etUrl)
        btnSave = findViewById(R.id.btnSave)
        btnDelete = findViewById(R.id.btnDelete)

        btnTogglePassword = findViewById(R.id.btnTogglePassword)

        btnTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                etPassword.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                //btnTogglePassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
                btnTogglePassword.setImageResource(R.drawable.ic_eye)
            } else {
                etPassword.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                btnTogglePassword.setImageResource(R.drawable.ic_eye)
            }
            //etPassword.setSelection(etPassword.text.length)
        }

        // da li je adding entry ili editing entry
        val entryId = intent.getIntExtra("ENTRY_ID", -1)
        if (entryId != -1) {
            loadEntry(entryId)
        }

        btnSave.setOnClickListener {
            saveEntry()
        }
        btnDelete.setOnClickListener {
            deleteEntry()
        }
    }

    private fun loadEntry(id: Int) {
        lifecycleScope.launch {
            val entry = db.passwordEntryDao().getById(id)!!
            existingEntry = entry
            withContext(Dispatchers.Main) {
                etName.setText(entry.name)
                etUsername.setText(entry.username)
                etPassword.setText(entry.password)
                etUrl.setText(entry.webUrl)
                btnDelete.visibility = View.VISIBLE
                title = getString(R.string.edit_entry)
            }
        }
    }

    private fun saveEntry() {
        val name = etName.text.toString().trim()
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString()
        val url = etUrl.text.toString().trim()

        if (name.isBlank() || username.isBlank() || password.isBlank()) {
            Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            if (existingEntry != null) {
                db.passwordEntryDao().update(
                    existingEntry!!.copy(
                        name = name,
                        username = username,
                        password = password,
                        webUrl = url
                    )
                )
            } else {
                db.passwordEntryDao().insert(
                    PasswordEntry(
                        name = name,
                        username = username,
                        password = password,
                        webUrl = url
                    )
                )
            }
            finish()
        }
    }

    private fun deleteEntry() {
        lifecycleScope.launch {
            db.passwordEntryDao().delete(existingEntry!!)
            finish()
        }
    }
}