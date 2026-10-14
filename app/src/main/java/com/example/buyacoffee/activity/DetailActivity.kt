package com.example.buyacoffee.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.buyacoffee.Helper.ManagmentCar
import com.example.buyacoffee.Helper.Precios
import com.example.buyacoffee.Helper.Tamano
import com.example.buyacoffee.model.ItemsModel
import com.example.buyacoffee.R
import com.example.buyacoffee.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemsModel
    private lateinit var cesta:ManagmentCar
    private var tamano = Tamano.TERCIO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cesta = ManagmentCar(this)
        savedInstanceState?.getString(KEY_TAMANO)?.let { tamano = Tamano.valueOf(it) }
        bundle()
        initSizeList()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_TAMANO, tamano.name)
    }

    private fun initSizeList() {
        binding.apply {
            quintosBtn.setOnClickListener { seleccionarTamano(Tamano.QUINTO) }
            terciosBtn.setOnClickListener { seleccionarTamano(Tamano.TERCIO) }
            medioLitroBtn.setOnClickListener { seleccionarTamano(Tamano.MEDIO_LITRO) }
        }
        seleccionarTamano(tamano)
    }

    /**
     * Marca el tamaño elegido y actualiza el precio mostrado.
     */
    private fun seleccionarTamano(nuevo: Tamano) {
        tamano = nuevo
        binding.apply {
            quintosBtn.setBackgroundResource(if (nuevo == Tamano.QUINTO) R.drawable.stroke_green_bg else 0)
            terciosBtn.setBackgroundResource(if (nuevo == Tamano.TERCIO) R.drawable.stroke_green_bg else 0)
            medioLitroBtn.setBackgroundResource(if (nuevo == Tamano.MEDIO_LITRO) R.drawable.stroke_green_bg else 0)
            tvPrecio.text = Precios.formatEuro(Precios.precioConTamano(item.price, nuevo))
        }
    }

    private fun bundle() {
        binding.apply {
            item = intent.getSerializableExtra("object") as ItemsModel
            Glide.with(this@DetailActivity)
                .load(item.picUrl[0])
                .into(binding.picMain)

            title.text = item.title
            descriptionTxt.text = item.description

            ratingTxt.text = item.rating.toString()

            addToCartBtn.setOnClickListener {
                // Se guarda una copia con el precio del tamaño elegido; item conserva el precio base
                val itemCesta = item.copy(
                    price = Precios.precioConTamano(item.price, tamano),
                    size = tamano.etiqueta,
                    numberInCart = cantidadActual()
                )
                cesta.insertItems(itemCesta)
            }
            backBtnFlecha.setOnClickListener {
                finish()
            }

            plusCart.setOnClickListener{
                tvCantidadNumerica.text = (cantidadActual() + 1).toString()
            }

            minusBtn.setOnClickListener{
                val number = cantidadActual()
                if(number>1){
                    tvCantidadNumerica.text = (number - 1).toString()
                }
            }

        }
    }

    private fun cantidadActual(): Int =
        binding.tvCantidadNumerica.text.toString().toIntOrNull() ?: 1

    companion object {
        private const val KEY_TAMANO = "tamano"
    }
}
