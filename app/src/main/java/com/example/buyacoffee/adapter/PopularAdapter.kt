package com.example.buyacoffee.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.buyacoffee.activity.DetailActivity
import com.example.buyacoffee.model.ItemsModel
import com.example.buyacoffee.databinding.ViewholderPopularBinding

class PopularAdapter(var items: List<ItemsModel>) :
    RecyclerView.Adapter<PopularAdapter.ViewHolder>() {
    lateinit var context: Context

    class ViewHolder(val binding: ViewholderPopularBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding = ViewholderPopularBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.titleTxt.text = items[position].title
        holder.binding.pricetxt.text = "$" + items[position].price.toString()

        Glide.with(context).load(items[position].picUrl[0]).into(holder.binding.pic)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("object", items[position])
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = items.size

    fun updateList(newList: MutableList<ItemsModel>) {
        items = newList
        notifyDataSetChanged() // Notifica al RecyclerView que los datos han cambiado
    }

}