package com.techapp.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.techapp.R
import com.techapp.databinding.FragmentOrdenesEvidenciasBinding
import com.techapp.ui.mock.MockMaintainQrData

class OrdenesEvidenciasFragment : Fragment() {

    private var _binding: FragmentOrdenesEvidenciasBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdenesEvidenciasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val inflater = LayoutInflater.from(requireContext())
        MockMaintainQrData.ordenes.forEach { orden ->
            val historial = MockMaintainQrData.getHistorialTrabajos(orden.id)
            val evidencias = historial.sumOf { it.evidencias.size }
            if (evidencias > 0) {
                val card = inflater.inflate(R.layout.item_qr_historial, binding.containerOrdenesEvidencias, false)
                card.findViewById<TextView>(R.id.tvQrHistEquipo).text =
                    "${orden.equipo?.marca ?: ""} ${orden.equipo?.modelo ?: ""}"
                card.findViewById<TextView>(R.id.tvQrHistSerie).text =
                    "${orden.folio} • $evidencias evidencia(s)"
                card.findViewById<TextView>(R.id.tvQrHistFecha).text =
                    historial.firstOrNull()?.fecha ?: "Sin fecha"
                card.setOnClickListener {
                    findNavController().navigate(R.id.nav_orden_detalle, bundleOf("ordenId" to orden.id))
                }
                binding.containerOrdenesEvidencias.addView(card)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
