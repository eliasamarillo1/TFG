package com.example.buyacoffee.activity

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.buyacoffee.repositorio.CartRepo
import com.example.buyacoffee.databinding.ActivityLastOrderBinding

class LastOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLastOrderBinding
    private lateinit var managmentCar: CartRepo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLastOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCar = CartRepo(this)

        val codigo = managmentCar.getLastOrderCode()
        val items = managmentCar.getLastOrderItems()

        if (codigo.isBlank() || items.isEmpty()) {
            binding.orderCodeText.text = "No hay pedidos anteriores registrados."
            Toast.makeText(this, "No se encontró ningún pedido reciente.", Toast.LENGTH_SHORT).show()
            return
        }

        binding.orderCodeText.text = "Código del pedido: [$codigo]"

        items.forEach { item ->
            val itemView = TextView(this)
            itemView.text =
                "${item.title} x${item.numberInCart} - %.2f €".format(item.price * item.numberInCart)
            itemView.textSize = 16f
            binding.itemsContainer.addView(itemView)
        }
    }
}
