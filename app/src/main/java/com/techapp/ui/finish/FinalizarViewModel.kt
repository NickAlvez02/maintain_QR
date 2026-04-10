package com.techapp.ui.finish

import android.content.Context
import androidx.lifecycle.*
import com.techapp.api.RetrofitClient
import com.techapp.models.FinalizarRequest
import com.techapp.utils.Resource
import com.techapp.utils.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FinalizarViewModel(private val context: Context) : ViewModel() {

    private val sessionManager = SessionManager(context)
    private val api = RetrofitClient.apiService

    private val _state = MutableLiveData<Resource<Unit>>()
    val state: LiveData<Resource<Unit>> = _state

    fun finalizar(ordenId: Int, observaciones: String?) {
        viewModelScope.launch {
            _state.value = Resource.Loading()
            try {
                val token = sessionManager.token.first() ?: return@launch
                val response = api.finalizarServicio(
                    "Bearer $token",
                    FinalizarRequest(ordenId = ordenId, observacionesFinales = observaciones)
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    _state.value = Resource.Success(Unit)
                } else {
                    _state.value = Resource.Error(response.body()?.message ?: "Error al finalizar")
                }
            } catch (e: Exception) {
                _state.value = Resource.Error("Sin conexión: ${e.localizedMessage}")
            }
        }
    }
}

class FinalizarViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        FinalizarViewModel(context) as T
}
