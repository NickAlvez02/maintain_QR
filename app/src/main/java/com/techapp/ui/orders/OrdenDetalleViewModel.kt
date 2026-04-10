package com.techapp.ui.orders

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.techapp.api.RetrofitClient
import com.techapp.databinding.ItemHistorialBinding
import com.techapp.models.EvidenciaItem
import com.techapp.models.HistorialItem
import com.techapp.models.OrdenDetalleResponse
import com.techapp.utils.Resource
import com.techapp.utils.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class OrdenDetalleViewModel(private val context: Context) : ViewModel() {

    private val sessionManager = SessionManager(context)
    private val api = RetrofitClient.apiService

    private val _detalleState = MutableLiveData<Resource<OrdenDetalleResponse>>()
    val detalleState: LiveData<Resource<OrdenDetalleResponse>> = _detalleState

    fun loadDetalle(ordenId: Int) {
        viewModelScope.launch {
            _detalleState.value = Resource.Loading()
            try {
                val token = sessionManager.token.first() ?: return@launch
                val response = api.getOrdenDetalle("Bearer $token", ordenId)
                if (response.isSuccessful && response.body()?.success == true) {
                    _detalleState.value = Resource.Success(response.body()!!)
                } else {
                    _detalleState.value = Resource.Error(response.body()?.message ?: "Error al obtener detalle")
                }
            } catch (e: Exception) {
                _detalleState.value = Resource.Error("Sin conexiÃ³n: ${e.localizedMessage}")
            }
        }
    }

    fun aceptarOrden(ordenId: Int) {
        viewModelScope.launch {
            try {
                val token = sessionManager.token.first() ?: return@launch
                api.aceptarOrden("Bearer $token", ordenId)
            } catch (e: Exception) {
                // puedes manejar error aquÃ­ si quieres
            }
        }
    }

    fun rechazarOrden(ordenId: Int) {
        viewModelScope.launch {
            try {
                val token = sessionManager.token.first() ?: return@launch
                api.rechazarOrden("Bearer $token", ordenId)
            } catch (e: Exception) {
                // puedes manejar error aquÃ­ si quieres
            }
        }
    }
}

class OrdenDetalleViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        OrdenDetalleViewModel(context) as T
}

class HistorialAdapter(
    private val onClick: ((HistorialItem) -> Unit)? = null
) : ListAdapter<HistorialItem, HistorialAdapter.VH>(Diff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemHistorialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    inner class VH(private val b: ItemHistorialBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: HistorialItem) {
            b.tvAccion.text = item.accion
            b.tvDescripcion.text = item.descripcion
            b.tvTecnicoHist.text = item.tecnico
            b.tvFechaHist.text = item.fecha
            b.tvResumenRegistro.text =
                "${item.evidencias.size} evidencia(s) â€¢ ${item.refacciones.size} refaccion(es)"

            val previews = listOf(b.ivPreview1, b.ivPreview2, b.ivPreview3)
            previews.forEachIndexed { index, imageView ->
                bindPreview(imageView, item.evidencias.getOrNull(index))
            }

            b.root.setOnClickListener { onClick?.invoke(item) }
        }

        private fun bindPreview(imageView: ImageView, evidencia: EvidenciaItem?) {
            if (evidencia == null) {
                imageView.visibility = View.GONE
                imageView.setImageDrawable(null)
                return
            }

            imageView.visibility = View.VISIBLE
            when {
                evidencia.imageUri != null -> {
                    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    Glide.with(imageView)
                        .load(Uri.parse(evidencia.imageUri))
                        .into(imageView)
                }
                evidencia.imageResId != null -> {
                    imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    imageView.setImageResource(evidencia.imageResId)
                }
                else -> {
                    imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    imageView.setImageDrawable(null)
                }
            }
        }
    }

    class Diff : DiffUtil.ItemCallback<HistorialItem>() {
        override fun areItemsTheSame(a: HistorialItem, b: HistorialItem) = a.id == b.id
        override fun areContentsTheSame(a: HistorialItem, b: HistorialItem) = a == b
    }
}
