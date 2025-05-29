package com.example.buyacoffee.repositorio

import android.view.View
import android.widget.Toast

//GENERAL

/**
 * Muestra u oculta una barra de progreso.
 *
 * @param progressBar Barra de progreso a mostrar/ocultar
 * @param show true para mostrar, false para ocultar
 */
fun showProgressBar(progressBar: View, show: Boolean) {
    progressBar.visibility = if (show) View.VISIBLE else View.GONE
}


