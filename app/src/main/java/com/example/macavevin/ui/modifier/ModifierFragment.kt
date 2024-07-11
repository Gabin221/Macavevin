package com.example.macavevin.ui.modifier

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.SearchView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.macavevin.Quadruple
import com.example.macavevin.R
import com.example.macavevin.VinsViewModel
import com.example.macavevin.databinding.FragmentModifierBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class ModifierFragment : Fragment() {

    private var _binding: FragmentModifierBinding? = null
    private val binding get() = _binding!!

    private lateinit var editTextNom: EditText
    private lateinit var spinnerType: Spinner
    private lateinit var editTextRegion: EditText
    private lateinit var editTextAnnee: EditText
    private lateinit var editTextZone: EditText
    private lateinit var editTextQuantite: EditText
    private lateinit var buttonAjouter: Button

    private lateinit var idLV: ListView
    private lateinit var idSV: SearchView

    private val categories = listOf("Blanc", "Rosé", "Rouge")

    lateinit var typeList: ArrayList<Pair<String, String>>
    lateinit var listAdapter: ArrayAdapter<Pair<String, String>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModifierBinding.inflate(inflater, container, false)
        val root: View = binding.root

        FirebaseApp.initializeApp(requireActivity())

        editTextNom = root.findViewById(R.id.editTextNom)
        spinnerType = root.findViewById(R.id.spinnerType)
        editTextRegion = root.findViewById(R.id.editTextRegion)
        editTextAnnee = root.findViewById(R.id.editTextAnnee)
        editTextZone = root.findViewById(R.id.editTextZone)
        editTextQuantite = root.findViewById(R.id.editTextQuantite)
        buttonAjouter = root.findViewById(R.id.buttonAjouter)
        idLV = root.findViewById(R.id.idLV)
        idSV = root.findViewById(R.id.idSV)

        val listeVinsFiltree = mutableListOf<Pair<String, String>>()

        val spinnerAdapterType = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listOf("Type de vin", "Blanc", "Rosé", "Rouge"))
        spinnerAdapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = spinnerAdapterType

        buttonAjouter.setOnClickListener {
            ajouterOuModifierVin()
        }

        typeList = ArrayList()

        chargerVins()

        idSV.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                listeVinsFiltree.clear()
                if (newText != null && newText.isNotEmpty()) {
                    for (film in typeList) {
                        if (film.first.contains(newText, ignoreCase = true)) {
                            listeVinsFiltree.add(film)
                        }
                    }
                    idLV.visibility = if (listeVinsFiltree.isEmpty()) View.GONE else View.VISIBLE
                    // setListViewHeightBasedOnChildren(idLV)
                } else {
                    idLV.visibility = View.GONE
                }
                listAdapter.notifyDataSetChanged()
                return false
            }
        })

        return root
    }

    private fun chargerVins() {
        val db = FirebaseFirestore.getInstance()

        for (genre in categories) {
            val collection = db.collection("Vins").document("Genre").collection(genre)
            collection.get().addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        val annee = document.getString("annee") ?: ""
                        val nom = document.getString("nom") ?: ""
                        val quantite = document.getString("quantite") ?: ""
                        val region = document.getString("region") ?: ""
                        val zone = document.getString("zone") ?: ""
                        val element = VinsViewModel(annee, nom, quantite, region, zone, genre)
                        val displayString = element.nom
                        typeList.add(Pair(displayString, genre))
                    }
                }
            }.addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Erreur lors du chargement des films de la catégorie $genre : $e", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun supprimerFilm(title: String, category: String) {
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("Vins").document("Genre").collection(category)
        collection.whereEqualTo("titre", title).get().addOnSuccessListener { documents ->
            for (document in documents) {
                collection.document(document.id).delete().addOnSuccessListener {
                    Toast.makeText(requireContext(), "Vin supprimé avec succès", Toast.LENGTH_SHORT).show()
                    typeList.removeIf { it.first.contains(title) && it.second == category }
                    listAdapter.notifyDataSetChanged()
                    idSV.setQuery("", false)
                }.addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Erreur lors de la suppression du film : $e", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Erreur lors de la recherche du film à supprimer : $e", Toast.LENGTH_SHORT).show()
        }
    }

    private fun ajouterOuModifierVin() {
        val nom = editTextNom.text.toString()
        val type = spinnerType.selectedItem.toString()
        val region = editTextRegion.text.toString()
        val annee = editTextAnnee.text.toString()
        val zone = editTextZone.text.toString()
        val quantite = editTextQuantite.text.toString().toIntOrNull() ?: 1

        if (nom.isBlank() || type.equals("Type") || region.isBlank() || annee.isBlank() || zone.isBlank() || quantite <= 0) {
            Toast.makeText(requireContext(), "Tous les champs doivent être remplis", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("Vins").document("Genre").collection(type)

        collectionRef
            .whereEqualTo("nom", nom)
            .whereEqualTo("region", region)
            .whereEqualTo("annee", annee)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // Ajouter un nouveau document
                    val newVin = hashMapOf(
                        "annee" to annee,
                        "nom" to nom,
                        "quantite" to quantite.toString(),
                        "region" to region,
                        "zone" to zone
                    )

                    collectionRef.add(newVin)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Vin ajouté avec succès", Toast.LENGTH_SHORT).show()
                            clearFields()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Erreur lors de l'ajout du vin: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Mettre à jour le document existant
                    for (document in documents) {
                        val currentQuantite = document.getString("quantite")?.toIntOrNull() ?: 0
                        val newQuantite = currentQuantite + quantite

                        collectionRef.document(document.id).update("quantite", newQuantite.toString())
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "Quantité mise à jour avec succès", Toast.LENGTH_SHORT).show()
                                clearFields()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(requireContext(), "Erreur lors de la mise à jour de la quantité: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Erreur lors de la vérification de l'existence du vin: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        editTextNom.text.clear()
        spinnerType.setSelection(0)
        editTextRegion.text.clear()
        editTextAnnee.text.clear()
        editTextZone.text.clear()
        editTextQuantite.setText("1")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
