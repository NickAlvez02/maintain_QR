package com.techapp.ui.repair

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.techapp.databinding.FragmentReparacionBinding
import com.techapp.utils.Resource

class ReparacionFragment : Fragment() {

    private var _binding: FragmentReparacionBinding? = null
    private val binding get() = _binding!!
    private val args: ReparacionFragmentArgs by navArgs()
    private val viewModel: ReparacionViewModel by viewModels {
        ReparacionViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReparacionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvOrdenIdRep.text = "Orden #${args.ordenId}"

        binding.btnGuardarReparacion.setOnClickListener {
            val acciones = binding.etAcciones.text.toString().trim()
            val observaciones = binding.etObservacionesRep.text.toString().trim()

            if (acciones.isEmpty()) {
                binding.tilAcciones.error = "Describe las acciones realizadas"
                return@setOnClickListener
            }
            binding.tilAcciones.error = null
            viewModel.registrarReparacion(args.ordenId, acciones, observaciones.ifEmpty { null })
        }

        observeState()
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.btnGuardarReparacion.isEnabled = false
                    binding.progressReparacion.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressReparacion.visibility = View.GONE
                    findNavController().previousBackStackEntry
                        ?.savedStateHandle?.set("refresh", true)
                    findNavController().popBackStack()
                }
                is Resource.Error -> {
                    binding.progressReparacion.visibility = View.GONE
                    binding.btnGuardarReparacion.isEnabled = true
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
