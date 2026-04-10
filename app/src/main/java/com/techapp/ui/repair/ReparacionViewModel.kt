package com.techapp.ui.repair

import android.content.Context
import androidx.lifecycle.*
import com.techapp.api.RetrofitClient
import com.techapp.models.ReparacionRequest
import com.techapp.utils.Resource
import com.techapp.utils.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ReparacionViewModel(private val context: Context) : ViewModel() {

    private val sessionManager = SessionManager(context)
    private val api = RetrofitClient.apiService

    private val _state = MutableLiveData<Resource<Unit>>()
    val state: LiveData<Resource<Unit>> = _state

    fun registrarReparacion(ordenId: Int, acciones: String, observaciones: String?) {
        viewModelScope.launch {
            _state.value = Resource.Loading()
            try {
                val token = sessionManager.token.first() ?: return@launch
                val response = api.registrarReparacion(
                    "Bearer $token",
                    ReparacionRequest(
                        ordenId = ordenId,
                        acciones = acciones,
                        observaciones = observaciones
                    )
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    _state.value = Resource.Success(Unit)
                } else {
                    _state.value = Resource.Error(response.body()?.message ?: "Error al guardar")
                }
            } catch (e: Exception) {
                _state.value = Resource.Error("Sin conexión: ${e.localizedMessage}")
            }
        }
    }
}

class ReparacionViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        ReparacionViewModel(context) as T
}
