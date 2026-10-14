package com.example.buyacoffee.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.buyacoffee.model.BannerModel
import com.example.buyacoffee.model.CategoryModel
import com.example.buyacoffee.model.ItemsModel
import com.example.buyacoffee.repositorio.DashBoardRepo

/**
 * ViewModel que gestiona los datos del dashboard de la aplicación.
 * Se encarga de obtener banners, categorías e ítems (populares o por categoría)
 * desde el repositorio y exponerlos como LiveData para su observación en la interfaz.
 */
class DashViewModel : ViewModel() {
    private val repo = DashBoardRepo()
    private val _allItems = MutableLiveData<MutableList<ItemsModel>>()
    private val _displayedItems = MutableLiveData<MutableList<ItemsModel>>()
    val displayedItems: LiveData<MutableList<ItemsModel>> get() = _displayedItems

    /**
     * Carga la lista de banners desde el repositorio.
     *
     * @return LiveData que contiene una lista mutable de [BannerModel].
     */
    fun loadBanner(): LiveData<MutableList<BannerModel>> {
        return repo.cargarBanner()
    }

    /**
     * Carga la lista de categorías desde el repositorio.
     *
     * @return LiveData que contiene una lista mutable de [CategoryModel].
     */
    fun loadCategory(): LiveData<MutableList<CategoryModel>> {
        return repo.cargarCategorias()
    }

    /**
     * Carga la lista de ítems populares desde el repositorio.
     *
     * @return LiveData que contiene una lista mutable de [ItemsModel].
     */
    fun loadPopular(): LiveData<MutableList<ItemsModel>> {
        return repo.cargarItemsPopulares()
    }

    /**
     * Carga los ítems que pertenecen a una categoría específica.
     *
     * @param categoryId ID de la categoría cuyos ítems se desean obtener.
     * @return LiveData que contiene una lista mutable de [ItemsModel].
     */
    fun loadItemsByCategory(categoryId: String): LiveData<MutableList<ItemsModel>> {
        return repo.loadItemByCategory(categoryId)
    }

    /** Lista completa de ítems; se usa para el buscador y para el explorador. */
    val allItems: LiveData<MutableList<ItemsModel>> get() = _allItems

    /** Texto buscado ahora mismo, o null si el buscador está cerrado. */
    var consultaActual: String? = null
        private set

    private var allItemsSource: LiveData<MutableList<ItemsModel>>? = null
    private val allItemsObserver = Observer<MutableList<ItemsModel>> { items ->
        _allItems.value = items
        // Si había una búsqueda activa, se vuelve a aplicar con los datos nuevos
        consultaActual?.let { filterItems(it) }
    }

    /**
     * Empieza a escuchar la lista completa de ítems. Solo se suscribe una vez,
     * aunque se llame varias veces, y el observador se quita en [onCleared].
     */
    fun loadAllItems() {
        if (allItemsSource != null) return
        allItemsSource = repo.loadAllItems().also { it.observeForever(allItemsObserver) }
    }

    /**
     * Filtra la lista de ítems mostrados basándose en una consulta.
     *
     * @param query Texto de búsqueda.
     */
    fun filterItems(query: String?) {
        consultaActual = query ?: ""
        val currentAllItems = _allItems.value ?: mutableListOf()
        val filteredList = if (query.isNullOrEmpty()) {
            currentAllItems // Si el texto está vacío, muestra la lista completa
        } else {
            currentAllItems.filter {
                // Filtra por título (puedes añadir descripción u otros campos si es necesario)
                it.title.contains(query, ignoreCase = true)
            }.toMutableList()
        }
        _displayedItems.value = filteredList // Actualiza la lista mostrada
    }

    /** Cierra la búsqueda: la lista vuelve a ser la de populares. */
    fun clearSearch() {
        consultaActual = null
    }

    override fun onCleared() {
        allItemsSource?.removeObserver(allItemsObserver)
        super.onCleared()
    }
}
