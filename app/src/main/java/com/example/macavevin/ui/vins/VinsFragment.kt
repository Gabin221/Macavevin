package com.example.macavevin.ui.vins

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.macavevin.R
import com.example.macavevin.databinding.FragmentVinsBinding
import com.google.firebase.FirebaseApp

class VinsFragment : Fragment() {

    private var _binding: FragmentVinsBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var calendrierTexte: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVinsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        FirebaseApp.initializeApp(requireActivity())

        try {
            recyclerView = root.findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            calendrierTexte = root.findViewById(R.id.calendrierTexte)

            calendrier()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Erreur lors de la récupération des données Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        return root
    }

    private fun calendrier() {
        val text = ""

        val spannableString = SpannableString(" $text ")

        val drawableStart = ContextCompat.getDrawable(requireContext(), R.drawable.ic_calendrier)
        drawableStart?.setBounds(0, 0, drawableStart.intrinsicWidth, drawableStart.intrinsicHeight)
        val imageSpanStart = ImageSpan(drawableStart!!, ImageSpan.ALIGN_BOTTOM)
        spannableString.setSpan(imageSpanStart, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        calendrierTexte.text = spannableString
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}