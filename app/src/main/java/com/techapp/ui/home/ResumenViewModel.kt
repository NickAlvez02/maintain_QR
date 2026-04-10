package com.techapp.ui.home

import android.content.Context
import androidx.lifecycle.*
import com.techapp.api.RetrofitClient
import com.techapp.models.OrdenServicio
import com.techapp.utils.Resource
import com.techapp.utils.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ResumenViewModel(private val context: Context) : ViewModel() {
    private val sessionManager = SessionManager(context)
    private val api = RetrofitClient.apiService

    private val _countState = MutableLiveData<Resource<List<OrdenServicio>>>()
    val countState: LiveData<Resource<List<OrdenServicio>>> = _countState

    fun loadResumen() {
        viewModelScope.launch {
            _countState.value = Resource.Loading()
            try {
                val token = sessionManager.token.first() ?: return@launch
                val response = api.getOrdenes("Bearer $token")
                if (response.isSuccessful) {
                    val lista = response.body()?.ordenes ?: response.body()?.data ?: emptyList()
                    _countState.value = Resource.Success(lista)
                } else {
                    _countState.value = Resource.Error("Error al cargar resumen")
                }
            } catch (e: Exception) {
                _countState.value = Resource.Error(e.localizedMessage ?: "Error desconocido")
            }
        }
    }
}

class ResumenViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ResumenViewModel(context) as T
}
