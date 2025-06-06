package com.example.buyacoffee.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.buyacoffee.R
import com.example.buyacoffee.activity.CartActivity
import com.example.buyacoffee.databinding.ViewholderCartBinding
import com.example.buyacoffee.model.ItemsModel

/**
 * Adapter para el RecyclerView del carrito de compras.
 * Actualizado para trabajar con el patrón MVVM y mejorar la carga de imágenes.
 */
class CartAdapter(
    private var items: ArrayList<ItemsModel>,
    private val context: Context,
    private val listener: CartActivity.CartAdapterListener
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    companion object {
        private const val TAG = "CartAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ViewholderCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size

    /**
     * Actualiza la lista de items del adapter
     */
    fun updateItems(newItems: ArrayList<ItemsModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class CartViewHolder(private val binding: ViewholderCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemsModel, position: Int) {
            binding.apply {
                // Configurar datos del item
                titleTxt.text = item.title
                feeEachItem.text = "${item.price} €"
                totalEachItem.text = "${Math.round((item.numberInCart * item.price) * 100) / 100.0} €"
                numberItem.text = item.numberInCart.toString()

                // Log para debug
                Log.d(TAG, "Cargando imagen para ${item.title}: ${item.picUrl}")

                // Cargar imagen con configuración mejorada de Glide
                loadImage(item.picUrl[0])

                // Configurar botones
                plusEachItem.setOnClickListener {
                    listener.onIncreaseQuantity(position)
                }

                minusEachItem.setOnClickListener {
                    listener.onDecreaseQuantity(position)
                }

                removeItmeBtn?.setOnClickListener {
                    listener.onRemoveItem(position)
                }
            }
        }

        /**
         * Carga la imagen usando Glide con configuración robusta
         */
        private fun loadImage(imageUrl: String?) {
            // Verificar que la URL no sea null o vacía
            if (imageUrl.isNullOrEmpty()) {
                Log.w(TAG, "URL de imagen vacía o nula")
                // Cargar imagen por defecto
                Glide.with(context)
                    .load(R.drawable.cerveza) // Asegúrate de tener esta imagen
                    .into(binding.picCart)
                return
            }
            Glide.with(context)
                .load(imageUrl)
                .centerCrop() // Ajustar imagen
                .into(binding.picCart)
        }
    }
}