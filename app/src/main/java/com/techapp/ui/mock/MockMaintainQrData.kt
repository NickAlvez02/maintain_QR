package com.techapp.ui.mock

import com.techapp.models.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object MockMaintainQrData {

    data class ActividadItem(
        val id: Int,
        val titulo: String,
        val descripcion: String,
        val fecha: String
    )

    data class NotificacionItem(
        val id: Int,
        val titulo: String,
        val mensaje: String,
        val fecha: String,
        val tipo: String
    )

    data class QrScanItem(
        val id: Int,
        val ordenId: Int,
        val equipo: String,
        val serie: String,
        val fecha: String,
        val origen: String
    )

    val ordenes = mutableListOf(
        OrdenServicio(
            id = 1,
            folio = "MQ-2401",
            fallaReportada = "No imprime etiquetas",
            solucionPropuesta = null,
            estadoFisico = "Bueno",
            estado = "pendiente",
            fechaRecepcion = "27 Mar 2026",
            equipo = Equipo(
                id = 1,
                tipo = "Impresora Zebra",
                marca = "Zebra",
                modelo = "ZT230",
                numeroSerie = "ZB-001-2401",
                cliente = Cliente(1, "Industrial Nova", null, null, null, null)
            ),
            prioridad = "alta",
            fechaEstimada = "28 Mar 2026",
            diagnostico = null,
            observaciones = "Pendiente de revision inicial"
        ),
        OrdenServicio(
            id = 2,
            folio = "MQ-2402",
            fallaReportada = "No enciende",
            solucionPropuesta = null,
            estadoFisico = "Desgastado",
            estado = "en_diagnostico",
            fechaRecepcion = "26 Mar 2026",
            equipo = Equipo(
                id = 2,
                tipo = "Laptop Dell",
                marca = "Dell",
                modelo = "Latitude 5420",
                numeroSerie = "DL-5420-7781",
                cliente = Cliente(2, "Logistica Atlas", null, null, null, null)
            ),
            prioridad = "media",
            fechaEstimada = "29 Mar 2026",
            diagnostico = "Falla en fuente interna",
            observaciones = "En espera de validacion"
        ),
        OrdenServicio(
            id = 3,
            folio = "MQ-2403",
            fallaReportada = "Pantalla congelada",
            solucionPropuesta = "Cambio de flex",
            estadoFisico = "Regular",
            estado = "en_reparacion",
            fechaRecepcion = "25 Mar 2026",
            equipo = Equipo(
                id = 3,
                tipo = "Handheld Scanner",
                marca = "Honeywell",
                modelo = "EDA52",
                numeroSerie = "HW-EDA52-990",
                cliente = Cliente(3, "Grupo Delta", null, null, null, null)
            ),
            prioridad = "alta",
            fechaEstimada = "30 Mar 2026",
            diagnostico = "Cambio de flex de display",
            observaciones = "Proceso activo"
        ),
        OrdenServicio(
            id = 4,
            folio = "MQ-2404",
            fallaReportada = "Puerto USB danado",
            solucionPropuesta = "Reemplazo de puerto",
            estadoFisico = "Bueno",
            estado = "finalizado",
            fechaRecepcion = "22 Mar 2026",
            equipo = Equipo(
                id = 4,
                tipo = "Terminal POS",
                marca = "Elo",
                modelo = "PayPoint Plus",
                numeroSerie = "ELO-PP-113",
                cliente = Cliente(4, "ServiMarket", null, null, null, null)
            ),
            prioridad = "baja",
            fechaEstimada = "24 Mar 2026",
            diagnostico = "Se reemplazo puerto USB",
            observaciones = "Listo para entrega"
        ),
        OrdenServicio(
            id = 5,
            folio = "MQ-2405",
            fallaReportada = "No carga bateria",
            solucionPropuesta = null,
            estadoFisico = "Bueno",
            estado = "pendiente",
            fechaRecepcion = "27 Mar 2026",
            equipo = Equipo(
                id = 5,
                tipo = "Tablet Samsung",
                marca = "Samsung",
                modelo = "Tab Active 3",
                numeroSerie = "SM-TA3-441",
                cliente = Cliente(5, "Almacen Central", null, null, null, null)
            ),
            prioridad = "media",
            fechaEstimada = "31 Mar 2026",
            diagnostico = null,
            observaciones = "Pendiente de asignacion"
        )
    )

    private val trabajosPorOrden = mutableMapOf(
        1 to mutableListOf(
            TrabajoDetalleItem(
                id = 101,
                ordenId = 1,
                titulo = "Recepcion inicial",
                descripcion = "Se documento la condicion general del equipo y se reviso su ingreso.",
                tecnico = "Luis Ramirez",
                fecha = "27 Mar 2026 09:10",
                resultado = "Equipo recibido para revision visual.",
                evidencias = listOf(
                    EvidenciaItem(
                        id = 1001,
                        trabajoId = 101,
                        nombre = "Frente del equipo",
                        origen = "Mock camara",
                        fecha = "27 Mar 2026 09:12",
                        imageResId = android.R.drawable.ic_menu_camera
                    )
                ),
                refaccionesUsadas = emptyList()
            )
        )
    )

    private val actividadReciente = mutableListOf<ActividadItem>()
    private val notificacionesSistema = mutableListOf<NotificacionItem>()
    private val historialQr = mutableListOf<QrScanItem>()

    fun filtrarOrdenes(filtro: String): List<OrdenServicio> {
        return when (filtro) {
            "pendientes" -> ordenes.filter { it.estado == "pendiente" }
            "en_proceso" -> ordenes.filter { it.estado == "en_diagnostico" || it.estado == "en_reparacion" }
            "finalizadas" -> ordenes.filter { it.estado == "finalizado" }
            else -> ordenes
        }
    }

    fun buscarPorSerie(serie: String): OrdenServicio? {
        return ordenes.firstOrNull { it.equipo?.numeroSerie.equals(serie.trim(), ignoreCase = true) }
    }

    fun buscarEquipos(query: String): List<OrdenServicio> {
        val term = query.trim()
        if (term.isBlank()) return ordenes
        return ordenes.filter { orden ->
            val equipo = orden.equipo
            val cliente = equipo?.cliente
            orden.folio.contains(term, true) ||
                (equipo?.tipo?.contains(term, true) == true) ||
                (equipo?.marca?.contains(term, true) == true) ||
                (equipo?.modelo?.contains(term, true) == true) ||
                (equipo?.numeroSerie?.contains(term, true) == true) ||
                (cliente?.nombre?.contains(term, true) == true)
        }
    }

    fun getHistorialTrabajos(ordenId: Int): List<HistorialItem> {
        return trabajosPorOrden[ordenId].orEmpty()
            .sortedByDescending { it.id }
            .map { trabajo ->
                HistorialItem(
                    id = trabajo.id,
                    accion = trabajo.titulo,
                    descripcion = trabajo.descripcion,
                    tecnico = trabajo.tecnico,
                    fecha = trabajo.fecha,
                    evidencias = trabajo.evidencias,
                    refacciones = trabajo.refaccionesUsadas
                )
            }
    }

    fun getTrabajoDetalle(ordenId: Int, trabajoId: Int): TrabajoDetalleItem? {
        return trabajosPorOrden[ordenId].orEmpty().firstOrNull { it.id == trabajoId }
    }

    fun registrarQrConsulta(orden: OrdenServicio, origen: String) {
        historialQr.add(
            0,
            QrScanItem(
                id = historialQr.size + 1,
                ordenId = orden.id,
                equipo = "${orden.equipo?.marca} ${orden.equipo?.modelo}",
                serie = orden.equipo?.numeroSerie ?: "N/A",
                fecha = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date()),
                origen = origen
            )
        )
    }

    fun registrarFinalizacionServicio(ordenId: Int) {
        val index = ordenes.indexOfFirst { it.id == ordenId }
        if (index != -1) {
            val orden = ordenes[index].copy(estado = "finalizado")
            ordenes[index] = orden

            actividadReciente.add(
                0,
                ActividadItem(
                    id = actividadReciente.size + 1,
                    titulo = "Orden Finalizada",
                    descripcion = "La orden ${orden.folio} ha sido marcada como finalizada.",
                    fecha = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date())
                )
            )
        }
    }

    fun crearRegistroTecnico(
        ordenId: Int,
        comentario: String,
        evidenciaUris: List<String>,
        refacciones: List<RefaccionRegistroItem>
    ) {
        val nextId = (trabajosPorOrden[ordenId]?.maxOfOrNull { it.id } ?: (ordenId * 100)) + 1
        val nuevoTrabajo = TrabajoDetalleItem(
            id = nextId,
            ordenId = ordenId,
            titulo = "Actualización de Servicio",
            descripcion = comentario,
            tecnico = "Usuario Actual",
            fecha = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date()),
            resultado = "Se agregaron evidencias y notas.",
            evidencias = evidenciaUris.mapIndexed { index, uriStr ->
                EvidenciaItem(
                    id = nextId * 10 + index,
                    trabajoId = nextId,
                    nombre = "Evidencia ${index + 1}",
                    origen = "Cámara",
                    fecha = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date()),
                    imageUri = uriStr
                )
            },
            refaccionesUsadas = refacciones
        )

        if (!trabajosPorOrden.containsKey(ordenId)) {
            trabajosPorOrden[ordenId] = mutableListOf()
        }
        trabajosPorOrden[ordenId]?.add(nuevoTrabajo)

        actividadReciente.add(
            0,
            ActividadItem(
                id = actividadReciente.size + 1,
                titulo = "Registro de Evidencia",
                descripcion = "Se añadió información a la orden ${folioDeOrden(ordenId)}",
                fecha = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date())
            )
        )
    }

    fun registrarUsoRefaccion(ordenId: Int, nombreRefaccion: String, cantidad: Int) {
        actividadReciente.add(
            0,
            ActividadItem(
                id = actividadReciente.size + 1,
                titulo = "Uso de Refacción",
                descripcion = "Se utilizaron $cantidad x $nombreRefaccion en la orden ${folioDeOrden(ordenId)}",
                fecha = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date())
            )
        )
    }

    fun getActividadReciente(): List<ActividadItem> = actividadReciente.toList()
    fun getNotificacionesSistema(): List<NotificacionItem> = notificacionesSistema.toList()
    fun getHistorialQr(): List<QrScanItem> = historialQr.toList()
    fun getUltimosEscaneos(limit: Int = 3): List<QrScanItem> = historialQr.take(limit)
    
    private fun folioDeOrden(ordenId: Int): String {
        return ordenes.firstOrNull { it.id == ordenId }?.folio ?: "#$ordenId"
    }
}
