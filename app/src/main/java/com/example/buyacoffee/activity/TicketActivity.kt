package com.example.buyacoffee.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.buyacoffee.databinding.ActivityTicketBinding
import com.example.buyacoffee.model.ItemsModel
import com.example.buyacoffee.repositorio.CartRepo
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class TicketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTicketBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val cartItems = intent.getSerializableExtra("cartItems") as? ArrayList<ItemsModel>
        val totalFee = intent.getDoubleExtra("totalFee", 0.0)
        Log.d("DEBUG_TOTAL", "Total recibido: $totalFee")


        val orderCode = generateRandomCode()
        binding.textViewOrderCode.text = "Tu pedido [$orderCode] ha sido enviado!"
        binding.textViewTotalFee.text = "Total: %.2f".format(totalFee) + " €"

        displayCartItems(cartItems)

        val managmentCar = CartRepo(this)
        managmentCar.subirPedidoAFirebase(orderCode, totalFee, cartItems)
        managmentCar.saveLastOrder(orderCode, cartItems ?: arrayListOf())

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


    private fun generateRandomCode(length: Int = 5): String {
        val caracteres = ('A'..'Z') + ('0'..'9')
        return (1..length).map { caracteres.random() }.joinToString("")
    }
}