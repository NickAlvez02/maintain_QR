package com.techapp.ui.orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techapp.databinding.ItemRefaccionUsadaBinding
import com.techapp.models.RefaccionUsada

class RefaccionesUsadasAdapter :
    ListAdapter<RefaccionUsada, RefaccionesUsadasAdapter.VH>(Diff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemRefaccionUsadaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    inner class VH(private val binding: ItemRefaccionUsadaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RefaccionUsada) {
            binding.tvNombreRefUsada.text = item.nombre
            binding.tvCantidadRefUsada.text = "Cantidad: ${item.cantidad}"
            binding.tvPrecioRefUsada.text = "Precio unitario: $${item.precio}"
        }
    }

    class Diff : DiffUtil.ItemCallback<RefaccionUsada>() {
        override fun areItemsTheSame(oldItem: RefaccionUsada, newItem: RefaccionUsada) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: RefaccionUsada, newItem: RefaccionUsada) =
            oldItem == newItem
    }
}
