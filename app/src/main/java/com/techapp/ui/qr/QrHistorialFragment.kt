package com.techapp.ui.qr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.techapp.R
import com.techapp.databinding.FragmentQrHistorialBinding
import com.techapp.ui.mock.MockMaintainQrData

class QrHistorialFragment : Fragment() {

    private var _binding: FragmentQrHistorialBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrHistorialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val inflater = LayoutInflater.from(requireContext())
        MockMaintainQrData.getHistorialQr().forEach { item ->
            val card = inflater.inflate(R.layout.item_qr_historial, binding.containerQrHistorial, false)
            card.findViewById<TextView>(R.id.tvQrHistEquipo).text = item.equipo
            card.findViewById<TextView>(R.id.tvQrHistSerie).text = "Serie: ${item.serie} â€¢ ${item.origen}"
            card.findViewById<TextView>(R.id.tvQrHistFecha).text = item.fecha
            card.setOnClickListener {
                findNavController().navigate(R.id.nav_orden_detalle, bundleOf("ordenId" to item.ordenId))
            }
            binding.containerQrHistorial.addView(card)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
