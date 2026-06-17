package com.example.monopass.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.monopass.R
import com.example.monopass.data.AppDatabase
import com.example.monopass.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest

class LoginActivity : AppCompatActivity() {
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button

    private val db by lazy {
        AppDatabase.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)

        btnLogin.setOnClickListener {
            attemptLogin()
        }
        btnRegister.setOnClickListener {
            attemptRegister()
        }
    }

    private fun attemptLogin() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString()

        if (username.isBlank() || password.isBlank()) {
            Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
            return
        }

        // coroutine, sql
        lifecycleScope.launch {
            val user = db.userDao().findByUsername(username) // na pozadinskoj niti
            withContext(Dispatchers.Main) { // prebaci se na glavnu nit
                if (user != null && user.passwordHash == hash(password)) {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.invalid_credentials),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun attemptRegister() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString()

        if (username.isBlank() || password.isBlank()) {
            Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
            return
        }

        // coroutine
        lifecycleScope.launch {
            val existing = db.userDao().findByUsername(username)

            if (existing != null) {
                withContext(Dispatchers.Main) { // prebaci iz pozadinske niti coroutine na glavnu nit
                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.username_taken),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return@launch // izlazak iz coroutine
            } else {
                db.userDao().insert(User(username = username, passwordHash = hash(password)))

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.account_created),
                        Toast.LENGTH_SHORT
                    ).show()
                    etPassword.text.clear()
                }
            }
        }
    }

    private fun hash(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256") // instanca SHA-256
        val inputBytes: ByteArray = input.toByteArray() // string u bajtove
        val hashBytes: ByteArray = digest.digest(inputBytes) // racuna hash od bajtova, rez = 32 bajt
        val hexParts = mutableListOf<String>() // svaki bajt pretvori u 2 znamenkaste hex blokove

        for (byte in hashBytes) {
            val hexString = "%02x".format(byte)
            hexParts.add(hexString)
        }

        val result: String = hexParts.joinToString("")
        return result
    }
}