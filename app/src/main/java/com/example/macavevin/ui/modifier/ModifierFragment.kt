package com.example.macavevin.ui.modifier

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.macavevin.R
import com.example.macavevin.databinding.FragmentModifierBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class ModifierFragment : Fragment() {

    private var _binding: FragmentModifierBinding? = null
    private val binding get() = _binding!!

    private lateinit var editTextNom: EditText
    private lateinit var editTextType: EditText
    private lateinit var editTextRegion: EditText
    private lateinit var editTextAnnee: EditText
    private lateinit var editTextZone: EditText
    private lateinit var editTextQuantite: EditText
    private lateinit var buttonAjouter: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModifierBinding.inflate(inflater, container, false)
        val root: View = binding.root

        FirebaseApp.initializeApp(requireActivity())

        editTextNom = root.findViewById(R.id.editTextNom)
        editTextType = root.findViewById(R.id.editTextType)
        editTextRegion = root.findViewById(R.id.editTextRegion)
        editTextAnnee = root.findViewById(R.id.editTextAnnee)
        editTextZone = root.findViewById(R.id.editTextZone)
        editTextQuantite = root.findViewById(R.id.editTextQuantite)
        buttonAjouter = root.findViewById(R.id.buttonAjouter)

        buttonAjouter.setOnClickListener {
            ajouterOuModifierVin()
        }

        return root
    }

    private fun ajouterOuModifierVin() {
        val nom = editTextNom.text.toString()
        val type = editTextType.text.toString()
        val region = editTextRegion.text.toString()
        val annee = editTextAnnee.text.toString()
        val zone = editTextZone.text.toString()
        val quantite = editTextQuantite.text.toString().toIntOrNull() ?: 1

        if (nom.isBlank() || type.isBlank() || region.isBlank() || annee.isBlank() || zone.isBlank() || quantite <= 0) {
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
                        "nom" to nom,
                        "type" to type,
                        "region" to region,
                        "annee" to annee,
                        "zone" to zone,
                        "quantite" to quantite.toString()
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
        editTextType.text.clear()
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
