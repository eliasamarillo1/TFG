package com.example.buyacoffee.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.buyacoffee.R
import com.example.buyacoffee.adapter.CategoryAdapter
import com.example.buyacoffee.adapter.PopularAdapter
import com.example.buyacoffee.databinding.ActivityDashBinding
import com.example.buyacoffee.databinding.ViewholderPopularBinding
import com.example.buyacoffee.model.ItemsModel
import com.example.buyacoffee.repositorio.showProgressBar
import com.example.buyacoffee.viewmodel.DashViewModel
import kotlinx.coroutines.launch

class DashBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashBinding
    private val viewModel = DashViewModel()
    private lateinit var itemsAdapter: PopularAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponents()
    }


    //INICIALIZADORES
    /**
     * Inicializa todos los componentes de la interfaz de usuario.
     */
    private fun initComponents() {
        initBanner()
        initCategory()
        initPopular()
        setupSearchView()
        initAllItemsDisplay()
        initClickListeners()
    }
    /**
     * Configura los listeners de los botones de navegación.
     */
    private fun initClickListeners() {
        binding.cartBtn.setOnClickListener {
            navigateToCart()
        }

        binding.pedido.setOnClickListener {
            navigateToLastOrder()
        }

        binding.explorador.setOnClickListener {
            loadRandomItem()
        }
    }
    /**
     * Inicializa y carga el banner principal de la aplicación.
     */
    private fun initBanner() {
        showProgressBar(binding.progressBarBanner, true)

        viewModel.loadBanner().observe(this) { banners ->
            if (banners.isNotEmpty()) {
                Glide.with(this)
                    .load(banners[0].url)
                    .into(binding.ivBanner)
            }
            showProgressBar(binding.progressBarBanner, false)
        }
    }
    /**
     * Inicializa y carga las categorías de productos.
     */
    private fun initCategory() {
        showProgressBar(binding.progressbarCategorias, true)

        viewModel.loadCategory().observe(this) { categories ->
            binding.recyclerViewCategorias.apply {
                layoutManager = LinearLayoutManager(
                    this@DashBoardActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                adapter = CategoryAdapter(categories)
            }
            showProgressBar(binding.progressbarCategorias, false)
        }
    }
    /**
     * Inicializa y carga los productos populares.
     */
    private fun initPopular() {
        showProgressBar(binding.progressBarPopulares, true)

        viewModel.loadPopular().observe(this) { popularItems ->
            setupRecyclerView(popularItems)
            showProgressBar(binding.progressBarPopulares, false)
        }
    }
    /**
     * Inicializa la visualización de todos los ítems disponibles.
     */
    private fun initAllItemsDisplay() {
        showProgressBar(binding.progressBarPopulares, true)
        viewModel.loadAllItems()

        viewModel.displayedItems.observe(this) { items ->
            if (!::itemsAdapter.isInitialized) {
                setupRecyclerView(items)
            } else {
                itemsAdapter.updateList(items)
            }
            showProgressBar(binding.progressBarPopulares, false)
        }
    }


    //SETUPS
    /**
     * Configura el RecyclerView con los ítems proporcionados.
     *
     * @param items Lista de ítems para mostrar
     */
    private fun setupRecyclerView(items: List<ItemsModel>) {
        itemsAdapter = PopularAdapter(items)
        binding.rvPopulares.apply {
            layoutManager = GridLayoutManager(this@DashBoardActivity, 2)
            adapter = itemsAdapter
        }
    }
    /**
     * Configura la funcionalidad del SearchView para buscar productos.
     */
    private fun setupSearchView() {
        binding.searchView.apply {
            setOnQueryTextListener(createQueryTextListener())
            setOnQueryTextFocusChangeListener(createFocusChangeListener())
            setOnCloseListener(createCloseListener())
        }
    }
    /**
     * Configura la vista del ítem (misma lógica que PopularAdapter.onBindViewHolder)
     */
    private fun setupItemView(binding: ViewholderPopularBinding, item: ItemsModel) {
        // Configurar título y precio exactamente como en el adapter
        binding.titleTxt.text = item.title
        binding.pricetxt.text = "$${item.price}"

        Glide.with(this)
            .load(item.picUrl.firstOrNull())
            .fitCenter()
            .into(binding.pic)

    }


    //CREATE
    /**
     * Crea el listener para cambios de foco en el SearchView.
     *
     * @return OnFocusChangeListener configurado
     */
    private fun createFocusChangeListener(): View.OnFocusChangeListener {
        return View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                updateTitle(getString(R.string.productos))
                viewModel.loadAllItems()
            }
        }
    }
    /**
     * Crea el listener para el cierre del SearchView.
     *
     * @return SearchView.OnCloseListener configurado
     */
    private fun createCloseListener(): SearchView.OnCloseListener {
        return SearchView.OnCloseListener {
            updateTitle(getString(R.string.populares))
            restorePopularItems()
            false
        }
    }
    /**
     * Crea el listener para cambios en el texto de búsqueda.
     *
     * @return SearchView.OnQueryTextListener configurado
     */
    private fun createQueryTextListener(): SearchView.OnQueryTextListener {
        return object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrBlank()) {
                    updateTitle(getString(R.string.productos))
                    viewModel.filterItems(newText)
                }
                return true
            }
        }
    }


    //NAVIGATE
    /**
     * Navega al DetailActivity
     */
    private fun navigateToDetail(item: ItemsModel) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("object", item)
        startActivity(intent)
    }
    /**
     * Navega a la actividad del carrito de compras.
     */
    private fun navigateToCart() {
        startActivity(Intent(this, CartActivity::class.java))
    }
    /**
     * Navega a la actividad de último pedido.
     */
    private fun navigateToLastOrder() {
        startActivity(Intent(this, LastOrderActivity::class.java))
    }


    //ITEMS
    /**
     * Maneja el resultado de la carga de ítems aleatorios.
     *
     * @param items Lista de ítems obtenidos
     */
    private fun handleItemsResult(items: List<ItemsModel>?) {
        if (items.isNullOrEmpty()) {
            showErrorToast("No hay ítems disponibles.")
        } else {
            val randomItem = items.random()
            showRandomItemDialog(randomItem)
        }
    }
    /**
     * Muestra un diálogo con información de un ítem aleatorio.
     *
     * @param item El ítem a mostrar en el diálogo
     */
    private fun showRandomItemDialog(item: ItemsModel) {
        // mismo layout que los ítems populares
        val binding = ViewholderPopularBinding.inflate(layoutInflater)

        // Configurar el ítem como el adapter
        setupItemView(binding, item)

        // Crear el diálogo
        val dialog = AlertDialog.Builder(this)
            .setView(binding.root)
            .setCancelable(true)
            .create()

        dialog.setOnShowListener {
            dialog.window?.setLayout(800, 750) // Ajusta estos númerosv
        }
        // Configurar click listener para navegar al detalle (misma lógica que el adapter)
        binding.root.setOnClickListener {
            dialog.dismiss()
            navigateToDetail(item)
        }
        dialog.show()
    }
    /**
     * Restaura la vista de productos populares.
     */
    private fun restorePopularItems() {
        viewModel.loadPopular().observe(this) { popularItems ->
            itemsAdapter = PopularAdapter(popularItems)
            binding.rvPopulares.adapter = itemsAdapter
        }
    }
    /**
     * Carga un ítem aleatorio y lo muestra en un diálogo.
     */
    private fun loadRandomItem() {
        lifecycleScope.launch {
            try {
                val items = viewModel.getAllItemsOnce()
                handleItemsResult(items)
            } catch (e: Exception) {
                showErrorToast("Error al cargar ítems: ${e.message}")
            }
        }
    }

    /**
     * Actualiza el título de la sección de productos.
     *
     * @param title Nuevo título a mostrar
     */
    private fun updateTitle(title: String) {
        binding.tituloPopulares.text = title
    }
    /**
     * Muestra un mensaje de error como Toast.
     *
     * @param message Mensaje de error a mostrar
     */
    private fun showErrorToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }



}