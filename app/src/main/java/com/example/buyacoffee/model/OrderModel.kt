package com.example.buyacoffee.model

/**
 * Pedido guardado en Firebase en Pedidos/{uid}/{codigo}.
 *
 * @property codigo Código aleatorio que se muestra en el ticket.
 * @property total Importe final pagado.
 * @property fecha Fecha y hora legibles en que se hizo el pedido.
 * @property timestamp Milisegundos del servidor, usados para ordenar el historial.
 * @property items Productos del pedido.
 */
data class OrderModel(
    var codigo: String = "",
    var total: Double = 0.0,
    var fecha: String = "",
    var timestamp: Long = 0L,
    var items: List<OrderItemModel> = emptyList()
)

/**
 * Línea de un pedido: solo lo necesario para mostrarlo en el historial.
 */
data class OrderItemModel(
    var title: String = "",
    var price: Double = 0.0,
    var numberInCart: Int = 0
)
