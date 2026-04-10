package com.techapp.models

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("token") val token: String?,
    @SerializedName("user") val user: UserInfo?
)

data class UserInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("email") val email: String,
    @SerializedName("rol") val rol: String?
)

data class Cliente(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("apellido_paterno") val apellidoPaterno: String?,
    @SerializedName("apellido_materno") val apellidoMaterno: String?,
    @SerializedName("correo") val correo: String?,
    @SerializedName("telefono") val telefono: String?
)

data class Equipo(
    @SerializedName("id") val id: Int,
    @SerializedName("tipo") val tipo: String?,
    @SerializedName("marca") val marca: String?,
    @SerializedName("modelo") val modelo: String?,
    @SerializedName("numero_serie") val numeroSerie: String?,
    @SerializedName("cliente") val cliente: Cliente?
)

data class OrdenServicio(
    @SerializedName("id") val id: Int,
    @SerializedName("folio") val folio: String,
    @SerializedName("falla_reportada") val fallaReportada: String?,
    @SerializedName("solucion_propuesta") val solucionPropuesta: String?,
    @SerializedName("estado_fisico") val estadoFisico: String?,
    @SerializedName("estado") val estado: String,
    @SerializedName("fecha_recepcion") val fechaRecepcion: String?,
    @SerializedName("equipo") val equipo: Equipo?,
    @SerializedName("prioridad") val prioridad: String? = "Media",
    @SerializedName("fecha_estimada_entrega") val fechaEstimada: String?,
    @SerializedName("diagnostico") val diagnostico: String?,
    @SerializedName("observaciones") val observaciones: String?
)

data class OrdenesResponse(
    @SerializedName("success") val success: Boolean?,
    @SerializedName("data") val data: List<OrdenServicio>?,
    @SerializedName("ordenes") val ordenes: List<OrdenServicio>?,
    @SerializedName("message") val message: String?
)

data class OrdenDetalleResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("orden") val orden: OrdenServicio?,
    @SerializedName("historial") val historial: List<HistorialItem>?,
    @SerializedName("refacciones") val refacciones: List<RefaccionUsada>?,
    @SerializedName("message") val message: String?
)

data class HistorialItem(
    @SerializedName("id") val id: Int,
    @SerializedName("accion") val accion: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("tecnico") val tecnico: String,
    @SerializedName("fecha") val fecha: String,
    val evidencias: List<EvidenciaItem> = emptyList(),
    val refacciones: List<RefaccionRegistroItem> = emptyList()
)

data class EvidenciaItem(
    val id: Int,
    val trabajoId: Int,
    val nombre: String,
    val origen: String,
    val fecha: String,
    val comentario: String? = null,
    val imageResId: Int? = null,
    val imageUri: String? = null
)

data class RefaccionRegistroItem(
    val id: Int,
    val nombre: String,
    val cantidad: Int,
    val observacion: String? = null
)

data class TrabajoDetalleItem(
    val id: Int,
    val ordenId: Int,
    val titulo: String,
    val descripcion: String,
    val tecnico: String,
    val fecha: String,
    val resultado: String,
    val evidencias: List<EvidenciaItem>,
    val refaccionesUsadas: List<RefaccionRegistroItem> = emptyList()
)

data class DiagnosticoRequest(
    @SerializedName("orden_id") val ordenId: Int,
    @SerializedName("diagnostico") val diagnostico: String,
    @SerializedName("estado") val estado: String = "en_diagnostico"
)

data class ReparacionRequest(
    @SerializedName("orden_id") val ordenId: Int,
    @SerializedName("acciones") val acciones: String,
    @SerializedName("observaciones") val observaciones: String?,
    @SerializedName("estado") val estado: String = "en_reparacion"
)

data class Refaccion(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("codigo") val codigo: String,
    @SerializedName("stock") val stock: Int,
    @SerializedName("precio") val precio: Double
)

data class RefaccionesResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("refacciones") val refacciones: List<Refaccion>?,
    @SerializedName("message") val message: String?
)

data class RefaccionUsada(
    @SerializedName("id") val id: Int,
    @SerializedName("refaccion_id") val refaccionId: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("cantidad") val cantidad: Int,
    @SerializedName("precio") val precio: Double
)

data class UsarRefaccionRequest(
    @SerializedName("orden_id") val ordenId: Int,
    @SerializedName("refaccion_id") val refaccionId: Int,
    @SerializedName("cantidad") val cantidad: Int
)

data class FinalizarRequest(
    @SerializedName("orden_id") val ordenId: Int,
    @SerializedName("observaciones_finales") val observacionesFinales: String?,
    @SerializedName("estado") val estado: String = "finalizado"
)

data class ApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?
)
