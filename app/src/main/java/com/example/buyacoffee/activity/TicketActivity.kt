package com.example.buyacoffee.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.buyacoffee.Helper.ManagmentCar
import com.example.buyacoffee.databinding.ActivityTicketBinding
import com.example.buyacoffee.model.ItemsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import java.text.SimpleDateFormat
import java.util.*

class TicketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTicketBinding
    private var orderCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val cartItems = intent.getSerializableExtra("cartItems") as? ArrayList<ItemsModel>
        val totalFee = intent.getDoubleExtra("totalFee", 0.0)
        Log.d("DEBUG_TOTAL", "Total recibido: $totalFee")


        // Al girar la pantalla la actividad se recrea: se conserva el código y no se vuelve a subir el pedido
        orderCode = savedInstanceState?.getString(KEY_ORDER_CODE) ?: generateRandomCode()
        binding.textViewOrderCode.text = "Tu pedido [$orderCode] ha sido enviado!"
        binding.textViewTotalFee.text = "Total: %.2f".format(totalFee) + " €"

        displayCartItems(cartItems)

        val managmentCar = ManagmentCar(this)
        if (savedInstanceState == null) {
            subirPedidoAFirebase(orderCode, totalFee, cartItems)
        }

        binding.buttonBack.setOnClickListener {
            startActivity( Intent(this, DashBoardActivity::class.java))
            managmentCar.clearCart()

        }
    }

    private fun displayCartItems(cartItems: ArrayList<ItemsModel>?) {
        binding.linearLayoutItems.removeAllViews()

        cartItems?.forEach { item ->
            val itemTextView = TextView(this).apply {
                text = "${item.title} x ${item.numberInCart} - $%.2f".format(item.price * item.numberInCart)
                textSize = 16f
            }
            binding.linearLayoutItems.addView(itemTextView)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_ORDER_CODE, orderCode)
    }

    /**
     * Guarda el pedido en Pedidos/{uid}/{codigo}, de modo que cada usuario solo ve los suyos.
     */
    private fun subirPedidoAFirebase(codigo: String, total: Double, items: ArrayList<ItemsModel>?) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "Inicia sesión para realizar pedidos", Toast.LENGTH_SHORT).show()
            return
        }
        val database = FirebaseDatabase.getInstance()
        val pedidosRef = database.getReference("Pedidos").child(user.uid)

        val fechaHora = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

        val itemsReducidos = items?.map {
            mapOf(
                "title" to it.title,
                "price" to it.price,
                "numberInCart" to it.numberInCart
            )
        }

        val pedidoMap = mapOf(
            "codigo" to codigo,
            "total" to total,
            "fecha" to fechaHora,
            "items" to itemsReducidos,
            "timestamp" to ServerValue.TIMESTAMP
        )

        pedidosRef.child(codigo).setValue(pedidoMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Pedido guardado correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar el pedido", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val KEY_ORDER_CODE = "orderCode"
    }

    private fun generateRandomCode(length: Int = 5): String {
        val caracteres = ('A'..'Z') + ('0'..'9')
        return (1..length).map { caracteres.random() }.joinToString("")
    }
}