package com.techapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class SessionManager(private val context: Context) {

    companion object {
        val TOKEN_KEY = stringPreferencesKey("token")
        val TECNICO_ID_KEY = intPreferencesKey("tecnico_id")
        val TECNICO_NOMBRE_KEY = stringPreferencesKey("tecnico_nombre")
        val TECNICO_USUARIO_KEY = stringPreferencesKey("tecnico_usuario")
    }

    val token: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    val tecnicoId: Flow<Int?> = context.dataStore.data.map { it[TECNICO_ID_KEY] }
    val tecnicoNombre: Flow<String?> = context.dataStore.data.map { it[TECNICO_NOMBRE_KEY] }
    val tecnicoUsuario: Flow<String?> = context.dataStore.data.map { it[TECNICO_USUARIO_KEY] }

    suspend fun saveSession(token: String, id: Int, nombre: String, usuario: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[TECNICO_ID_KEY] = id
            prefs[TECNICO_NOMBRE_KEY] = nombre
            prefs[TECNICO_USUARIO_KEY] = usuario
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }

    fun bearerToken(rawToken: String) = "Bearer $rawToken"
}
