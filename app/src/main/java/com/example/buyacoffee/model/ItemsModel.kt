package com.example.buyacoffee.model

import java.io.Serializable

/**
 * Modelo de datos que representa un ítem de producto en la aplicación.
 *
 * @property title Título o nombre del producto.
 * @property description Descripción del producto.
 * @property picUrl Lista de URLs de imágenes asociadas al producto.
 * @property price Precio del producto.
 * @property rating Calificación promedio del producto.
 * @property numberInCart Número de unidades del producto en el carrito.
 * @property extra Información adicional o personalizada del producto.
 * @property id Identificador único del producto.
 * @property size Tamaño elegido en la ficha (vacío si no aplica).
 * Implementa [Serializable] para permitir que los objetos puedan ser pasados entre componentes de Android.
 */
data class ItemsModel(
    var title: String = "",
    var description: String = "",
    var picUrl: ArrayList<String> = arrayListOf(),
    var price: Double = 0.0,
    var rating: Double = 0.0,
    var numberInCart: Int = 0,
    var extra: String = "",
    var id: String = "",
    var size: String = ""
) : Serializable {

    /** Nombre para cesta y ticket, con el tamaño si lo hay. */
    fun nombreConTamano(): String = if (size.isBlank()) title else "$title ($size)"
}


object LastOrderManager {

    var lastOrderItems: List<ItemsModel>? = null
    var lastOrderTotalFee: Double? = null

    fun saveOrder(items: List<ItemsModel>, total: Double) {
        lastOrderItems = items
        lastOrderTotalFee = total
    }


    fun clearLastOrder() {
        lastOrderItems = null
        lastOrderTotalFee = null
    }

}

