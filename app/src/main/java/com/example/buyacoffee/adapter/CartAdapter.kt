package com.example.buyacoffee.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.buyacoffee.Helper.ChangeNumberItemsListener
import com.example.buyacoffee.repositorio.CartRepo
import com.example.buyacoffee.databinding.ViewholderCartBinding
import com.example.buyacoffee.model.ItemsModel

class CartAdapter
    (
    private val listItemSelected: ArrayList<ItemsModel>,
    context: Context,
    var changeNumberIteemsListener: ChangeNumberItemsListener? = null
) : RecyclerView.Adapter<CartAdapter.Viewholder>() {


    class Viewholder(val binding: ViewholderCartBinding) : RecyclerView.ViewHolder(binding.root)

    private val managmentCar: CartRepo = CartRepo(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartAdapter.Viewholder {
        val binding = ViewholderCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return Viewholder(binding)
    }


    override fun onBindViewHolder(holder: CartAdapter.Viewholder, position: Int) {
        val item = listItemSelected[position]
        holder.binding.titleTxt.text = item.title
        holder.binding.feeEachItem.text = "${item.price} €"
        holder.binding.totalEachItem.text = "${Math.round(item.numberInCart * item.price)}€"
        holder.binding.numberItem.text = item.numberInCart.toString()

        Glide.with(holder.itemView.context).load(item.picUrl[0]).apply(
            RequestOptions().transform(
                CenterCrop(), RoundedCorners(20)
            )
        ).into(holder.binding.picCart)

        holder.binding.plusEachItem.setOnClickListener {
            managmentCar.plusItem(listItemSelected, position, object : ChangeNumberItemsListener {
                override fun onChanged() {
                    notifyDataSetChanged()
                    changeNumberIteemsListener?.onChanged()
                }

            })
        }

        holder.binding.minusEachItem.setOnClickListener {
            managmentCar.minusItem(listItemSelected, position, object : ChangeNumberItemsListener {
                override fun onChanged() {
                    notifyDataSetChanged()
                    changeNumberIteemsListener?.onChanged()
                }
            })
        }

        holder.binding.removeItmeBtn.setOnClickListener{
            managmentCar.romveItem(listItemSelected,position,object:ChangeNumberItemsListener{
                override fun onChanged() {
                    notifyDataSetChanged()
                    changeNumberIteemsListener?.onChanged()
                }
            })
        }
    }

    override fun getItemCount(): Int = listItemSelected.size
}