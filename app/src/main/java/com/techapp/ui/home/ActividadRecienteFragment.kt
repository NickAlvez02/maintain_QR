package com.techapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.techapp.R
import com.techapp.databinding.FragmentActividadRecienteBinding
import com.techapp.ui.mock.MockMaintainQrData

class ActividadRecienteFragment : Fragment() {

    private var _binding: FragmentActividadRecienteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActividadRecienteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val inflater = LayoutInflater.from(requireContext())
        MockMaintainQrData.getActividadReciente().forEach { actividad ->
            val item = inflater.inflate(R.layout.item_actividad, binding.containerActividad, false)
            item.findViewById<TextView>(R.id.tvActividadTitulo).text = actividad.titulo
            item.findViewById<TextView>(R.id.tvActividadDescripcion).text = actividad.descripcion
            item.findViewById<TextView>(R.id.tvActividadFecha).text = actividad.fecha
            binding.containerActividad.addView(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
