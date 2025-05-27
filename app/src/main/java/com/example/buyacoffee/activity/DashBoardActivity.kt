package com.example.buyacoffee.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
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
import com.example.buyacoffee.model.ItemsModel
import com.example.buyacoffee.viewmodel.DashViewModel
import kotlinx.coroutines.launch

/**
 *
 * Esta actividad maneja la pantalla principal de la aplicación, mostrando:
 * - Banner promocional
 * - Categorías de productos
 * - Productos populares
 * - Funcionalidad de búsqueda
 * - Navegación hacia el carrito y pedidos
 * - Explorador de productos aleatorios
 *
 * @author Elías Amarillo
 */
class DashBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashBinding
    private val viewModel = DashViewModel()
    private lateinit var itemsAdapter: PopularAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeComponents()
    }

    /**
     * Inicializa todos los componentes de la interfaz de usuario.
     */
    private fun initializeComponents() {
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
     * Restaura la vista de productos populares.
     */
    private fun restorePopularItems() {
        viewModel.loadPopular().observe(this) { popularItems ->
            itemsAdapter = PopularAdapter(popularItems)
            binding.rvPopulares.adapter = itemsAdapter
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
     * Muestra un diálogo con información de un ítem aleatorio.
     *
     * @param item El ítem a mostrar en el diálogo
     */
    private fun showRandomItemDialog(item: ItemsModel) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_random_item, null)
        val imageView = dialogView.findViewById<ImageView>(R.id.randomItemImage)

        loadItemImage(imageView, item.picUrl.firstOrNull())

        AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
            .show()
    }

    /**
     * Carga la imagen de un ítem en un ImageView.
     *
     * @param imageView Vista donde cargar la imagen
     * @param imageUrl URL de la imagen a cargar
     */
    private fun loadItemImage(imageView: ImageView, imageUrl: String?) {
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.ic_launcher_foreground)
        }
    }

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
     * Muestra u oculta una barra de progreso.
     *
     * @param progressBar Barra de progreso a mostrar/ocultar
     * @param show true para mostrar, false para ocultar
     */
    private fun showProgressBar(progressBar: View, show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
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