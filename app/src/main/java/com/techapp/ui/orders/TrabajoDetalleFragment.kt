package com.techapp.ui.orders

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.techapp.databinding.FragmentTrabajoDetalleBinding
import com.techapp.databinding.ItemEvidenciaBinding
import com.techapp.models.EvidenciaItem
import com.techapp.models.RefaccionRegistroItem
import com.techapp.models.TrabajoDetalleItem
import com.techapp.ui.mock.MockMaintainQrData

class TrabajoDetalleFragment : Fragment() {

    private var _binding: FragmentTrabajoDetalleBinding? = null
    private val binding get() = _binding!!
    private val args: TrabajoDetalleFragmentArgs by navArgs()
    private lateinit var evidenciasAdapter: EvidenciasAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrabajoDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        evidenciasAdapter = EvidenciasAdapter()
        binding.rvEvidencias.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = evidenciasAdapter
        }
        renderTrabajo()
    }

    private fun renderTrabajo() {
        val trabajo = MockMaintainQrData.getTrabajoDetalle(args.ordenId, args.trabajoId)
            ?: TrabajoDetalleItem(
                id = args.trabajoId,
                ordenId = args.ordenId,
                titulo = args.trabajoTitulo,
                descripcion = args.trabajoDescripcion,
                tecnico = args.trabajoTecnico,
                fecha = args.trabajoFecha,
                resultado = args.trabajoDescripcion,
                evidencias = emptyList(),
                refaccionesUsadas = emptyList()
            )

        binding.tvTrabajoTitulo.text = trabajo.titulo
        binding.tvTrabajoMeta.text = "${trabajo.tecnico} • ${trabajo.fecha}"
        binding.tvTrabajoDescripcion.text = trabajo.descripcion
        binding.tvTrabajoResultado.text = trabajo.resultado
        binding.tvRefaccionesDetalle.text = formatRefacciones(trabajo.refaccionesUsadas)

        evidenciasAdapter.submitList(trabajo.evidencias)
        binding.tvEvidenciasEmpty.visibility =
            if (trabajo.evidencias.isEmpty()) View.VISIBLE else View.GONE
        binding.rvEvidencias.visibility =
            if (trabajo.evidencias.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun formatRefacciones(refacciones: List<RefaccionRegistroItem>): String {
        if (refacciones.isEmpty()) return "Sin refacciones registradas"
        return refacciones.joinToString("\n") { refaccion ->
            val observacion = refaccion.observacion?.takeIf { it.isNotBlank() }?.let {
                " • $it"
            }.orEmpty()
            "${refaccion.nombre} x${refaccion.cantidad}$observacion"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

private class EvidenciasAdapter :
    ListAdapter<EvidenciaItem, EvidenciasAdapter.EvidenciaViewHolder>(Diff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = EvidenciaViewHolder(
        ItemEvidenciaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: EvidenciaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class EvidenciaViewHolder(
        private val binding: ItemEvidenciaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: EvidenciaItem) {
            binding.tvNombreEvidencia.text = item.nombre
            binding.tvMetaEvidencia.text = listOfNotNull(
                item.origen,
                item.fecha,
                item.comentario?.takeIf { it.isNotBlank() }
            ).joinToString(" • ")

            when {
                item.imageUri != null -> {
                    binding.ivEvidencia.scaleType = ImageView.ScaleType.CENTER_CROP
                    Glide.with(binding.ivEvidencia)
                        .load(Uri.parse(item.imageUri))
                        .into(binding.ivEvidencia)
                }
                item.imageResId != null -> {
                    binding.ivEvidencia.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    binding.ivEvidencia.setImageResource(item.imageResId)
                }
                else -> {
                    binding.ivEvidencia.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    binding.ivEvidencia.setImageDrawable(null)
                }
            }
        }
    }

    class Diff : DiffUtil.ItemCallback<EvidenciaItem>() {
        override fun areItemsTheSame(oldItem: EvidenciaItem, newItem: EvidenciaItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: EvidenciaItem, newItem: EvidenciaItem) =
            oldItem == newItem
    }
}
