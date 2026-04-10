package com.techapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.techapp.databinding.FragmentResumenBinding
import com.techapp.ui.mock.MockMaintainQrData

class ResumenFragment : Fragment() {

    private var _binding: FragmentResumenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResumenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ordenes = MockMaintainQrData.ordenes
        binding.tvResumenPendientes.text = ordenes.count { it.estado == "pendiente" }.toString()
        binding.tvResumenProceso.text = ordenes.count {
            it.estado == "en_diagnostico" || it.estado == "en_reparacion"
        }.toString()
        binding.tvResumenDetalle.text =
            "Hoy hay ${MockMaintainQrData.getActividadReciente().size} actividades y ${MockMaintainQrData.getNotificacionesSistema().size} notificaciones registradas en memoria local."
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
