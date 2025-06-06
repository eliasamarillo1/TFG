package com.example.buyacoffee.repositorio

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.buyacoffee.Helper.TinyDB
import com.example.buyacoffee.model.ItemsModel
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Repositorio encargado de gestionar los datos del carrito de compras.
 * Maneja el almacenamiento local y las operaciones con Firebase.
 *
 * @property context Contexto de la aplicación, necesario para acceder a recursos.
 */
class CartRepo(private val context: Context) {
    private val tinyDB = TinyDB(context)

    companion object {
        private const val CART_KEY = "CartList"
        private const val LAST_ORDER_ITEMS_KEY = "LastOrderItems"
        private const val LAST_ORDER_CODE_KEY = "LastOrderCode"
    }

    /**
     * Inserta un ítem al carrito. Si el ítem ya existe, actualiza su cantidad.
     *
     * @param item El objeto [ItemsModel] que se desea añadir o actualizar en el carrito.
     */
    fun insertItems(item: ItemsModel) {
        val listItem = getListCart()
        val existAlready = listItem.any { it.title == item.title }
        val index = listItem.indexOfFirst { it.title == item.title }

        if (existAlready) {
            listItem[index].numberInCart = item.numberInCart
        } else {
            listItem.add(item)
        }
        tinyDB.putListObject(CART_KEY, listItem)
        Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show()
    }

    /**
     * Obtiene la lista actual de ítems en el carrito.
     *
     * @return Una lista mutable de [ItemsModel] que representa el contenido del carrito.
     */
    fun getListCart(): ArrayList<ItemsModel> {
        return tinyDB.getListObject(CART_KEY) ?: arrayListOf()
    }

    /**
     * Actualiza la lista del carrito en el almacenamiento local.
     * Método agregado para compatibilidad con MVVM.
     *
     * @param items Lista actualizada de items del carrito.
     */
    fun updateCartInStorage(items: ArrayList<ItemsModel>) {
        tinyDB.putListObject(CART_KEY, items)
    }

    /**
     * Calcula el costo total de todos los productos en el carrito.
     *
     * @return El total a pagar como [Double].
     */
    fun getTotalFee(): Double {
        val listItem = getListCart()
        var fee = 0.0
        for (item in listItem) {
            fee += item.price * item.numberInCart
            Log.d("CartRepo", "Item: ${item.title}, Price: ${item.price}, Quantity: ${item.numberInCart}")
        }
        Log.d("CartRepo", "Total Fee: $fee")
        return fee
    }

    /**
     * Valida un código de descuento en Firebase.
     *
     * @param code Código de descuento a validar.
     * @param onResult Callback que retorna (esValido, valorDescuento, mensaje).
     */
    fun validateDiscountCode(code: String, onResult: (Boolean, Double, String) -> Unit) {
        val ref = FirebaseDatabase.getInstance().getReference("Descuentos").child(code)

        ref.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val valor = snapshot.child("valor").value.toString().toDoubleOrNull() ?: 0.0
                onResult(true, valor, "Descuento aplicado correctamente")
            } else {
                onResult(false, 0.0, "Código no válido")
            }
        }.addOnFailureListener {
            onResult(false, 0.0, "Error al validar el código")
        }
    }

    /**
     * Limpia el carrito de compras.
     */
    fun clearCart() {
        tinyDB.remove(CART_KEY)
    }

    /**
     * Guarda información del último pedido realizado.
     *
     * @param codigo Código del pedido.
     * @param items Lista de items del pedido.
     */
    fun saveLastOrder(codigo: String, items: List<ItemsModel>) {
        tinyDB.putListObject(LAST_ORDER_ITEMS_KEY, ArrayList(items))
        tinyDB.putString(LAST_ORDER_CODE_KEY, codigo)
    }

    /**
     * Obtiene los items del último pedido.
     *
     * @return Lista de items del último pedido.
     */
    fun getLastOrderItems(): ArrayList<ItemsModel> {
        return tinyDB.getListObject(LAST_ORDER_ITEMS_KEY) ?: arrayListOf()
    }

    /**
     * Obtiene el código del último pedido.
     *
     * @return Código del último pedido.
     */
    fun getLastOrderCode(): String {
        return tinyDB.getString(LAST_ORDER_CODE_KEY) ?: ""
    }

    /**
     * Sube un pedido a Firebase Realtime Database.
     *
     * @param codigo Código único del pedido.
     * @param total Total del pedido.
     * @param items Lista de items del pedido.
     */
    fun subirPedidoAFirebase(codigo: String, total: Double, items: ArrayList<ItemsModel>?) {
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
                Toast.makeText(context, "Pedido guardado correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al guardar el pedido", Toast.LENGTH_SHORT).show()
            }
    }

    // MÉTODOS LEGACY - Mantenidos para compatibilidad con código existente
    // Estos métodos están marcados como deprecated y deberían ser migrados al ViewModel

    @Deprecated("Use ViewModel methods instead")
    fun minusItem(listItems: ArrayList<ItemsModel>, position: Int, listener: com.example.buyacoffee.Helper.ChangeNumberItemsListener) {
        if (listItems[position].numberInCart == 1) {
            listItems.removeAt(position)
        } else {
            listItems[position].numberInCart--
        }
        tinyDB.putListObject(CART_KEY, listItems)
        listener.onChanged()
    }

    @Deprecated("Use ViewModel methods instead")
    fun romveItem(listItems: ArrayList<ItemsModel>, position: Int, listener: com.example.buyacoffee.Helper.ChangeNumberItemsListener) {
        listItems.removeAt(position)
        tinyDB.putListObject(CART_KEY, listItems)
        listener.onChanged()
    }

    @Deprecated("Use ViewModel methods instead")
    fun plusItem(listItems: ArrayList<ItemsModel>, position: Int, listener: com.example.buyacoffee.Helper.ChangeNumberItemsListener) {
        listItems[position].numberInCart++
        tinyDB.putListObject(CART_KEY, listItems)
        listener.onChanged()
    }
}