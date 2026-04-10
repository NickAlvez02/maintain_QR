package com.techapp.ui.parts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.techapp.databinding.FragmentRefaccionesBinding
import com.techapp.models.Refaccion
import com.techapp.ui.mock.MockMaintainQrData
import com.techapp.utils.Resource

class RefaccionesFragment : Fragment() {

    private var _binding: FragmentRefaccionesBinding? = null
    private val binding get() = _binding!!
    private val args: RefaccionesFragmentArgs by navArgs()
    private val viewModel: RefaccionesViewModel by viewModels {
        RefaccionesViewModelFactory(requireContext())
    }
    private lateinit var adapter: RefaccionesAdapter
    private var refaccionSeleccionada: Refaccion? = null
    private var cantidadSeleccionada: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRefaccionesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvOrdenIdRef.text = "Orden #${args.ordenId}"
        setupRecycler()
        viewModel.loadRefacciones()

        binding.etBuscarRef.addTextChangedListener {
            adapter.filter(it.toString())
        }

        observeRefacciones()
        observeUsarRefaccion()
    }

    private fun setupRecycler() {
        adapter = RefaccionesAdapter { refaccion ->
            showCantidadDialog(refaccion)
        }
        binding.rvRefacciones.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRefacciones.adapter = adapter
    }

    private fun showCantidadDialog(refaccion: Refaccion) {
        val view = layoutInflater.inflate(com.techapp.R.layout.dialog_cantidad, null)
        val etCantidad = view.findViewById<com.google.android.material.textfield.TextInputEditText>(
            com.techapp.R.id.etCantidad
        )

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Usar: ${refaccion.nombre}")
            .setMessage("Stock disponible: ${refaccion.stock}")
            .setView(view)
            .setPositiveButton("Usar") { _, _ ->
                val cantidad = etCantidad.text.toString().toIntOrNull() ?: 1
                if (cantidad > 0 && cantidad <= refaccion.stock) {
                    refaccionSeleccionada = refaccion
                    cantidadSeleccionada = cantidad
                    viewModel.usarRefaccion(args.ordenId, refaccion.id, cantidad)
                } else {
                    Snackbar.make(binding.root, "Cantidad invalida", Snackbar.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun observeRefacciones() {
        viewModel.refaccionesState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> binding.progressRef.visibility = View.VISIBLE
                is Resource.Success -> {
                    binding.progressRef.visibility = View.GONE
                    adapter.submitList(resource.data ?: emptyList())
                }
                is Resource.Error -> {
                    binding.progressRef.visibility = View.GONE
                    Snackbar.make(binding.root, resource.message ?: "Error", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun observeUsarRefaccion() {
        viewModel.usarState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> binding.progressRef.visibility = View.VISIBLE
                is Resource.Success -> {
                    binding.progressRef.visibility = View.GONE
                    refaccionSeleccionada?.let { refaccion ->
                        MockMaintainQrData.registrarUsoRefaccion(
                            args.ordenId,
                            refaccion.nombre,
                            cantidadSeleccionada
                        )
                    }
                    Snackbar.make(binding.root, "Refaccion registrada", Snackbar.LENGTH_SHORT).show()
                    findNavController().previousBackStackEntry
                        ?.savedStateHandle?.set("refresh", true)
                    viewModel.loadRefacciones()
                }
                is Resource.Error -> {
                    binding.progressRef.visibility = View.GONE
                    Snackbar.make(binding.root, resource.message ?: "Error", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
