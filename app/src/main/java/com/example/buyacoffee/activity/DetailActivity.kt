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
import com.example.buyacoffee.model.ItemsModel
import com.example.buyacoffee.R
import com.example.buyacoffee.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemsModel
    private lateinit var cesta:ManagmentCar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cesta = ManagmentCar(this)
        bundle()
        initSizeList()

    }
    private fun initSizeList() {
        binding.apply {

            quintosBtn.setOnClickListener {
                quintosBtn.setBackgroundResource(R.drawable.stroke_green_bg)
                terciosBtn.setBackgroundResource(0)
                medioLitroBtn.setBackgroundResource(0)
            }
            terciosBtn.setOnClickListener {
                quintosBtn.setBackgroundResource(0)
                terciosBtn.setBackgroundResource(R.drawable.stroke_green_bg)
                medioLitroBtn.setBackgroundResource(0)
            }
            medioLitroBtn.setOnClickListener {
                quintosBtn.setBackgroundResource(0)
                terciosBtn.setBackgroundResource(0)
                medioLitroBtn.setBackgroundResource(R.drawable.stroke_green_bg)
            }
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

            tvPrecio.text= item.price.toString()
            ratingTxt.text = item.rating.toString()

            addToCartBtn.setOnClickListener {
                item.numberInCart = Integer.valueOf(
                    tvCantidadNumerica.text.toString()
                )
                cesta.insertItems(item)
            }
            backBtnFlecha.setOnClickListener {
                finish()
            }

            plusCart.setOnClickListener{
                tvCantidadNumerica.text = (item.numberInCart + 1).toString()
                item.numberInCart++
            }

            minusBtn.setOnClickListener{
                var number = Integer.parseInt(tvCantidadNumerica.text.toString())
                if(number>1){
                    number--
                    tvCantidadNumerica.text = number.toString()
                }
            }

        }
    }
}