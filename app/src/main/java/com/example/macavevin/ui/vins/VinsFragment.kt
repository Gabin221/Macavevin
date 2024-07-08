package com.example.macavevin.ui.vins

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.macavevin.databinding.FragmentVinsBinding
import com.google.firebase.FirebaseApp

class VinsFragment : Fragment() {

    private var _binding: FragmentVinsBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVinsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        FirebaseApp.initializeApp(requireActivity())

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}