package com.techapp.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.techapp.databinding.FragmentOrdenDetalleBinding
import com.techapp.utils.Resource

class OrdenDetalleFragment : Fragment() {

    private var _binding: FragmentOrdenDetalleBinding? = null
    private val binding get() = _binding!!
    private val args: OrdenDetalleFragmentArgs by navArgs()

    private val viewModel: OrdenDetalleViewModel by viewModels {
        OrdenDetalleViewModelFactory(requireContext())
    }

    private lateinit var historialAdapter: HistorialAdapter
    private lateinit var refaccionesAdapter: RefaccionesUsadasAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdenDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLists()
        setupButtons()
        observeDetalle()

        viewModel.loadDetalle(args.ordenId)
    }

    private fun setupLists() {
        historialAdapter = HistorialAdapter()
        refaccionesAdapter = RefaccionesUsadasAdapter()

        binding.rvHistorial.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historialAdapter
        }

        binding.rvRefaccionesUsadas.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = refaccionesAdapter
        }
    }

    private fun setupButtons() {

        // âœ… ACEPTAR
        binding.btnAceptarOrden.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Aceptar orden")
                .setMessage("Â¿Deseas aceptar esta orden?")
                .setPositiveButton("SÃ­") { _, _ ->
                    viewModel.aceptarOrden(args.ordenId)
                    findNavController().popBackStack()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        // âŒ RECHAZAR
        binding.btnRechazarOrden.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Rechazar orden")
                .setMessage("Â¿Deseas rechazar esta orden?")
                .setPositiveButton("SÃ­") { _, _ ->
                    viewModel.rechazarOrden(args.ordenId)
                    findNavController().popBackStack()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun observeDetalle() {
        viewModel.detalleState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressDetalle.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    binding.progressDetalle.visibility = View.GONE

                    resource.data?.orden?.let { orden ->
                        binding.apply {
                            val equipo = orden.equipo
                            val cliente = equipo?.cliente

                            tvFolioDetalle.text = "Folio ${orden.folio}"
                            tvClienteDetalle.text =
                                "${cliente?.nombre ?: ""} ${cliente?.apellidoPaterno ?: ""}"

                            tvEstadoDetalle.text =
                                orden.estado.replace("_", " ").uppercase()

                            tvPrioridadDetalle.text =
                                "PRIORIDAD ${orden.prioridad?.uppercase() ?: "MEDIA"}"

                            tvFechaDetalle.text =
                                "Ingreso: ${orden.fechaRecepcion ?: "N/A"}"

                            tvFechaEstimadaDetalle.text =
                                "Fecha estimada: ${orden.fechaEstimada ?: "No definida"}"

                            tvEquipoDetalle.text = listOfNotNull(
                                equipo?.tipo,
                                equipo?.marca?.takeIf { it.isNotBlank() },
                                equipo?.modelo?.takeIf { it.isNotBlank() }
                            ).joinToString(" â€¢ ")

                            tvSerieDetalle.text =
                                "Serie: ${equipo?.numeroSerie ?: "N/A"}"

                            tvProblemaDetalle.text =
                                orden.fallaReportada ?: "Sin reporte"

                            tvDiagnosticoDetalle.text =
                                orden.diagnostico ?: "Sin diagnostico registrado"

                            tvObservacionesDetalle.text =
                                orden.observaciones ?: "Sin observaciones registradas"
                        }
                    }

                    val historial = resource.data?.historial ?: emptyList()
                    historialAdapter.submitList(historial)

                    binding.tvHistorialEmpty.visibility =
                        if (historial.isEmpty()) View.VISIBLE else View.GONE

                    binding.rvHistorial.visibility =
                        if (historial.isEmpty()) View.GONE else View.VISIBLE

                    val refacciones = resource.data?.refacciones ?: emptyList()
                    refaccionesAdapter.submitList(refacciones)

                    binding.tvRefaccionesEmpty.visibility =
                        if (refacciones.isEmpty()) View.VISIBLE else View.GONE

                    binding.rvRefaccionesUsadas.visibility =
                        if (refacciones.isEmpty()) View.GONE else View.VISIBLE
                }

                is Resource.Error -> {
                    binding.progressDetalle.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
