package com.techapp.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.techapp.databinding.FragmentConfiguracionBinding

class ConfiguracionFragment : Fragment() {

    private var _binding: FragmentConfiguracionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfiguracionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.switchNotificaciones.isChecked = true
        binding.switchVibracion.isChecked = true
        binding.switchTemaOscuro.isChecked = true
        binding.tvVersionValue.text = "MaintainQR Demo 1.0.0"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
