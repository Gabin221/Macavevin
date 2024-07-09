package com.example.macavevin.ui.vins

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.macavevin.CustomAdapter
import com.example.macavevin.R
import com.example.macavevin.VinsViewModel
import com.example.macavevin.databinding.FragmentVinsBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class VinsFragment : Fragment() {

    private var _binding: FragmentVinsBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerViewBlanc: RecyclerView
    private lateinit var recyclerViewRose: RecyclerView
    private lateinit var recyclerViewRouge: RecyclerView

    private lateinit var adapterBlanc: CustomAdapter
    private lateinit var adapterRose: CustomAdapter
    private lateinit var adapterRouge: CustomAdapter

    private lateinit var titreBlanc: TextView
    private lateinit var titreRose: TextView
    private lateinit var titreRouge: TextView

    private lateinit var viewBlancRose: View
    private lateinit var viewRoseRouge: View

    private var originalDataBlanc = listOf<VinsViewModel>()
    private var originalDataRose = listOf<VinsViewModel>()
    private var originalDataRouge = listOf<VinsViewModel>()

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

            viewBlancRose = root.findViewById(R.id.viewBlancRose)
            viewRoseRouge = root.findViewById(R.id.viewRoseRouge)

            adapterBlanc = CustomAdapter(emptyList())
            adapterRose = CustomAdapter(emptyList())
            adapterRouge = CustomAdapter(emptyList())

            recyclerViewBlanc.adapter = adapterBlanc
            recyclerViewRose.adapter = adapterRose
            recyclerViewRouge.adapter = adapterRouge

            chargerVins()

            val searchView: SearchView = root.findViewById(R.id.idSV)
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    // Do nothing on submit
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    filter(newText ?: "")
                    return false
                }
            })

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Erreur lors de la récupération des données Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        return root
    }

    private fun filter(query: String) {
        val filteredBlanc = originalDataBlanc.filter { it.nom.contains(query, ignoreCase = true) || it.annee.contains(query, ignoreCase = true) }
        val filteredRose = originalDataRose.filter { it.nom.contains(query, ignoreCase = true) || it.annee.contains(query, ignoreCase = true) }
        val filteredRouge = originalDataRouge.filter { it.nom.contains(query, ignoreCase = true) || it.annee.contains(query, ignoreCase = true) }

        adapterBlanc.updateData(filteredBlanc)
        adapterRose.updateData(filteredRose)
        adapterRouge.updateData(filteredRouge)

        updateTitleVisibility(filteredBlanc, filteredRose, filteredRouge)
    }

    private fun updateTitleVisibility(filteredBlanc: List<VinsViewModel>, filteredRose: List<VinsViewModel>, filteredRouge: List<VinsViewModel>) {
        titreBlanc.visibility = if (filteredBlanc.isNotEmpty()) View.VISIBLE else View.GONE
        titreRose.visibility = if (filteredRose.isNotEmpty()) View.VISIBLE else View.GONE
        titreRouge.visibility = if (filteredRouge.isNotEmpty()) View.VISIBLE else View.GONE

        viewBlancRose.visibility = if (filteredBlanc.isNotEmpty()) View.VISIBLE else View.GONE
        viewRoseRouge.visibility = if (filteredRose.isNotEmpty()) View.VISIBLE else View.GONE
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

                    when (genre) {
                        "Blanc" -> {
                            originalDataBlanc = data
                            adapterBlanc.updateData(originalDataBlanc)
                        }
                        "Rosé" -> {
                            originalDataRose = data
                            adapterRose.updateData(originalDataRose)
                        }
                        "Rouge" -> {
                            originalDataRouge = data
                            adapterRouge.updateData(originalDataRouge)
                        }
                    }
                    affichageTitres(genre)
                }
            }.addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Erreur lors du chargement des vins de la catégorie $genre : $e", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun affichageTitres(genre: String) {
        when (genre) {
            "Blanc" -> titreBlanc.visibility = if (originalDataBlanc.isNotEmpty()) View.VISIBLE else View.GONE
            "Rosé" -> titreRose.visibility = if (originalDataRose.isNotEmpty()) View.VISIBLE else View.GONE
            "Rouge" -> titreRouge.visibility = if (originalDataRouge.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
