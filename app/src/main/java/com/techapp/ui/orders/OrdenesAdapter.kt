package com.techapp.ui.orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techapp.R
import com.techapp.databinding.ItemOrdenBinding
import com.techapp.models.OrdenServicio

class OrdenesAdapter(
    private val onClick: (OrdenServicio) -> Unit
) : ListAdapter<OrdenServicio, OrdenesAdapter.ViewHolder>(DiffCallback()) {

    private var originalList: List<OrdenServicio> = emptyList()

    override fun submitList(list: List<OrdenServicio>?) {
        originalList = list ?: emptyList()
        super.submitList(list)
    }

    fun filter(query: String) {
        if (query.isEmpty()) {
            super.submitList(originalList)
        } else {
            val filtered = originalList.filter {
                val cliente = it.equipo?.cliente
                val nombreCompleto = "${cliente?.nombre ?: ""} ${cliente?.apellidoPaterno ?: ""}"
                val equipoInfo = "${it.equipo?.marca ?: ""} ${it.equipo?.modelo ?: ""}"
                
                it.folio.contains(query, true) ||
                nombreCompleto.contains(query, true) ||
                equipoInfo.contains(query, true) ||
                it.estado.contains(query, true)
            }
            super.submitList(filtered)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrdenBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemOrdenBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(orden: OrdenServicio) {
            binding.apply {
                tvFolio.text = "# ${orden.folio}"
                val cliente = orden.equipo?.cliente
                tvCliente.text = "${cliente?.nombre} ${cliente?.apellidoPaterno ?: ""}"
                
                val equipo = orden.equipo
                tvEquipo.text = "${equipo?.tipo ?: ""} - ${equipo?.marca ?: ""} ${equipo?.modelo ?: ""}"
                
                tvProblema.text = orden.fallaReportada ?: "Sin reporte"
                tvFecha.text = orden.fechaRecepcion ?: ""

                // Estado con color
                tvEstado.text = orden.estado.replace("_", " ").uppercase()
                val (bgColor, textColor) = getEstadoColors(orden.estado)
                tvEstado.setBackgroundColor(ContextCompat.getColor(root.context, bgColor))
                tvEstado.setTextColor(ContextCompat.getColor(root.context, textColor))

                // Prioridad
                val prioridad = orden.prioridad ?: "media"
                val prioColor = when (prioridad.lowercase()) {
                    "alta" -> R.color.prioridad_alta
                    "media" -> R.color.prioridad_media
                    else -> R.color.prioridad_baja
                }
                indicadorPrioridad.setBackgroundColor(
                    ContextCompat.getColor(root.context, prioColor)
                )

                root.setOnClickListener { onClick(orden) }
            }
        }

        private fun getEstadoColors(estado: String): Pair<Int, Int> = when (estado.lowercase()) {
            "pendiente" -> Pair(R.color.estado_pendiente_bg, R.color.estado_pendiente_text)
            "en_diagnostico" -> Pair(R.color.estado_diagnostico_bg, R.color.estado_diagnostico_text)
            "en_reparacion" -> Pair(R.color.estado_reparacion_bg, R.color.estado_reparacion_text)
            "finalizado" -> Pair(R.color.estado_finalizado_bg, R.color.estado_finalizado_text)
            else -> Pair(R.color.estado_default_bg, R.color.estado_default_text)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<OrdenServicio>() {
        override fun areItemsTheSame(a: OrdenServicio, b: OrdenServicio) = a.id == b.id
        override fun areContentsTheSame(a: OrdenServicio, b: OrdenServicio) = a == b
    }
}
