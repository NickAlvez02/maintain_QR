package com.techapp.ui.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.messaging.FirebaseMessaging
import com.techapp.api.RetrofitClient
import com.techapp.models.LoginRequest
import com.techapp.utils.Resource
import com.techapp.utils.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginViewModel(private val context: Context) : ViewModel() {

    private val sessionManager = SessionManager(context)
    private val api = RetrofitClient.apiService

    private val _loginState = MutableLiveData<Resource<Unit>>()
    val loginState: LiveData<Resource<Unit>> = _loginState

    fun checkSession(callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val token = sessionManager.token.first()
            callback(!token.isNullOrEmpty())
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading()
            try {
                val response = api.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body()?.token != null) {
                    val body = response.body()!!
                    val bearerToken = "Bearer ${body.token}"

                    // 1. Guardamos la sesión localmente
                    sessionManager.saveSession(
                        token = body.token!!,
                        id = body.user!!.id,
                        nombre = body.user.nombre ?: "Técnico",
                        usuario = body.user.email
                    )

                    // 2. Obtener token de Firebase y enviarlo al servidor
                    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val fcmToken = task.result
                            sendFcmTokenToServer(bearerToken, fcmToken)
                        } else {
                            Log.w("FCM", "Error al obtener el token de Firebase", task.exception)
                        }
                    }

                    _loginState.value = Resource.Success(Unit)
                } else {
                    val msg = response.body()?.message ?: "Credenciales incorrectas"
                    _loginState.value = Resource.Error(msg)
                }
            } catch (e: Exception) {
                _loginState.value = Resource.Error("Error: ${e.localizedMessage}")
            }
        }
    }

    private fun sendFcmTokenToServer(authToken: String, fcmToken: String) {
        viewModelScope.launch {
            try {
                val requestBody = mapOf("fcm_token" to fcmToken)
                val response = api.saveFcmToken(authToken, requestBody)

                if (response.isSuccessful) {
                    Log.d("FCM", "Token sincronizado con Railway correctamente")
                } else {
                    Log.e("FCM", "Error al sincronizar token: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("FCM", "Falla de red al enviar token: ${e.message}")
            }
        }
    }
}

class LoginViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
