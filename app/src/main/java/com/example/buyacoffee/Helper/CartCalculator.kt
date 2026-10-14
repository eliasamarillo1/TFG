package com.example.buyacoffee.Helper

import com.example.buyacoffee.model.ItemsModel

/**
 * Resultado del cálculo de la cesta. Todos los importes están redondeados a 2 decimales.
 *
 * @property subtotal Suma de precio x cantidad de todos los productos, sin descuento ni IVA.
 * @property descuento Importe descontado según el porcentaje aplicado.
 * @property baseImponible Total sin IVA.
 * @property impuesto Cuota de IVA incluida en el total.
 * @property total Importe final a pagar (subtotal menos descuento, con el IVA ya incluido).
 */
data class ResumenCesta(
    val subtotal: Double,
    val descuento: Double,
    val baseImponible: Double,
    val impuesto: Double,
    val total: Double
)

/**
 * Lógica de cálculo de la cesta sin dependencias de Android, para poder probarla con tests unitarios.
 */
object CartCalculator {

    /**
     * IVA de la hostelería en España (consumo en bar): 10 %.
     * Los precios al consumidor se muestran con el IVA incluido, así que el IVA
     * se desglosa del total en lugar de sumarse encima.
     */
    const val IVA = 0.10

    /**
     * Suma precio x cantidad de cada producto de la cesta.
     */
    fun subtotal(items: List<ItemsModel>): Double =
        items.sumOf { it.price * it.numberInCart }

    /**
     * Calcula el resumen de la cesta aplicando un descuento en porcentaje (0-100)
     * y desglosando el IVA incluido en el precio.
     */
    fun calcular(items: List<ItemsModel>, descuentoPorcentaje: Double = 0.0): ResumenCesta {
        val subtotal = subtotal(items)
        val descuento = redondear(subtotal * descuentoPorcentaje.coerceIn(0.0, 100.0) / 100.0)
        val total = redondear(subtotal - descuento)
        val baseImponible = redondear(total / (1 + IVA))
        val impuesto = redondear(total - baseImponible)

        return ResumenCesta(
            subtotal = redondear(subtotal),
            descuento = descuento,
            baseImponible = baseImponible,
            impuesto = impuesto,
            total = total
        )
    }

    /** Redondea un importe a 2 decimales. */
    fun redondear(valor: Double): Double = Math.round(valor * 100) / 100.0
}
