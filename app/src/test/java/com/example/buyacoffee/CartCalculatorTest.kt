package com.example.buyacoffee

import com.example.buyacoffee.Helper.CartCalculator
import com.example.buyacoffee.model.ItemsModel
import org.junit.Assert.assertEquals
import org.junit.Test

class CartCalculatorTest {

    private val delta = 0.001

    private fun item(title: String, price: Double, cantidad: Int) =
        ItemsModel(title = title, price = price, numberInCart = cantidad)

    @Test
    fun cestaVacia_todoEsCero() {
        val resumen = CartCalculator.calcular(emptyList())

        assertEquals(0.0, resumen.subtotal, delta)
        assertEquals(0.0, resumen.descuento, delta)
        assertEquals(0.0, resumen.impuesto, delta)
        assertEquals(0.0, resumen.total, delta)
    }

    @Test
    fun subtotal_sumaPrecioPorCantidad() {
        val items = listOf(item("IPA", 2.5, 3), item("Lager", 1.2, 2))

        assertEquals(9.9, CartCalculator.subtotal(items), delta)
    }

    @Test
    fun productoConCantidadCero_noSuma() {
        val items = listOf(item("IPA", 2.5, 0), item("Stout", 4.0, 1))

        assertEquals(4.0, CartCalculator.subtotal(items), delta)
    }

    @Test
    fun sinDescuento_elIvaVaIncluido() {
        val resumen = CartCalculator.calcular(listOf(item("IPA", 11.0, 2)))

        assertEquals(22.0, resumen.subtotal, delta)
        assertEquals(0.0, resumen.descuento, delta)
        assertEquals(20.0, resumen.baseImponible, delta)
        assertEquals(2.0, resumen.impuesto, delta)
        assertEquals(22.0, resumen.total, delta)
    }

    @Test
    fun descuentoPorcentaje_llegaAlTotal() {
        val resumen = CartCalculator.calcular(listOf(item("IPA", 10.0, 2)), descuentoPorcentaje = 10.0)

        assertEquals(2.0, resumen.descuento, delta)
        assertEquals(18.0, resumen.total, delta)
        assertEquals(16.36, resumen.baseImponible, delta)
        assertEquals(1.64, resumen.impuesto, delta)
    }

    @Test
    fun baseMasIva_esElTotal() {
        val resumen = CartCalculator.calcular(listOf(item("Lager", 3.33, 3)), descuentoPorcentaje = 15.0)

        assertEquals(resumen.total, CartCalculator.redondear(resumen.baseImponible + resumen.impuesto), delta)
    }

    @Test
    fun descuentoFueraDeRango_seLimita() {
        val resumen = CartCalculator.calcular(listOf(item("IPA", 5.0, 1)), descuentoPorcentaje = 150.0)

        assertEquals(5.0, resumen.descuento, delta)
        assertEquals(0.0, resumen.total, delta)
    }

    @Test
    fun descuentoDelCienPorCien_totalCero() {
        val resumen = CartCalculator.calcular(listOf(item("IPA", 3.75, 4)), descuentoPorcentaje = 100.0)

        assertEquals(15.0, resumen.descuento, delta)
        assertEquals(0.0, resumen.impuesto, delta)
        assertEquals(0.0, resumen.total, delta)
    }

    @Test
    fun importes_seRedondeanADosDecimales() {
        // 3 x 3.33 = 9.99 -> 15% = 1.4985 -> 1.50; total 8.49; base 8.49 / 1.10 = 7.718 -> 7.72; IVA 0.77
        val resumen = CartCalculator.calcular(listOf(item("Lager", 3.33, 3)), descuentoPorcentaje = 15.0)

        assertEquals(9.99, resumen.subtotal, delta)
        assertEquals(1.5, resumen.descuento, delta)
        assertEquals(8.49, resumen.total, delta)
        assertEquals(7.72, resumen.baseImponible, delta)
        assertEquals(0.77, resumen.impuesto, delta)
    }

    @Test
    fun redondear_aDosDecimales() {
        assertEquals(1.24, CartCalculator.redondear(1.2449), delta)
        assertEquals(1.25, CartCalculator.redondear(1.245001), delta)
    }
}
