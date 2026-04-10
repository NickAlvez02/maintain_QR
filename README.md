# TechApp — App Móvil para Técnicos de Servicio

App Android para gestión de órdenes de servicio técnico.  
Conecta con un backend Laravel via API REST con autenticación por token (Sanctum).

---

## 🏗️ Arquitectura

```
MVVM + Navigation Component + Coroutines + Retrofit
```

| Capa | Tecnología |
|------|-----------|
| UI | Fragments + ViewBinding + Material Design 3 |
| Estado | ViewModel + LiveData + Resource<T> sealed class |
| Navegación | Jetpack Navigation + Safe Args |
| Red | Retrofit 2 + OkHttp + Gson |
| Sesión | DataStore Preferences (reemplaza SharedPreferences) |
| Cámara/QR | CameraX + ZXing Android Embedded |
| Animación | Lottie |

---

## ⚡ Setup rápido

### 1. Clonar / abrir en Android Studio
```
File → Open → seleccionar carpeta TechnicianApp
```

### 2. Configurar URL del servidor
Edita `app/src/main/java/com/techapp/api/RetrofitClient.kt`:
```kotlin
private const val BASE_URL = "https://TU-SERVIDOR.com/"
```

> **Emulador**: usa `http://10.0.2.2:8000/`  
> **Dispositivo físico en red local**: usa la IP de tu PC, ej. `http://192.168.1.100:8000/`

### 3. Animación de éxito (Lottie)
- Descarga un JSON checkmark de [lottiefiles.com](https://lottiefiles.com/search?q=checkmark)
- Guárdalo en `app/src/main/res/raw/anim_check.json`

### 4. Sincronizar y compilar
Android Studio sincronizará Gradle automáticamente. Luego: **Run → Run 'app'**

---

## 📱 Pantallas

| Pantalla | Descripción |
|----------|-------------|
| Login | Autenticación con usuario/contraseña |
| Mis Órdenes | Lista con búsqueda y swipe-to-refresh |
| Detalle de Orden | Info completa + historial + acciones |
| Escanear QR | Cámara + búsqueda manual por serie |
| Diagnóstico | Registro del problema encontrado |
| Reparación | Registro de acciones realizadas |
| Refacciones | Catálogo con búsqueda y uso de piezas |
| Finalizar | Cierre del servicio con animación |
| Perfil | Info del técnico + cerrar sesión |

---

## 🔄 Flujo de estados de una orden

```
pendiente → en_diagnostico → en_reparacion → finalizado
```

Los botones de acción en el detalle se habilitan/deshabilitan automáticamente según el estado actual.

---

## 🌐 API Laravel requerida

Ver archivo `LARAVEL_API_ROUTES.php` con todos los endpoints, estructura de request/response y rutas de Laravel.

Autenticación: **Laravel Sanctum** (Bearer Token)

---

## 📦 Dependencias principales

```gradle
// UI
com.google.android.material:material:1.11.0

// Navegación
androidx.navigation:navigation-fragment-ktx:2.7.6

// Red
com.squareup.retrofit2:retrofit:2.9.0
com.squareup.okhttp3:logging-interceptor:4.12.0

// QR Scanner
com.journeyapps:zxing-android-embedded:4.3.0

// Sesión
androidx.datastore:datastore-preferences:1.0.0

// Animaciones
com.airbnb.android:lottie:6.3.0
```

---

## 🔧 Permisos en AndroidManifest

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" />
```

El permiso de cámara se solicita en tiempo de ejecución automáticamente al abrir el scanner QR.

---

## 🗂️ Estructura de paquetes

```
com.techapp/
├── api/           → Retrofit client + ApiService (endpoints)
├── models/        → Data classes (Kotlin) que mapean el JSON
├── utils/         → Resource<T>, SessionManager
└── ui/
    ├── login/     → LoginActivity + LoginViewModel
    ├── orders/    → Lista, Detalle, Adapters
    ├── qr/        → Scanner QR
    ├── diagnosis/ → Diagnóstico
    ├── repair/    → Reparación
    ├── parts/     → Refacciones
    ├── finish/    → Finalización
    └── profile/   → Perfil
```
