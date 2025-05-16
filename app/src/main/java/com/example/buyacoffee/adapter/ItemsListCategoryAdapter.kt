package com.example.buyacoffee.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.buyacoffee.activity.DetailActivity
import com.example.buyacoffee.databinding.ViewholderItemPicLeftBinding
import com.example.buyacoffee.databinding.ViewholderItemPicRigthBinding
import com.example.buyacoffee.model.ItemsModel

class ItemsListCategoryAdapter(val items: MutableList<ItemsModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_ITEM1 = 0
        const val TYPE_ITEM2 = 1

    }

    lateinit var context: Context

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) TYPE_ITEM1 else TYPE_ITEM2
    }

    class ViewHolderITem1(val binding: ViewholderItemPicRigthBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ViewHolderITem2(val binding: ViewholderItemPicLeftBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return when (viewType) {
            TYPE_ITEM1 -> {
                val binding = ViewholderItemPicRigthBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                ViewHolderITem1(binding)
            }

            TYPE_ITEM2 -> {
                val binding = ViewholderItemPicLeftBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                ViewHolderITem2(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")

        }


    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        fun bindCommonData(
            titleTxt: String,
            priceTxt: String,
            rating: Float,
            picUrl: String
        ) {
            when (holder) {
                is ViewHolderITem1 -> {
                    holder.binding.titletxt.text = titleTxt
                    holder.binding.preciotxt.text = priceTxt
                    holder.binding.ratingBarr.rating = rating

                    Glide.with(context)
                        .load(picUrl)
                        .transform(RoundedCorners(20))
                        .into(holder.binding.picMain)

                    holder.itemView.setOnClickListener {
                        val intent = Intent(context, DetailActivity::class.java)
                        intent.putExtra("object", items[position])
                        context.startActivity(intent)

                    }

                }

                is ViewHolderITem2 -> {
                    holder.binding.titletxt.text = titleTxt
                    holder.binding.preciotxt.text = priceTxt
                    holder.binding.ratingBarr.rating = rating

                    Glide.with(context)
                        .load(picUrl)
                        .transform(RoundedCorners(20))
                        .into(holder.binding.picMain)

                    holder.itemView.setOnClickListener {
                        val intent = Intent(context, DetailActivity::class.java)
                        intent.putExtra("object", items[position])
                        context.startActivity(intent)

                    }

                }

            }

        }
        bindCommonData(
            item.title,
            "${item.price} €",
            item.rating.toFloat(),
            item.picUrl[0]
        )
    }

}