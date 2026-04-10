package com.techapp.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.techapp.databinding.FragmentOrdenesBinding
import com.techapp.utils.Resource

class OrdenesFragment : Fragment() {

    private var _binding: FragmentOrdenesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrdenesViewModel by viewModels {
        OrdenesViewModelFactory(requireContext())
    }

    private lateinit var adapter: OrdenesAdapter
    private var filtro: String = "todas"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filtro = arguments?.getString("filtro") ?: "todas"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdenesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
        setupSearch()
        setupSwipeRefresh()
        observeViewModel()
        viewModel.loadOrdenes()
    }

    private fun observeViewModel() {
        viewModel.ordenesState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.swipeRefresh.isRefreshing = true
                }
                is Resource.Success -> {
                    binding.swipeRefresh.isRefreshing = false
                    val allOrders = resource.data ?: emptyList()
                    val filteredOrders = when (filtro) {
                        "pendientes" -> allOrders.filter { it.estado.lowercase() == "pendiente" }
                        "en_proceso" -> allOrders.filter {
                            it.estado.lowercase() == "en_diagnostico" ||
                                    it.estado.lowercase() == "en_reparacion"
                        }
                        "finalizadas" -> allOrders.filter { it.estado.lowercase() == "finalizado" }
                        "diagnostico" -> allOrders.filter { it.estado.lowercase() == "en_diagnostico" }
                        "reparacion" -> allOrders.filter { it.estado.lowercase() == "en_reparacion" }
                        else -> allOrders
                    }
                    adapter.submitList(filteredOrders)
                    binding.tvEmpty.visibility = if (filteredOrders.isEmpty()) View.VISIBLE else View.GONE
                    binding.tvContador.text = "${getTituloSegunFiltro(filtro)} â€¢ ${filteredOrders.size} orden(es)"
                }
                is Resource.Error -> {
                    binding.swipeRefresh.isRefreshing = false
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupRecycler() {
        adapter = OrdenesAdapter { orden ->
            val action = OrdenesFragmentDirections
                .actionOrdenesFragmentToOrdenDetalleFragment(orden.id)
            findNavController().navigate(action)
        }

        binding.rvOrdenes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrdenes.adapter = adapter
    }

    private fun setupSearch() {
        binding.etBuscar.addTextChangedListener { texto ->
            adapter.filter(texto.toString())
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadOrdenes()
        }
    }

    private fun getTituloSegunFiltro(filtro: String): String {
        return when (filtro) {
            "pendientes" -> "Ã“rdenes pendientes"
            "en_proceso" -> "Ã“rdenes en proceso"
            "finalizadas" -> "Ã“rdenes finalizadas"
            "diagnostico" -> "Ã“rdenes en diagnÃ³stico"
            "reparacion" -> "Ã“rdenes en reparaciÃ³n"
            else -> "Todas las Ã³rdenes"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
