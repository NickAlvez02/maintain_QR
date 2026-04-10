package com.techapp.ui.orders

import android.content.Context
import androidx.lifecycle.*
import com.techapp.api.RetrofitClient
import com.techapp.models.OrdenServicio
import com.techapp.utils.Resource
import com.techapp.utils.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class OrdenesViewModel(private val context: Context) : ViewModel() {

    private val sessionManager = SessionManager(context)
    private val api = RetrofitClient.apiService

    private val _ordenesState = MutableLiveData<Resource<List<OrdenServicio>>>()
    val ordenesState: LiveData<Resource<List<OrdenServicio>>> = _ordenesState

    fun loadOrdenes() {
        viewModelScope.launch {
            _ordenesState.value = Resource.Loading()
            try {
                val token = sessionManager.token.first() ?: return@launch
                val response = api.getOrdenes("Bearer $token")
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    val lista = body.ordenes ?: body.data ?: emptyList()
                    _ordenesState.value = Resource.Success(lista)
                } else {
                    _ordenesState.value = Resource.Error(body?.message ?: "Error al obtener Ã³rdenes")
                }
            } catch (e: Exception) {
                _ordenesState.value = Resource.Error("Sin conexiÃ³n: ${e.localizedMessage}")
            }
        }
    }
}

class OrdenesViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = OrdenesViewModel(context) as T
}
