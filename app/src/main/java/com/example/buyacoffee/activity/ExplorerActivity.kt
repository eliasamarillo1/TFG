package com.example.buyacoffee.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.buyacoffee.R
import com.example.buyacoffee.adapter.ItemsListCategoryAdapter
import com.example.buyacoffee.databinding.ActivityExplorerBinding
import com.example.buyacoffee.viewmodel.DashViewModel

class ExplorerActivity : AppCompatActivity() {
    lateinit var bindingItems: ActivityExplorerBinding
    private val viewModel = DashViewModel()
    private var id: String = ""
    private var title: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bindingItems = ActivityExplorerBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_explorer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initExplorer()
    }


    private fun initExplorer(){
        bindingItems.apply {
            viewModel.loadItems().observe(this@ExplorerActivity, Observer {
                rvItems.layoutManager = LinearLayoutManager(
                    this@ExplorerActivity,
                    LinearLayoutManager.VERTICAL, false
                )
                rvItems.adapter = ItemsListCategoryAdapter(it)
            })
        }
    }

}