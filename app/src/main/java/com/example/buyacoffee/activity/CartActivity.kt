package com.example.buyacoffee.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.buyacoffee.adapter.CartAdapter
import com.example.buyacoffee.databinding.ActivityCartBinding
import com.example.buyacoffee.viewmodel.CartViewModel

/**
 * Activity que muestra el carrito de compras siguiendo el patrón MVVM.
 * Se encarga únicamente de la presentación y delegación de eventos al ViewModel.
 */
class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var cartViewModel: CartViewModel
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    /**
     * Inicializa el ViewModel
     */
    private fun initViewModel() {
        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]
    }

    /**
     * Configura el RecyclerView del carrito
     */
    private fun setupRecyclerView() {
        binding.cartView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )

        // Inicializar el adapter con lista vacía
        cartAdapter = CartAdapter(
            arrayListOf(),
            this,
            object:CartAdapterListener {
                override fun onIncreaseQuantity(position: Int) {
                    cartViewModel.increaseItemQuantity(position)
                }

                override fun onDecreaseQuantity(position: Int) {
                    cartViewModel.decreaseItemQuantity(position)
                }

                override fun onRemoveItem(position: Int) {
                    cartViewModel.removeItem(position)
                }
            }
        )

        binding.cartView.adapter = cartAdapter
    }

    /**
     * Configura los observadores del ViewModel
     */
    private fun setupObservers() {
        // Observar cambios en la lista del carrito
        cartViewModel.cartItems.observe(this) { items ->
            cartAdapter.updateItems(items)
        }

        // Observar cambios en el subtotal
        cartViewModel.subtotal.observe(this) { subtotal ->
            binding.totalFreetxt.text = "${subtotal} €"
        }

        // Observar cambios en el impuesto
        cartViewModel.tax.observe(this) { tax ->
            binding.ivatxt.text = "${tax} €"
        }

        // Observar cambios en el total
        cartViewModel.total.observe(this) { total ->
            binding.totalTxt.text = "${total} €"
        }

        // Observar mensajes de error
        cartViewModel.errorMessage.observe(this) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        // Observar mensajes de éxito
        cartViewModel.successMessage.observe(this) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }


    }

    /**
     * Configura los listeners de los botones
     */
    private fun setupClickListeners() {
        // Botón de pagar
        binding.payBtn.setOnClickListener {
            cartViewModel.processPayment { cartItems, totalFee ->
                val intent = Intent(this, TicketActivity::class.java)
                intent.putExtra("cartItems", cartItems)
                intent.putExtra("totalFee", totalFee)
                startActivity(intent)
            }
        }

        // Botón de regresar
        binding.backBtn.setOnClickListener {
            startActivity(Intent(this, DashBoardActivity::class.java))
        }

        // Botón de aplicar descuento
        binding.applyDiscountBtn.setOnClickListener {
            val code = binding.discountCodeEditText.text.toString()
            cartViewModel.applyDiscountCode(code) { success, message ->
                if (success) {
                    binding.discountCodeEditText.setText("")
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Interface para manejar eventos del adapter
     */
    interface CartAdapterListener {
        fun onIncreaseQuantity(position: Int)
        fun onDecreaseQuantity(position: Int)
        fun onRemoveItem(position: Int)
    }
}