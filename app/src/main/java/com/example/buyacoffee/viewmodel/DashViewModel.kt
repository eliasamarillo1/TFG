package com.example.buyacoffee.viewmodel

import androidx.lifecycle.LiveData
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

}
