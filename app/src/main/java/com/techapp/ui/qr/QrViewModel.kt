package com.techapp.ui.qr

import android.content.Context
import androidx.lifecycle.*
import com.techapp.api.RetrofitClient
import com.techapp.models.OrdenDetalleResponse
import com.techapp.utils.Resource
import com.techapp.utils.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class QrViewModel(private val context: Context) : ViewModel() {

    private val sessionManager = SessionManager(context)
    private val api = RetrofitClient.apiService

    private val _qrState = MutableLiveData<Resource<OrdenDetalleResponse>>()
    val qrState: LiveData<Resource<OrdenDetalleResponse>> = _qrState

    fun buscarPorQR(serie: String) {
        viewModelScope.launch {
            _qrState.value = Resource.Loading()
            try {
                val token = sessionManager.token.first() ?: return@launch
                val response = api.getOrdenPorQR("Bearer $token", serie)
                if (response.isSuccessful && response.body()?.success == true) {
                    _qrState.value = Resource.Success(response.body()!!)
                } else {
                    _qrState.value = Resource.Error("No se encontró una orden para este equipo")
                }
            } catch (e: Exception) {
                _qrState.value = Resource.Error("Error de conexión: ${e.localizedMessage}")
            }
        }
    }
}

class QrViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = QrViewModel(context) as T
}
