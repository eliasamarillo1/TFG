package com.example.buyacoffee.activity

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.buyacoffee.Helper.ManagmentCar
import com.example.buyacoffee.R
import com.example.buyacoffee.model.ItemsModel
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class TicketActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket)

        // 1. Recuperar los datos del intent
        val cartItems = intent.getSerializableExtra("cartItems") as? ArrayList<ItemsModel>
        val totalFee = intent.getDoubleExtra("totalFee", 0.0)

        // 2. Generar y mostrar código
        val orderCode = generateRandomCode()
        findViewById<TextView>(R.id.textViewOrderCode).text =
            "Tu pedido [$orderCode] ha sido enviado!"

        // 3. Mostrar total
        findViewById<TextView>(R.id.textViewTotalFee).text =
            "Total: $%.2f".format(totalFee)

        // 4. Mostrar ítems en el layout
        displayCartItems(cartItems)

        // 5. Subir pedido a Firebase
        subirPedidoAFirebase(orderCode, totalFee, cartItems)
        val managmentCar = ManagmentCar(this)
        managmentCar.saveLastOrder(orderCode, cartItems ?: arrayListOf())


        // 6. Botón volver
        findViewById<Button>(R.id.buttonBack).setOnClickListener {
            managmentCar.clearCart()
            finish()
        }
    }

    private fun displayCartItems(cartItems: ArrayList<ItemsModel>?) {
        val itemsLayout = findViewById<LinearLayout>(R.id.linearLayoutItems)
        itemsLayout.removeAllViews()

        cartItems?.forEach { item ->
            val itemTextView = TextView(this)
            itemTextView.text =
                "${item.title} x${item.numberInCart} - $%.2f".format(item.price * item.numberInCart)
            itemTextView.textSize = 16f
            itemsLayout.addView(itemTextView)
        }
    }

    private fun subirPedidoAFirebase(codigo: String, total: Double, items: ArrayList<ItemsModel>?) {
        val database = FirebaseDatabase.getInstance()
        val pedidosRef = database.getReference("Pedidos")

        val fechaHora = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

        // Lista simplificada con solo los campos necesarios
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
            "items" to itemsReducidos
        )

        pedidosRef.child(codigo).setValue(pedidoMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Pedido guardado correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar el pedido", Toast.LENGTH_SHORT).show()
            }
    }
    private fun generateRandomCode(length: Int = 5): String {
        val caracteres = ('A'..'Z') + ('0'..'9')
        return (1..length).map { caracteres.random() }.joinToString("")
    }

}
