package com.techapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.techapp.R
import com.techapp.databinding.ActivityMainBinding
import com.techapp.ui.login.LoginActivity
import com.techapp.ui.mock.MockMaintainQrData
import com.techapp.utils.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var sessionManager: SessionManager
    private lateinit var appBarConfig: AppBarConfiguration
    private var currentDrawerSection: Int? = null
    private val topLevelDestinations = setOf(
        R.id.nav_dashboard,
        R.id.nav_resumen,
        R.id.nav_actividad_reciente,
        R.id.nav_notificaciones,
        R.id.nav_centro_acciones,
        R.id.nav_qr,
        R.id.nav_qr_historial,
        R.id.nav_qr_buscar_equipo,
        R.id.nav_qr_ultimos_escaneos,
        R.id.nav_ordenes,
        R.id.nav_ordenes_evidencias,
        R.id.nav_perfil,
        R.id.nav_configuracion,
        R.id.nav_consultar_equipos
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        sessionManager = SessionManager(applicationContext)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfig = AppBarConfiguration(topLevelDestinations, binding.drawerLayout)

        setupActionBarWithNavController(navController, appBarConfig)
        binding.bottomNav.setupWithNavController(navController)
        setupDynamicDrawerClicks()

        navController.addOnDestinationChangedListener { _, destination, arguments ->
            binding.bottomNav.visibility =
                if (destination.id in topLevelDestinations) View.VISIBLE else View.GONE
            syncNavigationUi(destination, arguments)
        }

        if (savedInstanceState == null) {
            syncNavigationUi(navController.currentDestination, null)
        }
        // Solicitar permiso de notificaciones para Android 13+
        checkNotificationPermission()
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }

    private fun updateDrawerMenu(destinationId: Int) {
        if (currentDrawerSection == destinationId) return
        binding.navigationView.menu.clear()
        when (destinationId) {
            R.id.nav_dashboard,
            R.id.nav_resumen -> {
                binding.navigationView.inflateMenu(R.menu.menu_drawer_inicio)
                updateHeader("Centro de operaciones")
            }
            R.id.nav_qr -> {
                binding.navigationView.inflateMenu(R.menu.menu_drawer_qr)
                updateHeader("Escaneo y consulta rapida")
            }
            R.id.nav_ordenes -> {
                binding.navigationView.inflateMenu(R.menu.menu_drawer_ordenes)
                updateHeader("Gestion de ordenes")
            }
            R.id.nav_perfil -> {
                binding.navigationView.inflateMenu(R.menu.menu_drawer_perfil)
                updateHeader("Cuenta y preferencias")
            }
        }
        binding.navigationView.menu.setGroupCheckable(0, true, true)
        currentDrawerSection = destinationId
    }

    private fun updateHeader(subtitle: String) {
        val headerView = binding.navigationView.getHeaderView(0)
        val txtTitle = headerView.findViewById<TextView>(R.id.txtHeaderTitle)
        val txtSubtitle = headerView.findViewById<TextView>(R.id.txtHeaderSubtitle)
        val txtRole = headerView.findViewById<TextView>(R.id.txtHeaderRole)
        val txtUser = headerView.findViewById<TextView>(R.id.txtHeaderUser)
        val txtInitials = headerView.findViewById<TextView>(R.id.txtHeaderInitials)
        val txtPendientes = headerView.findViewById<TextView>(R.id.txtHeaderPendientes)
        val txtProceso = headerView.findViewById<TextView>(R.id.txtHeaderProceso)

        txtSubtitle.text = subtitle
        // Ocultamos los contadores del drawer por ahora para evitar datos falsos
        txtPendientes.visibility = View.GONE
        txtProceso.visibility = View.GONE
        headerView.findViewById<View>(R.id.labelPendientes)?.visibility = View.GONE
        headerView.findViewById<View>(R.id.labelProceso)?.visibility = View.GONE

        lifecycleScope.launch {
            val nombre = sessionManager.tecnicoNombre.first()?.takeIf { it.isNotBlank() }
                ?: "Tecnico MaintainQR"
            val usuario = sessionManager.tecnicoUsuario.first()?.takeIf { it.isNotBlank() }
                ?: "tech.${nombre.lowercase().replace(" ", ".")}"
            val tecnicoId = sessionManager.tecnicoId.first()
            txtTitle.text = nombre
            txtRole.text = "Tecnico de servicio"
            txtUser.text = tecnicoId?.let { "$usuario • ID $it" } ?: usuario
            txtInitials.text = initialsFromName(nombre)
        }
    }

    private fun initialsFromName(name: String): String {
        return name.trim()
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .mapNotNull { it.firstOrNull()?.toString() }
            .joinToString("")
            .uppercase()
            .ifBlank { "TM" }
    }

    private fun setupDynamicDrawerClicks() {
        binding.navigationView.setNavigationItemSelectedListener { item ->
            val handled = when (item.itemId) {
                R.id.menu_inicio_resumen -> navigateTopLevel(R.id.nav_dashboard)
                R.id.menu_inicio_actividad -> navigateDrawerDestination(R.id.nav_actividad_reciente)
                R.id.menu_inicio_notificaciones -> navigateDrawerDestination(R.id.nav_notificaciones)
                R.id.menu_inicio_accesos -> navigateDrawerDestination(R.id.nav_centro_acciones)

                R.id.menu_qr_escanear -> navigateTopLevel(R.id.nav_qr)
                R.id.menu_qr_historial -> navigateDrawerDestination(R.id.nav_qr_historial)
                R.id.menu_qr_buscar -> navigateDrawerDestination(R.id.nav_qr_buscar_equipo)
                R.id.menu_qr_ultimos -> navigateDrawerDestination(R.id.nav_qr_ultimos_escaneos)

                R.id.menu_ordenes_todas -> navigateToOrdenes("todas")
                R.id.menu_ordenes_pendientes -> navigateToOrdenes("pendientes")
                R.id.menu_ordenes_proceso -> navigateToOrdenes("en_proceso")
                R.id.menu_ordenes_finalizadas -> navigateToOrdenes("finalizadas")
                R.id.menu_ordenes_evidencias -> navigateDrawerDestination(R.id.nav_ordenes_evidencias)

                R.id.menu_perfil_mi_perfil -> navigateTopLevel(R.id.nav_perfil)
                R.id.menu_perfil_configuracion -> navigateDrawerDestination(R.id.nav_configuracion)
                R.id.action_logout -> {
                    confirmLogout()
                    true
                }
                else -> false
            }
            if (handled && item.isCheckable) item.isChecked = true
            handled
        }
    }

    private fun navigateDrawerDestination(destinationId: Int): Boolean {
        if (navController.currentDestination?.id == destinationId) {
            closeDrawer()
            return true
        }
        navController.navigate(destinationId)
        closeDrawer()
        return true
    }

    private fun navigateTopLevel(destinationId: Int): Boolean {
        if (navController.currentDestination?.id == destinationId) {
            closeDrawer()
            return true
        }
        navController.navigate(destinationId)
        closeDrawer()
        return true
    }

    private fun navigateToOrdenes(filtro: String): Boolean {
        if (navController.currentDestination?.id == R.id.nav_ordenes &&
            currentOrdenesFilter(navController.currentBackStackEntry?.arguments) == filtro
        ) {
            closeDrawer()
            return true
        }
        val bundle = Bundle().apply { putString("filtro", filtro) }
        navController.navigate(R.id.nav_ordenes, bundle)
        closeDrawer()
        return true
    }

    private fun showPlaceholder(sectionName: String): Boolean {
        Toast.makeText(this, "$sectionName quedara como siguiente pantalla", Toast.LENGTH_SHORT).show()
        closeDrawer()
        return true
    }

    private fun setBottomNavChecked(itemId: Int) {
        binding.bottomNav.menu.findItem(itemId)?.isChecked = true
    }

    private fun syncNavigationUi(destination: NavDestination?, arguments: Bundle?) {
        if (destination == null) return

        val drawerSection = resolveDrawerSection(destination.id)
        updateDrawerMenu(drawerSection)
        binding.toolbar.title = resolveToolbarTitle(destination.id)
        resolveBottomNavItem(destination.id)?.let(::setBottomNavChecked)
        resolveDrawerItem(destination.id, arguments)?.let { binding.navigationView.setCheckedItem(it) }
    }

    private fun resolveDrawerSection(destinationId: Int): Int {
        return when (destinationId) {
            R.id.nav_dashboard,
            R.id.nav_resumen,
            R.id.nav_actividad_reciente,
            R.id.nav_notificaciones,
            R.id.nav_centro_acciones,
            R.id.nav_consultar_equipos -> R.id.nav_dashboard

            R.id.nav_qr,
            R.id.nav_qr_historial,
            R.id.nav_qr_buscar_equipo,
            R.id.nav_qr_ultimos_escaneos -> R.id.nav_qr

            R.id.nav_ordenes,
            R.id.nav_ordenes_evidencias,
            R.id.nav_orden_detalle,
            R.id.nav_trabajo_detalle,
            R.id.nav_registro_evidencia,
            R.id.nav_refacciones,
            R.id.nav_finalizar -> R.id.nav_ordenes

            else -> R.id.nav_perfil
        }
    }

    private fun resolveDrawerItem(destinationId: Int, arguments: Bundle?): Int? {
        return when (destinationId) {
            R.id.nav_dashboard,
            R.id.nav_resumen -> R.id.menu_inicio_resumen
            R.id.nav_actividad_reciente -> R.id.menu_inicio_actividad
            R.id.nav_notificaciones -> R.id.menu_inicio_notificaciones
            R.id.nav_centro_acciones,
            R.id.nav_consultar_equipos -> R.id.menu_inicio_accesos

            R.id.nav_qr -> R.id.menu_qr_escanear
            R.id.nav_qr_historial -> R.id.menu_qr_historial
            R.id.nav_qr_buscar_equipo -> R.id.menu_qr_buscar
            R.id.nav_qr_ultimos_escaneos -> R.id.menu_qr_ultimos

            R.id.nav_ordenes -> when (currentOrdenesFilter(arguments)) {
                "pendientes" -> R.id.menu_ordenes_pendientes
                "en_proceso" -> R.id.menu_ordenes_proceso
                "finalizadas" -> R.id.menu_ordenes_finalizadas
                else -> R.id.menu_ordenes_todas
            }

            R.id.nav_ordenes_evidencias -> R.id.menu_ordenes_evidencias

            R.id.nav_orden_detalle,
            R.id.nav_trabajo_detalle,
            R.id.nav_registro_evidencia,
            R.id.nav_refacciones,
            R.id.nav_finalizar -> resolveDeepOrderDrawerItem()

            R.id.nav_perfil -> R.id.menu_perfil_mi_perfil
            R.id.nav_configuracion -> R.id.menu_perfil_configuracion
            else -> null
        }
    }

    private fun resolveDeepOrderDrawerItem(): Int {
        val previousDestinationId = navController.previousBackStackEntry?.destination?.id
        val previousArgs = navController.previousBackStackEntry?.arguments
        return when (previousDestinationId) {
            R.id.nav_ordenes_evidencias -> R.id.menu_ordenes_evidencias
            R.id.nav_ordenes -> when (currentOrdenesFilter(previousArgs)) {
                "pendientes" -> R.id.menu_ordenes_pendientes
                "en_proceso" -> R.id.menu_ordenes_proceso
                "finalizadas" -> R.id.menu_ordenes_finalizadas
                else -> R.id.menu_ordenes_todas
            }
            else -> R.id.menu_ordenes_todas
        }
    }

    private fun resolveBottomNavItem(destinationId: Int): Int? {
        return when (destinationId) {
            R.id.nav_dashboard,
            R.id.nav_resumen,
            R.id.nav_actividad_reciente,
            R.id.nav_notificaciones,
            R.id.nav_centro_acciones,
            R.id.nav_consultar_equipos -> R.id.nav_dashboard

            R.id.nav_qr,
            R.id.nav_qr_historial,
            R.id.nav_qr_buscar_equipo,
            R.id.nav_qr_ultimos_escaneos -> R.id.nav_qr

            R.id.nav_ordenes,
            R.id.nav_ordenes_evidencias -> R.id.nav_ordenes

            R.id.nav_perfil,
            R.id.nav_configuracion -> R.id.nav_perfil
            else -> null
        }
    }

    private fun resolveToolbarTitle(destinationId: Int): String {
        return when (destinationId) {
            R.id.nav_dashboard,
            R.id.nav_resumen -> "Inicio"
            R.id.nav_actividad_reciente -> "Actividad reciente"
            R.id.nav_notificaciones -> "Notificaciones"
            R.id.nav_centro_acciones -> "Centro de acciones"
            R.id.nav_consultar_equipos -> "Consultar equipos"
            R.id.nav_qr -> "QR"
            R.id.nav_qr_historial -> "Historial QR"
            R.id.nav_qr_buscar_equipo -> "Buscar equipo"
            R.id.nav_qr_ultimos_escaneos -> "Ultimos escaneos"
            R.id.nav_ordenes -> "Ordenes"
            R.id.nav_ordenes_evidencias -> "Evidencias"
            R.id.nav_perfil -> "Perfil"
            R.id.nav_configuracion -> "Configuracion"
            else -> "Ordenes"
        }
    }

    private fun currentOrdenesFilter(arguments: Bundle?): String {
        return arguments?.getString("filtro") ?: "todas"
    }

    private fun confirmLogout() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Cerrar sesion")
            .setMessage("¿Quieres cerrar la sesion actual en MaintainQR?")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Cerrar sesion") { _, _ -> logout() }
            .show()
    }

    private fun closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfig) || super.onSupportNavigateUp()
    }

    private fun logout() {
        lifecycleScope.launch {
            sessionManager.clearSession()
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }
    }
}
