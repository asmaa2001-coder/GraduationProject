package com.example.traningforproject

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lazyeye.R


class PageAdapter(private val listOfData: List<Data> , val context: Context , private val resource:Int) :
    RecyclerView.Adapter<PageAdapter.PageViewHolderAdapter>() {

    inner class PageViewHolderAdapter(itemView: View) : RecyclerView.ViewHolder(
        itemView
    ) {
        val exDetails: TextView = itemView.findViewById(R.id.exer_guide)
        val imageDetail: ImageView = itemView.findViewById(R.id.exer_image)

    }

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): PageViewHolderAdapter {
        return PageViewHolderAdapter(
            LayoutInflater.from(parent.context).inflate(resource , parent , false)
        )
    }

    override fun getItemCount()=listOfData.size

    override fun onBindViewHolder(holder: PageViewHolderAdapter , position: Int) {
        val item = listOfData[position]
        holder.exDetails.text=item.des
        Glide.with(context)
                .load(Uri.parse("file:///android_asset/${item.image}"))
                .into(holder.imageDetail)       // holder.image_detail.setImageResource(item.image)

    }
}