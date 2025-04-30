package com.example.buyacoffee.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.buyacoffee.model.BannerModel
import com.example.buyacoffee.repositorio.MainRepo

class MainViewModel : ViewModel() {
    private val repo = MainRepo()

    fun loadBanner():LiveData<MutableList<BannerModel>>{
        val banners: LiveData<MutableList<BannerModel>> = repo.cargarBanner()
        return banners
    }

}
