package com.example.buyacoffee.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.buyacoffee.model.BannerModel
import com.example.buyacoffee.model.CategoryModel
import com.example.buyacoffee.model.ItemsModel
import com.example.buyacoffee.repositorio.DashBoardRepo

class DashViewModel : ViewModel() {
    private val repo = DashBoardRepo()

    fun loadBanner():LiveData<MutableList<BannerModel>>{
        return repo.cargarBanner()
    }

    fun loadCategory():LiveData<MutableList<CategoryModel>>{
        return repo.cargarCategorias()
    }

    fun loadPopular():LiveData<MutableList<ItemsModel>>{
        return repo.cargarItems()
    }

    fun loadItems(categoryId:String):LiveData<MutableList<ItemsModel>>{
        return repo.loadItemByCategory(categoryId)
    }
}
