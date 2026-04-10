package com.techapp.ui.diagnosis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.techapp.databinding.FragmentDiagnosticoBinding
import com.techapp.utils.Resource

class DiagnosticoFragment : Fragment() {

    private var _binding: FragmentDiagnosticoBinding? = null
    private val binding get() = _binding!!
    private val args: DiagnosticoFragmentArgs by navArgs()
    private val viewModel: DiagnosticoViewModel by viewModels {
        DiagnosticoViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiagnosticoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvOrdenId.text = "Orden #${args.ordenId}"

        binding.btnGuardarDiagnostico.setOnClickListener {
            val diagnostico = binding.etDiagnostico.text.toString().trim()
            if (diagnostico.isEmpty()) {
                binding.tilDiagnostico.error = "Describe el diagnóstico"
                return@setOnClickListener
            }
            binding.tilDiagnostico.error = null
            viewModel.registrarDiagnostico(args.ordenId, diagnostico)
        }

        observeState()
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.btnGuardarDiagnostico.isEnabled = false
                    binding.progressDiagnostico.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressDiagnostico.visibility = View.GONE
                    // Avisar al detalle que haga refresh
                    findNavController().previousBackStackEntry
                        ?.savedStateHandle?.set("refresh", true)
                    findNavController().popBackStack()
                }
                is Resource.Error -> {
                    binding.progressDiagnostico.visibility = View.GONE
                    binding.btnGuardarDiagnostico.isEnabled = true
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
