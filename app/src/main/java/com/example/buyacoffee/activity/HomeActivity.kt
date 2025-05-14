package com.example.buyacoffee.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import androidx.core.content.edit
import com.example.buyacoffee.R
import com.example.buyacoffee.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val TAG = "HomeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Log.d(TAG, "Inflando binding")
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Elimina esta línea porque sobreescribe el layout del binding:
        // setContentView(R.layout.activity_home)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bundle: Bundle? = intent.extras
        val email: String? = bundle?.getString("email")
        val provider: String? = bundle?.getString("provider")

        Log.d(TAG, "Recibido email: $email, provider: $provider")

        if (email != null && provider != null) {
            setUp(email, provider)
        } else {
            Log.e(TAG, "Faltan datos de email o provider en el intent")
        }

        getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit() {
            putString("email", email)
            putString("provider", provider)
        }
    }

    private fun setUp(email: String, provider: String) {
        title = "SplashActivity"

        Log.d(TAG, "Mostrando datos en pantalla")
        binding.emailTv.text = email
        binding.providerTv.text = provider

        binding.btLogOut.setOnClickListener {
            Log.d(TAG, "Cerrando sesión")
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit() {
                clear()
            }
            FirebaseAuth.getInstance().signOut()
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
