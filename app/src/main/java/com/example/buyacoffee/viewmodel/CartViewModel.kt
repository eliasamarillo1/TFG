package com.example.buyacoffee.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.buyacoffee.model.ItemsModel
import com.example.buyacoffee.repositorio.CartRepo

/**
 * ViewModel para gestionar el estado del carrito de compras.
 * Actúa como intermediario entre la Vista (Activity) y el Repositorio.
 */
class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val cartRepo = CartRepo(application.applicationContext)

    // LiveData para la lista del carrito
    private val _cartItems = MutableLiveData<ArrayList<ItemsModel>>()
    val cartItems: LiveData<ArrayList<ItemsModel>> = _cartItems

    // LiveData para los cálculos del carrito
    private val _subtotal = MutableLiveData<Double>()
    val subtotal: LiveData<Double> = _subtotal

    private val _tax = MutableLiveData<Double>()
    val tax: LiveData<Double> = _tax

    private val _total = MutableLiveData<Double>()
    val total: LiveData<Double> = _total

    private val _discountApplied = MutableLiveData<Double>()
    val discountApplied: LiveData<Double> = _discountApplied

    // LiveData para estados de la UI
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage

    private val _isCartEmpty = MutableLiveData<Boolean>()
    val isCartEmpty: LiveData<Boolean> = _isCartEmpty

    // Variables para cálculos
    private var currentDiscountPercentage = 0.0
    private val taxRate = 0.02 // 2% IVA

    init {
        loadCartItems()
    }

    /**
     * Carga los elementos del carrito desde el repositorio
     */
    fun loadCartItems() {
        val items = cartRepo.getListCart()
        _cartItems.value = items
        _isCartEmpty.value = items.isEmpty()
        calculateCartTotals()
    }

    /**
     * Aumenta la cantidad de un producto
     */
    fun increaseItemQuantity(position: Int) {
        val currentItems = _cartItems.value ?: return
        if (position < currentItems.size) {
            currentItems[position].numberInCart++
            cartRepo.updateCartInStorage(currentItems)
            _cartItems.value = currentItems
            calculateCartTotals()
        }
    }

    /**
     * Disminuye la cantidad de un producto
     */
    fun decreaseItemQuantity(position: Int) {
        val currentItems = _cartItems.value ?: return
        if (position < currentItems.size) {
            if (currentItems[position].numberInCart > 1) {
                currentItems[position].numberInCart--
                cartRepo.updateCartInStorage(currentItems)
                _cartItems.value = currentItems
                calculateCartTotals()
            } else {
                removeItem(position)
            }
        }
    }

    /**
     * Elimina un producto del carrito
     */
    fun removeItem(position: Int) {
        val currentItems = _cartItems.value ?: return
        if (position < currentItems.size) {
            currentItems.removeAt(position)
            cartRepo.updateCartInStorage(currentItems)
            _cartItems.value = currentItems
            _isCartEmpty.value = currentItems.isEmpty()
            calculateCartTotals()
        }
    }

    /**
     * Aplica un código de descuento
     */
    fun applyDiscountCode(code: String, onResult: (Boolean, String) -> Unit) {
        if (code.isBlank()) {
            onResult(false, "Introduce un código")
            return
        }

        _isLoading.value = true
        cartRepo.validateDiscountCode(code.uppercase().trim()) { isValid, discountValue, message ->
            _isLoading.value = false
            if (isValid) {
                currentDiscountPercentage = discountValue
                _discountApplied.value = discountValue
                calculateCartTotals()
                onResult(true, "Descuento del $discountValue% aplicado")
            } else {
                onResult(false, message)
            }
        }
    }

    /**
     * Calcula los totales del carrito
     */
    private fun calculateCartTotals() {
        val baseFee = cartRepo.getTotalFee()

        // Aplicar descuento
        val discountAmount = (baseFee * currentDiscountPercentage / 100.0)
        val priceWithDiscount = baseFee - discountAmount

        // Calcular impuesto
        val taxAmount = priceWithDiscount * taxRate
        val finalTotal = priceWithDiscount + taxAmount

        // Redondear valores
        _subtotal.value = Math.round(priceWithDiscount * 100) / 100.0
        _tax.value = Math.round(taxAmount * 100) / 100.0
        _total.value = Math.round(finalTotal * 100) / 100.0
    }

    /**
     * Procesa el pago del carrito
     */
    fun processPayment(onSuccess: (ArrayList<ItemsModel>, Double) -> Unit) {
        val items = _cartItems.value
        val totalAmount = _total.value

        if (items.isNullOrEmpty()) {
            _errorMessage.value = "El carrito está vacío"
            return
        }

        if (totalAmount == null) {
            _errorMessage.value = "Error al calcular el total"
            return
        }

        onSuccess(ArrayList(items), totalAmount)
    }

}