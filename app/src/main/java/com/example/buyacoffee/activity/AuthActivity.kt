package com.example.buyacoffee.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.buyacoffee.databinding.ActivityAuthBinding
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {
    private val GOOGLE_SIGN_IN = 100
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Log.d("AuthActivity", "onCreate - Iniciando setup")
        setUp()
        session()
    }

    override fun onStart() {
        super.onStart()
        Log.d("AuthActivity", "onStart - Mostrando layout de autenticación")
        binding.authLayout.visibility = View.VISIBLE
    }

    private fun session() {
        val prefs = getSharedPreferences(getString(com.example.buyacoffee.R.string.prefs_file), MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val provider = prefs.getString("provider", null)

        if (email != null && provider != null) {
            Log.d("AuthActivity", "Sesion encontrada: $email con $provider")
            binding.authLayout.visibility = View.INVISIBLE
            showHome(email, ProviderType.valueOf(provider))
        } else {
            Log.d("AuthActivity", "No se encontró sesión activa")
        }
    }

    private fun setUp() {
        binding.signUpbtn.setOnClickListener {
            val email = binding.eamilEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            Log.d("AuthActivity", "Botón registrar pulsado")

            if (email.isNotEmpty() && password.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d("AuthActivity", "Usuario registrado con éxito")
                            Toast.makeText(this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show()
                            showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                        } else {
                            Log.e("AuthActivity", "Error al registrar", it.exception)
                            Toast.makeText(this, "Error en autenticación", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Log.w("AuthActivity", "Campos vacíos al registrar")
                Toast.makeText(this, "Debe completar los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginButton.setOnClickListener {
            val email = binding.eamilEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            Log.d("AuthActivity", "Botón login pulsado")

            if (email.isNotEmpty() && password.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d("AuthActivity", "Inicio de sesión exitoso")
                            Toast.makeText(this, "Inicio de sesión correcto", Toast.LENGTH_SHORT).show()
                            showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                        } else {
                            Log.e("AuthActivity", "Error al iniciar sesión", it.exception)
                            Toast.makeText(this, "Error en autenticación", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Log.w("AuthActivity", "Campos vacíos al loguearse")
                Toast.makeText(this, "Debe completar los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showHome(email: String, provider: ProviderType) {
        Log.d("AuthActivity", "Navegando a Home con $email y $provider")
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }
}
