package com.example.buyacoffee.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.buyacoffee.Helper.ChangeNumberItemsListener
import com.example.buyacoffee.Helper.ManagmentCar
import com.example.buyacoffee.adapter.CartAdapter
import com.example.buyacoffee.databinding.ActivityCartBinding
import com.example.buyacoffee.model.ItemsModel
import com.google.firebase.database.FirebaseDatabase
/**
 * Representa el carrito de compras.
 *
 * Esta clase muestra los productos añadidos al carrito, permite aplicar descuentos,
 * calcular el total a pagar y realizar el pedido.
 */
class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var managmentCar: ManagmentCar
    private var impuesto: Double = 0.0
    private var descuentoAplicado = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCar = ManagmentCar(this)

        calculateCart()
        initCartlist()
        initButtons()
    }

    /**
     * Inicializa el RecyclerView con los productos del carrito usando CartAdapter.
     */
    private fun initCartlist() {
        binding.cartView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.cartView.adapter = CartAdapter(
            managmentCar.getListCart(),
            this,
            object : ChangeNumberItemsListener {
                override fun onChanged() {
                    calculateCart()
                }
            }
        )
    }

    private fun initButtons() {

        binding.payBtn.setOnClickListener {
            val cartItems: ArrayList<ItemsModel> = ArrayList(managmentCar.getListCart())
            val totalFee = Math.round((managmentCar.getTotalFee() + impuesto + 10.0) * 100) / 100.0

            if (cartItems.isEmpty()) {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Esto detiene la ejecución si el carrito está vacío
            }
            val intent = Intent(this, TicketActivity::class.java)
            intent.putExtra("cartItems", cartItems)
            intent.putExtra("totalFee", totalFee)

            startActivity(intent)

        }

        binding.backBtn.setOnClickListener {
            startActivity(Intent(this, DashBoardActivity::class.java))
        }
        /**
         * Configura el botón de aplicar descuento.
         * Verifica el código en Firebase y si es válido, aplica el porcentaje correspondiente.
         */
        binding.applyDiscountBtn.setOnClickListener {
            val code = binding.discountCodeEditText.text.toString().uppercase().trim()
            if (code.isBlank()) {
                Toast.makeText(this, "Introduce un código", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ref = FirebaseDatabase.getInstance().getReference("Descuentos").child(code)
            ref.get().addOnSuccessListener { snapshot ->

                if (snapshot.exists()) {
                    val valor = snapshot.child("valor").value.toString().toDoubleOrNull() ?: 0.0
                    descuentoAplicado = valor
                    Toast.makeText(this, "Descuento del $valor% aplicado", Toast.LENGTH_SHORT)
                        .show()
                    calculateCart()
                } else {
                    Toast.makeText(this, "Código no válido", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error al validar el código", Toast.LENGTH_SHORT).show()
            }
        }
    }


    /**
     * Calcula el total del carrito aplicando descuentos e impuestos.
     * Actualiza los textos en pantalla con los valores correspondientes.
     */
    private fun calculateCart() {
        val iva = 0.02
        val baseFee = managmentCar.getTotalFee()

        // Aplicar descuento por porcentaje si existe
        val descuento = (baseFee * descuentoAplicado / 100.0).let {
            Math.round(it * 100) / 100.0
        }

        val precioConDescuento = baseFee - descuento

        impuesto = Math.round((precioConDescuento * iva) * 100) / 100.0
        val total = Math.round((precioConDescuento + impuesto ) * 100) / 100.0
        val itemTotal = Math.round(precioConDescuento * 100) / 100.0

        binding.totalFreetxt.text = "$itemTotal €"
        binding.ivatxt.text = "$impuesto €"
        binding.totalTxt.text = "$total €"
    }





}
