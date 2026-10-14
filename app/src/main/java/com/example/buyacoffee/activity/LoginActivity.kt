package com.example.buyacoffee.activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.buyacoffee.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

/**
 * Pantalla de acceso y registro con correo y contraseña usando Firebase Auth.
 * Si el usuario ya tiene la sesión iniciada, pasa directamente al Dashboard.
 */
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (auth.currentUser != null) {
            irAlDashboard()
            return
        }

        binding.btnAcceder.setOnClickListener { acceder() }
        binding.btnRegistrar.setOnClickListener { registrar() }
    }

    private fun acceder() {
        val (email, password) = leerCampos() ?: return
        mostrarCargando(true)
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { irAlDashboard() }
            .addOnFailureListener { e ->
                mostrarCargando(false)
                Toast.makeText(this, mensajeDeError(e), Toast.LENGTH_LONG).show()
            }
    }

    private fun registrar() {
        val (email, password) = leerCampos() ?: return
        mostrarCargando(true)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Toast.makeText(this, "Cuenta creada. ¡Bienvenido!", Toast.LENGTH_SHORT).show()
                irAlDashboard()
            }
            .addOnFailureListener { e ->
                mostrarCargando(false)
                Toast.makeText(this, mensajeDeError(e), Toast.LENGTH_LONG).show()
            }
    }

    /**
     * Valida el correo y la contraseña. Devuelve null y marca el campo si algo no es válido.
     */
    private fun leerCampos(): Pair<String, String>? {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        binding.tilEmail.error = null
        binding.tilPassword.error = null

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Introduce un correo válido"
            return null
        }
        if (password.length < 6) {
            binding.tilPassword.error = "La contraseña debe tener al menos 6 caracteres"
            return null
        }
        return email to password
    }

    /**
     * Traduce los errores de Firebase Auth a mensajes en español para el usuario.
     */
    private fun mensajeDeError(e: Exception): String = when (e) {
        is FirebaseAuthWeakPasswordException -> "La contraseña es demasiado débil"
        is FirebaseAuthUserCollisionException -> "Ya existe una cuenta con ese correo"
        is FirebaseAuthInvalidUserException -> "No existe ninguna cuenta con ese correo"
        is FirebaseAuthInvalidCredentialsException -> "Correo o contraseña incorrectos"
        is FirebaseNetworkException -> "Sin conexión. Revisa tu red e inténtalo de nuevo"
        else -> "No se pudo completar la operación: ${e.localizedMessage}"
    }

    private fun mostrarCargando(cargando: Boolean) {
        binding.progressLogin.visibility = if (cargando) View.VISIBLE else View.GONE
        binding.btnAcceder.isEnabled = !cargando
        binding.btnRegistrar.isEnabled = !cargando
    }

    private fun irAlDashboard() {
        startActivity(Intent(this, DashBoardActivity::class.java))
        finish()
    }
}
