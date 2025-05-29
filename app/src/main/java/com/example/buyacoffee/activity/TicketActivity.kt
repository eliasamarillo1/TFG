package com.example.buyacoffee.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.buyacoffee.Helper.ManagmentCar
import com.example.buyacoffee.databinding.ActivityTicketBinding
import com.example.buyacoffee.model.ItemsModel
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TicketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTicketBinding
    val managmentCar = ManagmentCar(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var orderCode = generateRandomCode()
        var cartItems = intent.getSerializableExtra("cartItems") as? ArrayList<ItemsModel>
        var totalFee = intent.getDoubleExtra("totalFee", 0.0)

        binding.textViewOrderCode.text = "Tu pedido [$orderCode] ha sido enviado!"
        binding.textViewTotalFee.text = "Total: $%.2f".format(totalFee)

        displayCartItems(cartItems)

        subirPedidoAFirebase(orderCode, totalFee, cartItems)
        managmentCar.saveLastOrder(orderCode, cartItems ?: arrayListOf())

        initbtn()

    }

    private fun initbtn() {

        binding.buttonBack.setOnClickListener {
            startActivity(Intent(this, DashBoardActivity::class.java))
            managmentCar.clearCart()

        }

    }
    
    /**
     * Muestra los artículos que se encuentran actualmente en el carrito.
     **
     * @param cartItems Un [ArrayList] de objetos [ItemsModel], que representan los artículos actualmente en el carrito.
     * Este parámetro puede ser nulo si el carrito está vacío.
     */
    private fun displayCartItems(cartItems: ArrayList<ItemsModel>?) {
        binding.linearLayoutItems.removeAllViews()

        cartItems?.forEach { item ->
            val itemTextView = TextView(this).apply {
                text = "${item.title} x${item.numberInCart} - $%.2f".format(item.price * item.numberInCart)
                textSize = 16f
            }
            binding.linearLayoutItems.addView(itemTextView)
        }
    }

    /**
     * Sube un pedido a Firebase Realtime Database bajo la rama "Pedidos", usando el código del pedido como clave.
     *
     * @param codigo Código único que identifica el pedido.
     * @param total Importe total del pedido.
     * @param items Lista de objetos [ItemsModel] que representan los artículos del pedido.
     *
     * El pedido se guarda como un mapa que incluye el código, total, fecha y una lista reducida de items
     * (solo con título, precio y cantidad). Se muestra un mensaje de éxito o error mediante un `Toast`
     * dependiendo del resultado de la operación.
     */
    private fun subirPedidoAFirebase(codigo: String, total: Double, items: ArrayList<ItemsModel>?) {
        val database = FirebaseDatabase.getInstance()
        val pedidosRef = database.getReference("Pedidos")
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
