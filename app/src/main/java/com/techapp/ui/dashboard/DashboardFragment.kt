package com.techapp.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.techapp.databinding.FragmentDashboardBinding
import com.techapp.ui.orders.OrdenesAdapter
import com.techapp.utils.Resource
import com.techapp.utils.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var agendaAdapter: OrdenesAdapter
    private lateinit var sessionManager: SessionManager

    private val viewModel: DashboardViewModel by viewModels {
        DashboardViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        setupRecyclerView()
        setupHeaderAccess()
        setupVerTodo()

        observeViewModel()
        viewModel.loadDashboardData()
    }

    private fun observeViewModel() {
        viewModel.dashboardState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Opcional: mostrar un indicador de carga
                }
                is Resource.Success -> {
                    val data = resource.data ?: return@observe
                    binding.countPendientes.text = data.pendientes.toString()
                    binding.countProceso.text = data.enProceso.toString()
                    binding.countFinalizadas.text = data.finalizadas.toString()
                    binding.countEnEspera.text = data.enEspera.toString()
                    binding.countCanceladas.text = data.canceladas.toString()
                    agendaAdapter.submitList(data.agenda)

                    lifecycleScope.launch {
                        val nombre = sessionManager.tecnicoNombre.first() ?: "TÃ©cnico"
                        binding.tvWelcome.text = "Hola, ${nombre.substringBefore(" ")}"
                        binding.tvWelcomeSub.text = "Tienes ${data.enProceso} equipos en proceso y ${data.pendientes} pendientes"
                        binding.tvAvatarDash.text = initialsFromName(nombre)

                        val usuario = sessionManager.tecnicoUsuario.first() ?: "tech"
                        val tecnicoId = sessionManager.tecnicoId.first()
                        binding.tvDashboardMeta.text = tecnicoId?.let { "$usuario â€¢ ID $it" } ?: usuario
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        agendaAdapter = OrdenesAdapter { orden ->
            val action = DashboardFragmentDirections.actionDashboardToOrdenDetalle(orden.id)
            findNavController().navigate(action)
        }
        binding.rvAgenda.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAgenda.adapter = agendaAdapter
    }

    private fun setupHeaderAccess() {
        binding.cardAvatarDash.setOnClickListener {
            findNavController().navigate(com.techapp.R.id.nav_perfil)
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

    private fun setupVerTodo() {
        var expandido = false

        binding.tvClick.setOnClickListener {
            expandido = !expandido

            if (expandido) {
                binding.layoutExtra.visibility = View.VISIBLE
                binding.layoutExtra.alpha = 0f
                binding.layoutExtra.animate().alpha(1f).setDuration(200)
            } else {
                binding.layoutExtra.animate().alpha(0f).setDuration(200).withEndAction {
                    binding.layoutExtra.visibility = View.GONE
                }
            }

            binding.tvClick.text = if (expandido) "Ocultar" else "Ver todo"
        }
    }
}
