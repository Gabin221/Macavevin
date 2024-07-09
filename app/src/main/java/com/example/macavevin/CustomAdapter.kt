package com.example.macavevin

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.macavevin.databinding.CardViewDesignBinding

class CustomAdapter(private var mList: List<VinsViewModel>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    fun updateData(newList: List<VinsViewModel>) {
        mList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardViewDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]
        holder.binding.nomTextView.text = item.nom
        holder.binding.anneeTextView.text = item.annee
        holder.binding.quantiteTextView.text = item.quantite
        holder.binding.regionTextView.text = item.region
        holder.binding.zoneTextView.text = item.zone
        holder.binding.categorieTextView.text = item.categorie

        val textCalendrier: TextView = holder.binding.calendrierTexte
        val text = " "
        val spannableString = SpannableString("$text")
        val drawableStart = ContextCompat.getDrawable(holder.binding.root.context, R.drawable.ic_calendrier)
        drawableStart?.setBounds(0, 0, drawableStart.intrinsicWidth, drawableStart.intrinsicHeight)
        val imageSpanStart = ImageSpan(drawableStart!!, ImageSpan.ALIGN_BOTTOM)
        spannableString.setSpan(imageSpanStart, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textCalendrier.text = spannableString
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(val binding: CardViewDesignBinding) : RecyclerView.ViewHolder(binding.root)
}
