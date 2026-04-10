package com.techapp.ui.qr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.techapp.R
import com.techapp.databinding.FragmentQrBuscarEquipoBinding
import com.techapp.ui.mock.MockMaintainQrData

class QrBuscarEquipoFragment : Fragment() {

    private var _binding: FragmentQrBuscarEquipoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrBuscarEquipoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        renderResults("")
        binding.etBuscarEquipoQr.doAfterTextChanged { text ->
            renderResults(text?.toString().orEmpty())
        }
    }

    private fun renderResults(query: String) {
        binding.containerBuscarEquipoQr.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())
        MockMaintainQrData.buscarEquipos(query).forEach { orden ->
            val card = inflater.inflate(R.layout.item_qr_historial, binding.containerBuscarEquipoQr, false)
            card.findViewById<TextView>(R.id.tvQrHistEquipo).text =
                "${orden.equipo?.marca ?: ""} ${orden.equipo?.modelo ?: ""}"
            card.findViewById<TextView>(R.id.tvQrHistSerie).text =
                "Serie: ${orden.equipo?.numeroSerie ?: "N/A"} • ${orden.equipo?.cliente?.nombre ?: ""}"
            card.findViewById<TextView>(R.id.tvQrHistFecha).text = "Folio ${orden.folio}"
            card.setOnClickListener {
                MockMaintainQrData.registrarQrConsulta(orden, "Busqueda")
                findNavController().navigate(R.id.nav_orden_detalle, bundleOf("ordenId" to orden.id))
            }
            binding.containerBuscarEquipoQr.addView(card)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
