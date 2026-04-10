package com.techapp.ui.parts

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techapp.api.RetrofitClient
import com.techapp.databinding.ItemRefaccionBinding
import com.techapp.models.Refaccion
import com.techapp.utils.Resource
import com.techapp.utils.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// ── ViewModel ────────────────────────────────────────────────────────────────
class RefaccionesViewModel(private val context: Context) : ViewModel() {

    private val sessionManager = SessionManager(context)
    private val api = RetrofitClient.apiService

    private val _refaccionesState = MutableLiveData<Resource<List<Refaccion>>>()
    val refaccionesState: LiveData<Resource<List<Refaccion>>> = _refaccionesState

    private val _usarState = MutableLiveData<Resource<Unit>>()
    val usarState: LiveData<Resource<Unit>> = _usarState

    fun loadRefacciones(buscar: String? = null) {
        viewModelScope.launch {
            _refaccionesState.value = Resource.Loading()
            try {
                val token = sessionManager.token.first() ?: return@launch
                val response = api.getRefacciones("Bearer $token", buscar)
                if (response.isSuccessful && response.body()?.success == true) {
                    _refaccionesState.value = Resource.Success(response.body()?.refacciones ?: emptyList())
                } else {
                    _refaccionesState.value = Resource.Error("Error al cargar refacciones")
                }
            } catch (e: Exception) {
                _refaccionesState.value = Resource.Error("Sin conexión: ${e.localizedMessage}")
            }
        }
    }

    fun usarRefaccion(ordenId: Int, refaccionId: Int, cantidad: Int) {
        viewModelScope.launch {
            _usarState.value = Resource.Loading()
            try {
                val token = sessionManager.token.first() ?: return@launch
                val response = api.usarRefaccion(
                    "Bearer $token",
                    com.techapp.models.UsarRefaccionRequest(ordenId, refaccionId, cantidad)
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    _usarState.value = Resource.Success(Unit)
                } else {
                    _usarState.value = Resource.Error(response.body()?.message ?: "Error")
                }
            } catch (e: Exception) {
                _usarState.value = Resource.Error("Sin conexión: ${e.localizedMessage}")
            }
        }
    }
}

class RefaccionesViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        RefaccionesViewModel(context) as T
}

// ── Adapter ──────────────────────────────────────────────────────────────────
class RefaccionesAdapter(
    private val onUsar: (Refaccion) -> Unit
) : ListAdapter<Refaccion, RefaccionesAdapter.VH>(Diff()) {

    private var originalList: List<Refaccion> = emptyList()

    override fun submitList(list: List<Refaccion>?) {
        originalList = list ?: emptyList()
        super.submitList(list)
    }

    fun filter(query: String) {
        val filtered = if (query.isEmpty()) originalList
        else originalList.filter {
            it.nombre.contains(query, true) || it.codigo.contains(query, true)
        }
        super.submitList(filtered)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemRefaccionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    inner class VH(private val b: ItemRefaccionBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: Refaccion) {
            b.tvNombreRef.text = item.nombre
            b.tvCodigoRef.text = "Código: ${item.codigo}"
            b.tvStockRef.text = "Stock: ${item.stock}"
            b.tvPrecioRef.text = "$${item.precio}"
            b.btnUsarRef.isEnabled = item.stock > 0
            b.btnUsarRef.setOnClickListener { onUsar(item) }
        }
    }

    class Diff : DiffUtil.ItemCallback<Refaccion>() {
        override fun areItemsTheSame(a: Refaccion, b: Refaccion) = a.id == b.id
        override fun areContentsTheSame(a: Refaccion, b: Refaccion) = a == b
    }
}
