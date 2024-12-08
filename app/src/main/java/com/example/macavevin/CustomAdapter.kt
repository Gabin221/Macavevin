package com.example.macavevin

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.macavevin.databinding.CardViewDesignBinding
import com.google.firebase.firestore.FirebaseFirestore

class CustomAdapter(private var mList: List<VinsViewModel>,
                    private val contexte: Context
) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

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

        holder.binding.deleteButton.setOnClickListener {
            afficherConfirmationSuppression(item.nom, item.annee, item.categorie)
        }
    }

    private fun afficherConfirmationSuppression(nom: String, annee: String, categorie: String) {
        val builder = AlertDialog.Builder(contexte)
        builder.setTitle("Confirmation")
        builder.setMessage("Êtes-vous sûr de vouloir supprimer une bouteille de $nom datant de $annee ?")
        builder.setPositiveButton("Oui") { dialog, _ ->
            supprimerVin(nom, annee, categorie)
            dialog.dismiss()
        }
        builder.setNegativeButton("Non") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun supprimerVin(nom: String, annee: String, categorie: String) {
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("Vins").document("Genre").collection(categorie)
        collection.whereEqualTo("nom", nom)
            .whereEqualTo("annee", annee)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val currentQuantite = document.getString("quantite")?.toIntOrNull() ?: 0
                    val newQuantite = currentQuantite - 1

                    collection.document(document.id).update("quantite", newQuantite.toString())
                        .addOnSuccessListener {
                            Toast.makeText(contexte, "Quantité mise à jour avec succès", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(contexte, "Erreur lors de la mise à jour de la quantité: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
        }.addOnFailureListener { e ->
            Toast.makeText(contexte, "Erreur lors de la recherche de la bouteille à supprimer : $e", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(val binding: CardViewDesignBinding) : RecyclerView.ViewHolder(binding.root)
}
