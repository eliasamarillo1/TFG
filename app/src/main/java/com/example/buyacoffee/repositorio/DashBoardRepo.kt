package com.example.buyacoffee.repositorio

import android.app.DownloadManager.Query
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.buyacoffee.model.BannerModel
import com.example.buyacoffee.model.CategoryModel
import com.example.buyacoffee.model.ItemsModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DashBoardRepo {

    //TODO meter esto en rsc
    private val firebaseDatabase = FirebaseDatabase.getInstance("https://buyacoffe-ea2cb-default-rtdb.europe-west1.firebasedatabase.app")

    /**
     * Recupera en tiempo real la lista de banners desde Firebase.
     * Este metodo se conecta a la referencia `Banners` de la base de datos, escucha los
     * cambios con el [ValueEventListenner], convierte cada nodo hijo a un BannerModel y los guarda en una lista,
     * luego lo publica en un objeto LiveData para que las vistas puedan observarlo.
     *
     * @return un [LiveData] que emite una lista mutable de [BannerModel] cada vez que los datos cambian.
     */

    fun cargarBanner(): LiveData<MutableList<BannerModel>> {

        val listData = MutableLiveData<MutableList<BannerModel>>()
        val ref = firebaseDatabase.getReference("Banner")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<BannerModel>()

                for (data in snapshot.children) {
                    val item = data.getValue(BannerModel::class.java)
                    item?.let { list.add(it) }
                }
                listData.value = list
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return listData
    }

    fun cargarCategorias(): LiveData<MutableList<CategoryModel>> {

        val listData = MutableLiveData<MutableList<CategoryModel>>()
        val ref = firebaseDatabase.getReference("Category")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<CategoryModel>()

                for (data in snapshot.children) {
                    val item = data.getValue(CategoryModel::class.java)
                    item?.let { list.add(it) }
                }
                listData.value = list
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return listData
    }

    fun cargarItems(): LiveData<MutableList<ItemsModel>> {

        val listData = MutableLiveData<MutableList<ItemsModel>>()
        val ref = firebaseDatabase.getReference("Popular")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ItemsModel>()

                for (data in snapshot.children) {
                    val item = data.getValue(ItemsModel::class.java)
                    item?.let { list.add(it) }
                }
                listData.value = list
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return listData
    }

    fun loadItemByCategory(categoryId: String):LiveData<MutableList<ItemsModel>>{
        val itemsLiveData = MutableLiveData<MutableList<ItemsModel>>()
        val ref = firebaseDatabase.getReference("Items")
        val query:com.google.firebase.database.Query = ref.orderByChild("categoryId").equalTo(categoryId)

        query.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ItemsModel>()

                for (data in snapshot.children) {
                    val item = data.getValue(ItemsModel::class.java)
                    item?.let { list.add(it) }
                }
                itemsLiveData.value = list            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })
        return itemsLiveData

    }

}
