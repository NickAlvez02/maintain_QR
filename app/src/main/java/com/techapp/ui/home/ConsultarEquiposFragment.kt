package com.techapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.techapp.ui.mock.MockMaintainQrData
import com.techapp.databinding.FragmentConsultarEquiposBinding

class ConsultarEquiposFragment : Fragment() {

    private var _binding: FragmentConsultarEquiposBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConsultarEquiposBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val inflater = LayoutInflater.from(requireContext())
        MockMaintainQrData.ordenes.forEach { orden ->
            val card = MaterialCardView(requireContext()).apply {
                radius = 18f
                setCardBackgroundColor(resources.getColor(com.techapp.R.color.surface, null))
                val padding = (16 * resources.displayMetrics.density).toInt()
                setContentPadding(padding, padding, padding, padding)
                useCompatPadding = true
            }
            val text = TextView(requireContext()).apply {
                val equipo = orden.equipo
                val cliente = equipo?.cliente
                text = "${equipo?.tipo ?: "Equipo"} • ${equipo?.marca ?: "Marca"}\nSerie: ${equipo?.numeroSerie ?: "N/A"}\nCliente: ${cliente?.nombre ?: "Sin cliente"}"
                setTextColor(resources.getColor(com.techapp.R.color.text_primary, null))
                textSize = 14f
            }
            card.addView(text)
            val params = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = (12 * resources.displayMetrics.density).toInt() }
            binding.containerEquipos.addView(card, params)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
