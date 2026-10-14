package com.example.buyacoffee.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.buyacoffee.Helper.CartCalculator
import com.example.buyacoffee.Helper.ChangeNumberItemsListener
import com.example.buyacoffee.Helper.ManagmentCar
import com.example.buyacoffee.Helper.Precios
import com.example.buyacoffee.adapter.CartAdapter
import com.example.buyacoffee.databinding.ActivityCartBinding
import com.example.buyacoffee.model.ItemsModel
import com.google.firebase.database.FirebaseDatabase

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var managmentCar: ManagmentCar

    private var descuentoAplicado = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        managmentCar = ManagmentCar(this)
        descuentoAplicado = savedInstanceState?.getDouble(KEY_DESCUENTO) ?: 0.0

        initiCart()

    }

    private fun initiCart() {
        calculateCart()
        initCartlist()
        initButtons()
    }

    /**
     * Inicializa el RecyclerView con los productos del carrito usando CartAdapter.
     */
    private fun initCartlist() {

        binding.cartView.layoutManager =
            LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,
                false)

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
            val totalFee = CartCalculator.calcular(cartItems, descuentoAplicado).total
            val intent = Intent(this, TicketActivity::class.java)


            if (cartItems.isEmpty()) {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Esto detiene la ejecución si el carrito está vacío
            }
            intent.putExtra("cartItems", cartItems)
            intent.putExtra("totalFee", totalFee)

            startActivity(intent)
            // El pedido ya viaja en el Intent: se vacía la cesta para que no siga llena
            // si se vuelve con el botón atrás del sistema, y se cierra esta pantalla
            managmentCar.clearCart()
            finish()

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
            val ref = FirebaseDatabase.getInstance().getReference("Descuentos").child(code)
            var valor: Double

            if (code.isBlank()) {
                Toast.makeText(this, "Introduce un código", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //addOnSuccessListener es un callBack que se ejecuta cuando la operación de lectura es exitosa.
            ref.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                     valor = snapshot.child("valor").value.toString().toDoubleOrNull() ?: 0.0
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
     * Calcula el total del carrito aplicando el descuento y desglosa el IVA incluido.
     * Actualiza los textos en pantalla con los valores correspondientes.
     */
    private fun calculateCart() {
        val resumen = CartCalculator.calcular(managmentCar.getListCart(), descuentoAplicado)

        binding.totalFreetxt.text = Precios.formatEuro(resumen.baseImponible)
        binding.ivatxt.text = Precios.formatEuro(resumen.impuesto)
        binding.totalTxt.text = Precios.formatEuro(resumen.total)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putDouble(KEY_DESCUENTO, descuentoAplicado)
    }

    companion object {
        private const val KEY_DESCUENTO = "descuento"
    }
}
