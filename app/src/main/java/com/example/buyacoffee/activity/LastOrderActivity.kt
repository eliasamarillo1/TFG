package com.example.buyacoffee.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.buyacoffee.Helper.ManagmentCar
import com.example.buyacoffee.databinding.ActivityLastOrderBinding
import com.example.buyacoffee.databinding.ViewholderOrderBinding
import com.example.buyacoffee.model.OrderModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

/**
 * Historial de pedidos del usuario con la sesión iniciada, del más reciente al más antiguo.
 * También permite cerrar la sesión.
 */
class LastOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLastOrderBinding
    private lateinit var managmentCar: ManagmentCar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLastOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCar = ManagmentCar(this)

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            irAlLogin()
            return
        }

        binding.tvUsuario.text = user.email
        binding.btnCerrarSesion.setOnClickListener { cerrarSesion() }

        cargarPedidos(user.uid)
    }

    /**
     * Lee Pedidos/{uid} ordenado por timestamp y pinta una tarjeta por pedido.
     */
    private fun cargarPedidos(uid: String) {
        binding.progressPedidos.visibility = View.VISIBLE

        FirebaseDatabase.getInstance().getReference("Pedidos").child(uid)
            .orderByChild("timestamp")
            .get()
            .addOnSuccessListener { snapshot ->
                binding.progressPedidos.visibility = View.GONE

                val pedidos = snapshot.children
                    .mapNotNull { it.getValue(OrderModel::class.java) }
                    .reversed()

                if (pedidos.isEmpty()) {
                    binding.tvSinPedidos.visibility = View.VISIBLE
                    return@addOnSuccessListener
                }
                pedidos.forEach { mostrarPedido(it) }
            }
            .addOnFailureListener {
                binding.progressPedidos.visibility = View.GONE
                Toast.makeText(this, "No se pudieron cargar tus pedidos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mostrarPedido(pedido: OrderModel) {
        val card = ViewholderOrderBinding.inflate(layoutInflater, binding.ordersContainer, false)

        card.tvCodigo.text = "Pedido [${pedido.codigo}]"
        card.tvFecha.text = pedido.fecha
        card.tvTotal.text = "Total: %.2f €".format(pedido.total)

        pedido.items.forEach { item ->
            val itemView = TextView(this)
            itemView.text =
                "${item.title} x${item.numberInCart} - %.2f €".format(item.price * item.numberInCart)
            itemView.textSize = 16f
            card.itemsContainer.addView(itemView)
        }

        binding.ordersContainer.addView(card.root)
    }

    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        // La cesta es local al dispositivo: se vacía para que no la herede el siguiente usuario
        managmentCar.clearCart()
        irAlLogin()
    }

    private fun irAlLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
