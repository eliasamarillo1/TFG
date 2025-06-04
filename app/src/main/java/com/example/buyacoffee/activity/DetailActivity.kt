package com.example.buyacoffee.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import com.bumptech.glide.Glide
import com.example.buyacoffee.repositorio.CartRepo
import com.example.buyacoffee.model.ItemsModel
import com.example.buyacoffee.R
import com.example.buyacoffee.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemsModel
    private lateinit var cesta: CartRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cesta = CartRepo(this)
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
            backBtn.setOnClickListener {
                val intent = Intent(this@DetailActivity, DashBoardActivity::class.java)
                startActivity(intent)
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