package com.example.buyacoffee.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.buyacoffee.Helper.ChangeNumberItemsListener
import com.example.buyacoffee.Helper.ManagmentCar
import com.example.buyacoffee.adapter.CartAdapter
import com.example.buyacoffee.databinding.ActivityCartBinding

class CartActivity : AppCompatActivity() {
    lateinit var bining: ActivityCartBinding
    lateinit var managmentCar: ManagmentCar
    private var tax:Double =0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bining = ActivityCartBinding.inflate(layoutInflater)
        setContentView(bining.root)
        managmentCar = ManagmentCar(this)
        calculateCart()
        setVariable()
        initCartlist()

    }

    private fun initCartlist() {
        bining.apply {
            cartView.layoutManager=
                LinearLayoutManager(this@CartActivity, LinearLayoutManager.VERTICAL,false)
            cartView.adapter= CartAdapter(
                managmentCar.getListCart(),
                this@CartActivity,
                object : ChangeNumberItemsListener {
                    override fun onChanged() {
                        calculateCart()
                    }

                }
            )
        }
    }

    private fun setVariable() {
        bining.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun calculateCart() {

        val iva= 0.02
        val delivery:Double = 10.0
        tax = Math.round((managmentCar.getTotalFee() * iva) * 100) / 100.0
        val total = Math.round((managmentCar.getTotalFee() + tax + delivery) * 100) / 100.0
        val itemTotal = Math.round(managmentCar.getTotalFee() * 100) / 100.0

        bining.apply {
            totalFreetxt.text = "$itemTotal €"
            taxTxt.text = "$tax €"
            deliveryTxt.text="$delivery €"
            totalTxt.text = "$total €"

        }

    }
}