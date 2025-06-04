package com.example.buyacoffee.repositorio

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.buyacoffee.Helper.ChangeNumberItemsListener
import com.example.buyacoffee.Helper.TinyDB
import com.example.buyacoffee.model.ItemsModel
import com.example.buyacoffee.model.PedidoModel
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Clase encargada de gestionar el carrito de compras de la aplicación.
 *
 * @property context Contexto de la aplicación, necesario para acceder a recursos y mostrar mensajes.
 */
class CartRepo(val context: Context) {
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
        tinyDB.putListObject("CartList", listItem)
        Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show()
    }

    /**
     * Obtiene la lista actual de ítems en el carrito.
     *
     * @return Una lista mutable de [ItemsModel] que representa el contenido del carrito.
     */
    fun getListCart(): ArrayList<ItemsModel> {
        return tinyDB.getListObject("CartList") ?: arrayListOf()
    }

    /**
     * Disminuye en uno la cantidad del ítem indicado en la posición dada.
     * Si la cantidad llega a 1, elimina el ítem del carrito.
     *
     * @param listItems Lista actual del carrito.
     * @param position Índice del ítem a modificar.
     * @param listener Callback para notificar cambios.
     */
    fun minusItem(listItems: ArrayList<ItemsModel>, position: Int, listener: ChangeNumberItemsListener) {
        if (listItems[position].numberInCart == 1) {
            listItems.removeAt(position)
        } else {
            listItems[position].numberInCart--
        }
        tinyDB.putListObject("CartList", listItems)
        listener.onChanged()
    }

    /**
     * Elimina un ítem del carrito según su posición.
     *
     * @param listItems Lista actual del carrito.
     * @param position Índice del ítem a eliminar.
     * @param listener Callback para notificar cambios.
     */
    fun romveItem(listItems: ArrayList<ItemsModel>, position: Int, listener: ChangeNumberItemsListener) {
        listItems.removeAt(position)
        tinyDB.putListObject("CartList", listItems)
        listener.onChanged()
    }

    /**
     * Aumenta en uno la cantidad del ítem indicado en la posición dada.
     *
     * @param listItems Lista actual del carrito.
     * @param position Índice del ítem a modificar.
     * @param listener Callback para notificar cambios.
     */
    fun plusItem(listItems: ArrayList<ItemsModel>, position: Int, listener: ChangeNumberItemsListener) {
        listItems[position].numberInCart++
        tinyDB.putListObject("CartList", listItems)
        listener.onChanged()
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

            Log.d("Item", item.title + " " + item.price + " " + item.numberInCart)
        }
        Log.d("Total", fee.toString())
        return fee

    }
    /**
     * Limpia el carrito de compras.
     */
    fun clearCart() {
        tinyDB.remove("CartList")
    }

    fun saveLastOrder(codigo: String, items: List<ItemsModel>) {
        tinyDB.putListObject(LAST_ORDER_ITEMS_KEY, ArrayList(items))
        tinyDB.putString(LAST_ORDER_CODE_KEY, codigo)
    }
    fun getLastOrderItems(): ArrayList<ItemsModel> {
        return tinyDB.getListObject(LAST_ORDER_ITEMS_KEY) ?: arrayListOf()
    }

    fun getLastOrderCode(): String {
        return tinyDB.getString(LAST_ORDER_CODE_KEY) ?: ""
    }

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

         var pedido = PedidoModel(codigo, total.toString(), fechaHora , items.toString())

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


}
