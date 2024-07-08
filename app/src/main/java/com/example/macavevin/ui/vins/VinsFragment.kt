package com.example.macavevin.ui.vins

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.macavevin.CustomAdapter
import com.example.macavevin.R
import com.example.macavevin.VinsAdapter
import com.example.macavevin.VinsViewModel
import com.example.macavevin.databinding.FragmentVinsBinding
import com.example.macavevin.model.Vins
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class VinsFragment : Fragment() {

    private var _binding: FragmentVinsBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerViewBlanc: RecyclerView
    private lateinit var recyclerViewRose: RecyclerView
    private lateinit var recyclerViewRouge: RecyclerView

    private lateinit var adapterBlanc: VinsAdapter<Vins>
    private lateinit var adapterRose: VinsAdapter<Vins>
    private lateinit var adapterRouge: VinsAdapter<Vins>

    private lateinit var titreBlanc: TextView
    private lateinit var titreRose: TextView
    private lateinit var titreRouge: TextView

    lateinit var programmingLanguagesLV: ListView
    lateinit var listAdapter: ArrayAdapter<String>
    lateinit var programmingLanguagesList: ArrayList<String>
    lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVinsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        FirebaseApp.initializeApp(requireActivity())

        try {
            recyclerViewBlanc = root.findViewById(R.id.recyclerViewBlanc)
            recyclerViewBlanc.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewRose = root.findViewById(R.id.recyclerViewRose)
            recyclerViewRose.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewRouge = root.findViewById(R.id.recyclerViewRouge)
            recyclerViewRouge.layoutManager = LinearLayoutManager(requireContext())

            titreBlanc = root.findViewById(R.id.titreBlanc)
            titreRose = root.findViewById(R.id.titreRose)
            titreRouge = root.findViewById(R.id.titreRouge)

            adapterBlanc = VinsAdapter(emptyList()) {}
            adapterRose = VinsAdapter(emptyList()) {}
            adapterRouge = VinsAdapter(emptyList()) {}

            recyclerViewBlanc.adapter = adapterBlanc
            recyclerViewRose.adapter = adapterRose
            recyclerViewRouge.adapter = adapterRouge

            chargerVins()

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Erreur lors de la récupération des données Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        return root
    }

    private fun chargerVins() {
        val db = FirebaseFirestore.getInstance()
        val types = listOf("Blanc", "Rosé", "Rouge")

        for (genre in types) {
            val collection = db.collection("Vins").document("Genre").collection(genre)
            collection.get().addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val data = ArrayList<VinsViewModel>()
                    for (document in documents) {
                        val nom = document.getString("nom") ?: ""
                        val annee = document.getString("annee") ?: ""
                        val quantite = document.getString("quantite") ?: ""
                        val region = document.getString("region") ?: ""
                        val zone = document.getString("zone") ?: ""
                        val categorie = genre
                        val element = VinsViewModel(annee, nom, quantite, region, zone, categorie)

                        data.add(element)
                    }
                    val adapter = CustomAdapter(data)

                    when (genre) {
                        "Blanc" -> recyclerViewBlanc.adapter = adapter
                        "Rosé" -> recyclerViewRose.adapter = adapter
                        "Rouge" -> recyclerViewRouge.adapter = adapter
                    }
                    affichageTitres(genre)
                }
            }.addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Erreur lors du chargement des vins de la catégorie $genre : $e", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun affichageTitres(input: String) {
        when (input) {
            "Blanc" -> titreBlanc.visibility = VISIBLE
            "Rosé" -> titreRose.visibility = VISIBLE
            "Rouge" -> titreRouge.visibility = VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}