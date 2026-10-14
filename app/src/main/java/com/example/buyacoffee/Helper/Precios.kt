package com.example.buyacoffee.Helper

import java.util.Locale

/**
 * Tamaños disponibles en la ficha de producto.
 * El precio que viene de Firebase corresponde al tercio; el resto se calcula con [factor].
 *
 * @property etiqueta Texto que se muestra en la cesta y en el ticket.
 * @property factor Multiplicador que se aplica al precio base.
 */
enum class Tamano(val etiqueta: String, val factor: Double) {
    QUINTO("Quinto", 0.8),
    TERCIO("Tercio", 1.0),
    MEDIO_LITRO("1/2 L", 1.5)
}

/**
 * Formato de precios en un único sitio, para que cesta, ticket y listados coincidan.
 * El cálculo de la cesta (descuento e IVA) está en [CartCalculator].
 */
object Precios {

    private val LOCALE_ES = Locale.forLanguageTag("es-ES")

    /** Devuelve el importe con dos decimales y el símbolo del euro, p. ej. "3,50 €". */
    fun formatEuro(valor: Double): String = String.format(LOCALE_ES, "%.2f €", valor)

    fun precioConTamano(precioBase: Double, tamano: Tamano): Double =
        CartCalculator.redondear(precioBase * tamano.factor)
}
