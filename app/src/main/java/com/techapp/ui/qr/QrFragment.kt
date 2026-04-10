package com.techapp.ui.qr

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.techapp.api.RetrofitClient
import com.techapp.databinding.FragmentQrBinding
import com.techapp.ui.mock.MockMaintainQrData
import com.techapp.utils.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class QrFragment : Fragment() {

    private var _binding: FragmentQrBinding? = null
    private val binding get() = _binding!!
    private var isFlashOn = false

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        val message = if (granted) {
            "Vista QR lista. Backend del escaner se conecta despues."
        } else {
            "Puedes seguir usando la busqueda manual aunque la camara este apagada"
        }
        showMessage(message)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkCameraPermission()
        binding.btnFlash.isEnabled = requireContext().packageManager
            .hasSystemFeature(android.content.pm.PackageManager.FEATURE_CAMERA_FLASH)

        binding.btnManual.setOnClickListener {
            val serie = binding.etSerieManual.text?.toString()?.trim().orEmpty()
            val orden = if (serie.isBlank()) {
                MockMaintainQrData.ordenes.firstOrNull()
            } else {
                MockMaintainQrData.buscarPorSerie(serie)
            }

            if (orden != null) {
                MockMaintainQrData.registrarQrConsulta(orden, if (serie.isBlank()) "QR" else "Manual")
                val action = QrFragmentDirections.actionQrFragmentToOrdenDetalleFragment(orden.id)
                findNavController().navigate(action)
            } else {
                showMessage("Serie no encontrada en la maqueta frontend")
            }
        }

        binding.barcodeView.decodeContinuous { result ->
            val qrToken = result.text

            requireActivity().runOnUiThread {
                if (!qrToken.isNullOrEmpty()) {
                    binding.barcodeView.pause()
                    showMessage("QR detectado")
                    navegarADetalle(qrToken)
                }
            }
        }

        binding.btnFlash.setOnClickListener {
            toggleFlash()
        }
        updateFlashUi()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showMessage("Escaner visual listo para integrar")
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun navegarADetalle(qrToken: String) {
        lifecycleScope.launch {
            try {
                val sessionManager = SessionManager(requireContext())
                val api = RetrofitClient.apiService

                val token = sessionManager.token.first() ?: return@launch

                val response = api.getOrdenPorQR("Bearer $token", qrToken)

                if (response.isSuccessful && response.body()?.success == true) {
                    val ordenId = response.body()?.orden?.id

                    if (ordenId != null) {
                        val action = QrFragmentDirections
                            .actionQrFragmentToOrdenDetalleFragment(ordenId)

                        findNavController().navigate(action)
                    }
                } else {
                    showMessage("Orden no encontrada o ya asignada")
                    binding.barcodeView.resume()
                }
            } catch (e: Exception) {
                showMessage("Error: ${e.localizedMessage}")
                binding.barcodeView.resume()
            }
        }
    }

    private fun showMessage(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
    }

    private fun toggleFlash() {
        val hasFlash = requireContext().packageManager
            .hasSystemFeature(android.content.pm.PackageManager.FEATURE_CAMERA_FLASH)

        if (!hasFlash) {
            showMessage("Este dispositivo no reporta soporte de flash")
            return
        }

        runCatching {
            if (isFlashOn) {
                binding.barcodeView.setTorchOff()
            } else {
                binding.barcodeView.setTorchOn()
            }
            isFlashOn = !isFlashOn
            updateFlashUi()
        }.onFailure {
            showMessage("No fue posible cambiar el estado del flash")
        }
    }

    private fun updateFlashUi() {
        binding.btnFlash.text = if (isFlashOn) "Flash ON" else "Flash OFF"
        binding.btnFlash.alpha = if (binding.btnFlash.isEnabled) 1f else 0.7f
    }

    override fun onResume() {
        super.onResume()
        binding.barcodeView.resume()
    }

    override fun onPause() {
        if (isFlashOn) {
            runCatching { binding.barcodeView.setTorchOff() }
            isFlashOn = false
            updateFlashUi()
        }
        binding.barcodeView.pause()
        super.onPause()
    }

    override fun onDestroyView() {
        binding.barcodeView.pause()
        super.onDestroyView()
        _binding = null
    }
}
