<?php

// ═══════════════════════════════════════════════════════════════
//  RUTAS LARAVEL REQUERIDAS POR TECHAPP MOBILE
//  Archivo: routes/api.php
// ═══════════════════════════════════════════════════════════════

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\TecnicoController;

// ── Sin autenticación ────────────────────────────────────────
Route::post('/tecnico/login', [TecnicoController::class, 'login']);

// ── Con autenticación (Sanctum / Passport) ───────────────────
Route::middleware('auth:sanctum')->prefix('tecnico')->group(function () {

    Route::post('/logout',              [TecnicoController::class, 'logout']);

    // Órdenes
    Route::get('/ordenes',              [TecnicoController::class, 'ordenes']);
    Route::get('/ordenes/{id}',         [TecnicoController::class, 'ordenDetalle']);
    Route::get('/ordenes/qr/{serie}',   [TecnicoController::class, 'ordenPorQR']);

    // Acciones
    Route::post('/diagnostico',         [TecnicoController::class, 'registrarDiagnostico']);
    Route::post('/reparacion',          [TecnicoController::class, 'registrarReparacion']);
    Route::post('/finalizar',           [TecnicoController::class, 'finalizarServicio']);

    // Refacciones / Inventario
    Route::get('/refacciones',          [TecnicoController::class, 'refacciones']);
    Route::post('/refacciones/usar',    [TecnicoController::class, 'usarRefaccion']);
});


// ═══════════════════════════════════════════════════════════════
//  ESTRUCTURA JSON ESPERADA POR CADA ENDPOINT
// ═══════════════════════════════════════════════════════════════

/*
POST /api/tecnico/login
  Request:  { "usuario": "string", "password": "string" }
  Response: {
    "success": true,
    "token": "Bearer token string",
    "tecnico": {
      "id": 1, "nombre": "Juan", "apellido": "Pérez",
      "usuario": "jperez", "email": "j@mail.com"
    }
  }

GET /api/tecnico/ordenes
  Headers:  Authorization: Bearer {token}
  Response: {
    "success": true,
    "ordenes": [{
      "id": 1, "folio": "ORD-001", "cliente": "Nombre cliente",
      "equipo": "Laptop", "marca": "Dell", "modelo": "Inspiron 15",
      "numero_serie": "ABC123", "problema_reportado": "No enciende",
      "estado": "pendiente|en_diagnostico|en_reparacion|finalizado",
      "prioridad": "alta|media|baja",
      "fecha_ingreso": "2024-01-15", "fecha_estimada": "2024-01-20",
      "diagnostico": null, "observaciones": null
    }]
  }

GET /api/tecnico/ordenes/{id}
  Response: {
    "success": true,
    "orden": { ...mismo objeto de arriba... },
    "historial": [{
      "id": 1, "accion": "Diagnóstico registrado",
      "descripcion": "Se encontró falla en...", "tecnico": "Juan P.",
      "fecha": "2024-01-15 10:30"
    }],
    "refacciones": [{
      "id": 1, "refaccion_id": 5, "nombre": "Fuente de poder",
      "cantidad": 1, "precio": 350.00
    }]
  }

GET /api/tecnico/ordenes/qr/{serie}
  → Mismo response que GET /ordenes/{id} pero buscando por numero_serie

POST /api/tecnico/diagnostico
  Request:  { "orden_id": 1, "diagnostico": "string", "estado": "en_diagnostico" }
  Response: { "success": true, "message": "Diagnóstico registrado" }

POST /api/tecnico/reparacion
  Request:  { "orden_id": 1, "acciones": "string", "observaciones": "string|null", "estado": "en_reparacion" }
  Response: { "success": true, "message": "Reparación registrada" }

GET /api/tecnico/refacciones?buscar=opcional
  Response: {
    "success": true,
    "refacciones": [{
      "id": 1, "nombre": "Fuente de poder ATX", "codigo": "FP-ATX-001",
      "stock": 5, "precio": 350.00
    }]
  }

POST /api/tecnico/refacciones/usar
  Request:  { "orden_id": 1, "refaccion_id": 5, "cantidad": 1 }
  Response: { "success": true, "message": "Refacción registrada y stock actualizado" }

POST /api/tecnico/finalizar
  Request:  { "orden_id": 1, "observaciones_finales": "string|null", "estado": "finalizado" }
  Response: { "success": true, "message": "Servicio finalizado correctamente" }
*/
