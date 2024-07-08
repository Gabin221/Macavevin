package com.example.macavevin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.macavevin.model.Vins

class VinsAdapter<T>(private var articles: List<T>, private val onItemClick: (T) -> Unit) :
    RecyclerView.Adapter<VinsAdapter<T>.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomTextView: TextView = itemView.findViewById(R.id.nomTextView)

        init {
            itemView.setOnClickListener {
                onItemClick(articles[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vins, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position] as Vins
        holder.nomTextView.text = article.nom
    }

    override fun getItemCount(): Int = articles.size

    fun updateData(newData: List<T>) {
        articles = newData
        notifyDataSetChanged()
    }
}