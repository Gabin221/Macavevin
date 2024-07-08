package com.example.macavevin

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(private val mList: List<VinsViewModel>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_design, parent, false)

        return ViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]

        holder.nomTextView.text = ItemsViewModel.nom
        holder.regionTextView.text = ItemsViewModel.region
        holder.quantiteTextView.text = ItemsViewModel.quantite
        holder.anneeTextView.text = ItemsViewModel.annee
        holder.zoneTextView.text = ItemsViewModel.zone
        holder.categorieTextView.text = ItemsViewModel.categorie

        val textCalendrier: TextView = holder.calendrierTexte
        val text = " "
        val spannableString = SpannableString("$text")
        val drawableStart = ContextCompat.getDrawable(holder.context, R.drawable.ic_calendrier)
        drawableStart?.setBounds(0, 0, drawableStart.intrinsicWidth, drawableStart.intrinsicHeight)
        val imageSpanStart = ImageSpan(drawableStart!!, ImageSpan.ALIGN_BOTTOM)
        spannableString.setSpan(imageSpanStart, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textCalendrier.text = spannableString
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View, val context: Context) : RecyclerView.ViewHolder(ItemView) {
        val nomTextView: TextView = itemView.findViewById(R.id.nomTextView)
        val regionTextView: TextView = itemView.findViewById(R.id.regionTextView)
        val quantiteTextView: TextView = itemView.findViewById(R.id.quantiteTextView)
        val anneeTextView: TextView = itemView.findViewById(R.id.anneeTextView)
        val zoneTextView: TextView = itemView.findViewById(R.id.zoneTextView)
        val categorieTextView: TextView = itemView.findViewById(R.id.categorieTextView)
        val calendrierTexte: TextView = itemView.findViewById(R.id.calendrierTexte)
    }
}
