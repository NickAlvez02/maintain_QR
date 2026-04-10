package com.techapp.ui.finish

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.techapp.R
import com.techapp.databinding.FragmentFinalizarBinding
import com.techapp.ui.mock.MockMaintainQrData
import com.techapp.utils.Resource

class FinalizarFragment : Fragment() {

    private var _binding: FragmentFinalizarBinding? = null
    private val binding get() = _binding!!
    private val args: FinalizarFragmentArgs by navArgs()
    private val viewModel: FinalizarViewModel by viewModels {
        FinalizarViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinalizarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvOrdenIdFin.text = "Orden #${args.ordenId}"

        binding.btnConfirmarFinalizar.setOnClickListener {
            val obs = binding.etObservacionesFin.text.toString().trim()
            viewModel.finalizar(args.ordenId, obs.ifEmpty { null })
        }

        binding.btnCancelarFin.setOnClickListener {
            findNavController().popBackStack()
        }

        observeState()
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.btnConfirmarFinalizar.isEnabled = false
                    binding.progressFin.visibility = View.VISIBLE
                    binding.lottieCheck.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressFin.visibility = View.GONE
                    MockMaintainQrData.registrarFinalizacionServicio(args.ordenId)
                    binding.lottieCheck.visibility = View.VISIBLE
                    binding.lottieCheck.playAnimation()
                    binding.btnConfirmarFinalizar.visibility = View.GONE
                    binding.btnCancelarFin.text = "Volver a órdenes"
                    binding.tvEstadoFin.text = "¡Servicio finalizado exitosamente!"
                    binding.tvEstadoFin.setTextColor(
                        resources.getColor(R.color.estado_finalizado_text, null)
                    )
                    // Al volver navegar hasta la lista de órdenes
                    binding.btnCancelarFin.setOnClickListener {
                        findNavController().popBackStack(R.id.nav_ordenes, false)
                    }
                }
                is Resource.Error -> {
                    binding.progressFin.visibility = View.GONE
                    binding.btnConfirmarFinalizar.isEnabled = true
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
