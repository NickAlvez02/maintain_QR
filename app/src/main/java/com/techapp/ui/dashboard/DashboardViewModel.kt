package com.techapp.ui.dashboard

import android.content.Context
import androidx.lifecycle.*
import com.techapp.api.RetrofitClient
import com.techapp.models.OrdenServicio
import com.techapp.utils.Resource
import com.techapp.utils.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DashboardViewModel(private val context: Context) : ViewModel() {

    private val sessionManager = SessionManager(context)
    private val api = RetrofitClient.apiService

    private val _dashboardState = MutableLiveData<Resource<DashboardData>>()
    val dashboardState: LiveData<Resource<DashboardData>> = _dashboardState

    private val _tecnicoNombre = MutableLiveData<String?>()
    val tecnicoNombre: LiveData<String?> = _tecnicoNombre

    init {
        viewModelScope.launch {
            _tecnicoNombre.value = sessionManager.tecnicoNombre.first()
        }
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _dashboardState.value = Resource.Loading()
            try {
                val token = sessionManager.token.first() ?: return@launch
                val response = api.getOrdenes("Bearer $token")

                if (response.isSuccessful) {
                    val body = response.body()
                    val allOrders = body?.data ?: body?.ordenes ?: emptyList()

                    val pendientes = allOrders.count { it.estado.lowercase().contains("pendiente") }
                    val enEspera = allOrders.count {
                        val estado = it.estado.trim().lowercase()
                        estado == "espera"
                    }
                    val enProceso = allOrders.count {
                        it.estado.lowercase().contains("diagnostico") ||
                        it.estado.lowercase().contains("reparacion") ||
                        it.estado.lowercase().contains("proceso")
                    }
                    val canceladas = allOrders.count {
                        it.estado.lowercase().contains("cancelada")
                    }
                    val finalizadas = allOrders.count { it.estado.lowercase().contains("finalizado") }

                    val agenda = allOrders.filter { it.estado.lowercase() != "finalizado" }
                        .sortedByDescending { it.prioridad?.lowercase() == "alta" }
                        .take(5)

                    _dashboardState.value = Resource.Success(
                        DashboardData(pendientes, enProceso, finalizadas, enEspera, canceladas, agenda)
                    )
                } else {
                    _dashboardState.value = Resource.Error("Error al cargar datos")
                }
            } catch (e: Exception) {
                _dashboardState.value = Resource.Error("Sin conexiÃ³n: ${e.localizedMessage}")
            }
        }
    }
}

data class DashboardData(
    val pendientes: Int,
    val enProceso: Int,
    val finalizadas: Int,
    val enEspera: Int,
    val canceladas: Int,
    val agenda: List<OrdenServicio>
)

class DashboardViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        DashboardViewModel(context) as T
}
