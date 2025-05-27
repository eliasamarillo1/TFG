package com.example.buyacoffee.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.buyacoffee.R
import com.example.buyacoffee.adapter.CategoryAdapter
import com.example.buyacoffee.adapter.PopularAdapter
import com.example.buyacoffee.databinding.ActivityDashBinding
import com.example.buyacoffee.model.ItemsModel
import com.example.buyacoffee.viewmodel.DashViewModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DashBoardActivity : AppCompatActivity() {
    lateinit var binding: ActivityDashBinding
    private val viewModel = DashViewModel()
    private lateinit var itemsAdapter: PopularAdapter




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDashBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        initBanner()
        initCategory()
        initPopular()
        setupSearchView()
        initAllItemsDisplay()
        initBtn()
    }

    private fun initBtn() {
        binding.cartBtn.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        binding.explorador.setOnClickListener {
        }
        binding.pedido.setOnClickListener {
            val intent = Intent(this, LastOrderActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initBanner() {
        binding.progressBarBanner.visibility = View.VISIBLE
        viewModel.loadBanner().observeForever {
            Glide.with(this@DashBoardActivity)
                .load(it[0].url)
                .into(binding.ivBanner)
            binding.progressBarBanner.visibility = View.GONE
        }
    }

    private fun initCategory(){
        binding.progressbarCategorias.visibility = View.VISIBLE
        viewModel.loadCategory().observeForever {
            binding.recyclerViewCategorias.layoutManager =
                LinearLayoutManager(this@DashBoardActivity, LinearLayoutManager.HORIZONTAL,
                    false)

            binding.recyclerViewCategorias.adapter = CategoryAdapter(it)
            binding.progressbarCategorias.visibility = View.GONE
        }
    }

    private fun initPopular(){
        binding.progressBarPopulares.visibility = View.VISIBLE
        viewModel.loadPopular().observeForever {
            binding.rvPopulares.layoutManager =GridLayoutManager(this,2)
            binding.rvPopulares.adapter = PopularAdapter(it)

            binding.progressBarPopulares.visibility = View.GONE
        }
    }
    private fun initAllItemsDisplay() {
        binding.progressBarPopulares.visibility = View.VISIBLE // Reutilizamos la progress bar
        viewModel.loadAllItems() // Carga todos los ítems

        viewModel.displayedItems.observe(this) { items ->
            if (!::PopularAdapter.isOpen) {
                // Inicializa el adaptador la primera vez que se reciben datos
                itemsAdapter = PopularAdapter(items)
                binding.rvPopulares.layoutManager = GridLayoutManager(this, 2) // Reutilizamos el RecyclerView
                binding.rvPopulares.adapter = itemsAdapter
            } else {
                // Actualiza la lista en el adaptador existente
                itemsAdapter.updateList(items)
            }
            binding.progressBarPopulares.visibility = View.GONE
            // Opcional: Mostrar un mensaje si la lista filtrada está vacía
            // binding.noResultsText.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        }
    }


    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrBlank()) {
                    binding.tituloPopulares.text = getString(R.string.productos) // Cambia el título al buscar
                    viewModel.filterItems(newText)
                }
                return true
            }
        })

        // Cuando se enfoca el SearchView (se toca)
        binding.searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.tituloPopulares.text = getString(R.string.productos)
                viewModel.loadAllItems()
            }
        }

        // Cuando se cierra (al pulsar la X)
        binding.searchView.setOnCloseListener {
            binding.tituloPopulares.text = getString(R.string.populares) // Restaurar el título original
            viewModel.loadPopular().observe(this@DashBoardActivity) {
                itemsAdapter = PopularAdapter(it)
                binding.rvPopulares.adapter = itemsAdapter
            }
            false
        }
    }

}


