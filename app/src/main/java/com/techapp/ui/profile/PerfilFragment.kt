package com.techapp.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.lifecycle.lifecycleScope
import com.techapp.databinding.FragmentPerfilBinding
import com.techapp.ui.login.LoginActivity
import com.techapp.utils.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        lifecycleScope.launch {
            val nombre = sessionManager.tecnicoNombre.first()?.takeIf { it.isNotBlank() }
                ?: "Tecnico MaintainQR"
            val usuario = sessionManager.tecnicoUsuario.first()?.takeIf { it.isNotBlank() }
                ?: "tech.${nombre.lowercase().replace(" ", ".")}"
            val tecnicoId = sessionManager.tecnicoId.first()

            binding.tvNombrePerfil.text = nombre
            binding.tvIniciales.text = initialsFromName(nombre)
            binding.tvRolPerfil.text = "Tecnico de servicio"
            binding.tvUsuarioPerfil.text = tecnicoId?.let { "$usuario • ID $it" } ?: usuario
            binding.tvResumenPerfil.text = "Perfil operativo listo para trabajo en campo"
            binding.tvDetallePerfil.text =
                "Sesión local activa para consultas, historial, evidencias y refacciones."
        }

        binding.btnLogout.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cerrar sesion")
                .setMessage("¿Quieres cerrar la sesion actual en MaintainQR?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Cerrar sesion") { _, _ ->
                    lifecycleScope.launch {
                        sessionManager.clearSession()
                        startActivity(Intent(requireContext(), LoginActivity::class.java))
                        requireActivity().finish()
                    }
                }
                .show()
        }
    }

    private fun initialsFromName(name: String): String {
        return name.trim()
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .mapNotNull { it.firstOrNull()?.toString() }
            .joinToString("")
            .uppercase()
            .ifBlank { "TM" }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
