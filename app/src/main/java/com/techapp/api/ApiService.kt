package com.techapp.api

import com.techapp.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("login")
    @Headers("Accept: application/json")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("logout")
    suspend fun logout(@Header("Authorization") token: String): Response<ApiResponse>

    @GET("ordenes")
    suspend fun getOrdenes(
        @Header("Authorization") token: String
    ): Response<OrdenesResponse>

    @GET("ordenes/{id}")
    suspend fun getOrdenDetalle(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<OrdenDetalleResponse>

    @GET("ordenes/qr/{serie}")
    suspend fun getOrdenPorQR(
        @Header("Authorization") token: String,
        @Path("serie") serie: String
    ): Response<OrdenDetalleResponse>

    @POST("ordenes/{id}/aceptar")
    suspend fun aceptarOrden(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>

    @POST("ordenes/{id}/rechazar")
    suspend fun rechazarOrden(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>

    @POST("ordenes/{id}/diagnostico")
    suspend fun registrarDiagnostico(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: DiagnosticoRequest
    ): Response<ApiResponse>

    @POST("reparacion")
    suspend fun registrarReparacion(
        @Header("Authorization") token: String,
        @Body request: ReparacionRequest
    ): Response<ApiResponse>

    @GET("inventario")
    suspend fun getRefacciones(
        @Header("Authorization") token: String,
        @Query("buscar") buscar: String? = null
    ): Response<RefaccionesResponse>

    @POST("inventario/usar")
    suspend fun usarRefaccion(
        @Header("Authorization") token: String,
        @Body request: UsarRefaccionRequest
    ): Response<ApiResponse>

    @POST("finalizar")
    suspend fun finalizarServicio(
        @Header("Authorization") token: String,
        @Body request: FinalizarRequest
    ): Response<ApiResponse>

    @POST("fcm-token")
    suspend fun saveFcmToken(
        @Header("Authorization") token: String,
        @Body request: Map<String, String>
    ): Response<ApiResponse>
}