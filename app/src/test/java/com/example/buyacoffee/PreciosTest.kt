package com.example.buyacoffee

import com.example.buyacoffee.Helper.Precios
import com.example.buyacoffee.Helper.Tamano
import com.example.buyacoffee.model.ItemsModel
import org.junit.Assert.assertEquals
import org.junit.Test

class PreciosTest {

    private val delta = 0.001

    @Test
    fun formatoEuro_conComaDecimal() {
        // Según la versión de Java/Android el espacio puede ser no separable
        assertEquals("3,50 €", Precios.formatEuro(3.5).replace('\u00a0', ' '))
    }

    @Test
    fun precioConTamano_aplicaElFactor() {
        assertEquals(1.6, Precios.precioConTamano(2.0, Tamano.QUINTO), delta)
        assertEquals(2.0, Precios.precioConTamano(2.0, Tamano.TERCIO), delta)
        assertEquals(3.0, Precios.precioConTamano(2.0, Tamano.MEDIO_LITRO), delta)
    }

    @Test
    fun nombreConTamano_soloSiHayTamano() {
        assertEquals("IPA (Tercio)", ItemsModel(title = "IPA", size = "Tercio").nombreConTamano())
        assertEquals("IPA", ItemsModel(title = "IPA").nombreConTamano())
    }
}
