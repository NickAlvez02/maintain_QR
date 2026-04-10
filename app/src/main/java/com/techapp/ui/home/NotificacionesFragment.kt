package com.techapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.techapp.R
import com.techapp.databinding.FragmentNotificacionesBinding
import com.techapp.ui.mock.MockMaintainQrData

class NotificacionesFragment : Fragment() {

    private var _binding: FragmentNotificacionesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificacionesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val inflater = LayoutInflater.from(requireContext())
        MockMaintainQrData.getNotificacionesSistema().forEach { notificacion ->
            val item = inflater.inflate(R.layout.item_notificacion, binding.containerNotificaciones, false)
            item.findViewById<TextView>(R.id.tvNotificacionTipo).text = notificacion.tipo.uppercase()
            item.findViewById<TextView>(R.id.tvNotificacionTitulo).text = notificacion.titulo
            item.findViewById<TextView>(R.id.tvNotificacionMensaje).text = notificacion.mensaje
            item.findViewById<TextView>(R.id.tvNotificacionFecha).text = notificacion.fecha
            binding.containerNotificaciones.addView(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
