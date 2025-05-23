package com.example.buyacoffee.repositorio

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.buyacoffee.model.BannerModel
import com.example.buyacoffee.model.CategoryModel
import com.example.buyacoffee.model.ItemsModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * Repositorio encargado de manejar las operaciones de lectura desde la base de datos de Firebase
 * para las secciones del dashboard de la aplicación como banners, categorías e ítems.
 */
class DashBoardRepo {

    //TODO meter esto en rsc
    private val firebaseDatabase = FirebaseDatabase.getInstance("https://buyacoffe-ea2cb-default-rtdb.europe-west1.firebasedatabase.app")

    /**
     * Recupera en tiempo real la lista de banners desde Firebase.
     *
     * Se conecta a la referencia "Banner" de la base de datos y escucha los cambios usando
     * un [ValueEventListener]. Cada nodo hijo se convierte a un objeto [BannerModel] y se agrega
     * a una lista que es publicada mediante LiveData.
     *
     * @return [LiveData] que emite una lista mutable de [BannerModel] cada vez que hay cambios en los datos.
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

    /**
     * Recupera en tiempo real la lista de categorías desde Firebase.
     *
     * Se conecta a la referencia "Category", convierte los datos en objetos [CategoryModel],
     * y los publica mediante LiveData.
     *
     * @return [LiveData] que emite una lista mutable de [CategoryModel] con los datos actuales.
     */
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

    /**
     * Recupera en tiempo real la lista de ítems populares desde Firebase.
     *
     * Consulta la referencia "Popular" y convierte los datos a [ItemsModel],
     * emitiendo los resultados mediante LiveData.
     *
     * @return [LiveData] con una lista mutable de [ItemsModel] actualizada en tiempo real.
     */
    fun cargarItemsPopulares(): LiveData<MutableList<ItemsModel>> {
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
    /**
     * Recupera la lista de ítems pertenecientes a una categoría específica desde Firebase.
     *
     * Realiza una consulta en la referencia "Items" filtrando por el campo "categoryId"
     * con el valor proporcionado. Los resultados se convierten a [ItemsModel] y se
     * publican mediante LiveData.
     *
     * @param categoryId ID de la categoría para filtrar los ítems.
     * @return [LiveData] con una lista mutable de [ItemsModel] filtrados por categoría.
     */
    fun loadItemByCategory(categoryId: String): LiveData<MutableList<ItemsModel>> {
        val itemsLiveData = MutableLiveData<MutableList<ItemsModel>>()
        val ref = firebaseDatabase.getReference("Items")
        val query: com.google.firebase.database.Query = ref.orderByChild("categoryId").equalTo(categoryId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ItemsModel>()
                for (data in snapshot.children) {
                    val item = data.getValue(ItemsModel::class.java)
                    item?.let { list.add(it) }
                }
                itemsLiveData.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return itemsLiveData
    }
    /**
     * Recupera en tiempo real la lista completa de ítems desde Firebase.
     *
     * Consulta la referencia "Items" y convierte los datos a [ItemsModel],
     * emitiendo los resultados mediante LiveData.
     *
     * @return [LiveData] con una lista mutable de [ItemsModel] actualizada en tiempo real.
     */
    fun loadAllItems(): LiveData<MutableList<ItemsModel>> {
        val listData = MutableLiveData<MutableList<ItemsModel>>()
        val ref = firebaseDatabase.getReference("Items")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ItemsModel>()
                for (childSnapshot in snapshot.children) {
                    childSnapshot.getValue(ItemsModel::class.java)?.let {
                        list.add(it)
                    }
                }
                listData.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el error según sea necesario
                Log.e("DashBoardRepo", "Error loading all items: ${error.message}")
                listData.value = mutableListOf() // Devolver una lista vacía en caso de error
            }
        })
        return listData
    }

}
