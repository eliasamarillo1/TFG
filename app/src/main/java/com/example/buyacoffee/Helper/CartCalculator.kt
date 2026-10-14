package com.example.buyacoffee.Helper

import com.example.buyacoffee.model.ItemsModel

/**
 * Resultado del cálculo de la cesta. Todos los importes están redondeados a 2 decimales.
 *
 * @property subtotal Suma de precio x cantidad de todos los productos, sin descuento ni IVA.
 * @property descuento Importe descontado según el porcentaje aplicado.
 * @property baseImponible Subtotal menos el descuento.
 * @property impuesto IVA calculado sobre la base imponible.
 * @property total Importe final a pagar (base imponible + IVA).
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

    /** Tipo de IVA aplicado en la cesta. */
    const val IVA = 0.02

    /**
     * Suma precio x cantidad de cada producto de la cesta.
     */
    fun subtotal(items: List<ItemsModel>): Double =
        items.sumOf { it.price * it.numberInCart }

    /**
     * Calcula el resumen de la cesta aplicando un descuento en porcentaje (0-100) y el IVA.
     */
    fun calcular(items: List<ItemsModel>, descuentoPorcentaje: Double = 0.0): ResumenCesta {
        val subtotal = subtotal(items)
        val descuento = redondear(subtotal * descuentoPorcentaje / 100.0)
        val precioConDescuento = subtotal - descuento
        val impuesto = redondear(precioConDescuento * IVA)
        val total = redondear(precioConDescuento + impuesto)

        return ResumenCesta(
            subtotal = redondear(subtotal),
            descuento = descuento,
            baseImponible = redondear(precioConDescuento),
            impuesto = impuesto,
            total = total
        )
    }

    /** Redondea un importe a 2 decimales. */
    fun redondear(valor: Double): Double = Math.round(valor * 100) / 100.0
}
