package com.techapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.techapp.R
import com.techapp.databinding.FragmentCentroAccionesBinding

class CentroAccionesFragment : Fragment() {

    private var _binding: FragmentCentroAccionesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCentroAccionesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cardEscanearQr.setOnClickListener { findNavController().navigate(R.id.nav_qr) }
        binding.cardVerOrdenes.setOnClickListener { findNavController().navigate(R.id.nav_ordenes) }
        binding.cardConsultarEquipos.setOnClickListener {
            findNavController().navigate(R.id.nav_consultar_equipos)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
