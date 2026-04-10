package com.techapp.ui.orders

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.techapp.databinding.FragmentRegistroEvidenciaBinding
import com.techapp.models.RefaccionRegistroItem
import com.techapp.ui.mock.MockMaintainQrData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class RegistroEvidenciaFragment : Fragment() {

    private var _binding: FragmentRegistroEvidenciaBinding? = null
    private val binding get() = _binding!!
    private val args: RegistroEvidenciaFragmentArgs by navArgs()

    private val evidenciaUris = mutableListOf<String>()
    private val refaccionesTemporales = mutableListOf<RefaccionRegistroItem>()

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            lifecycleScope.launch {
                val uri = withContext(Dispatchers.IO) { saveBitmapToCache(bitmap) }
                if (uri != null) {
                    evidenciaUris.add(uri)
                    renderPreviews()
                } else {
                    showMessage("No fue posible guardar la foto tomada")
                }
            }
        }
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            evidenciaUris.addAll(uris.map(Uri::toString))
            renderPreviews()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistroEvidenciaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActions()
        renderPreviews()
        renderRefacciones()
    }

    private fun setupActions() {
        binding.btnAgregarFotoCamara.setOnClickListener { cameraLauncher.launch(null) }
        binding.btnAgregarFotoGaleria.setOnClickListener { galleryLauncher.launch("image/*") }

        binding.switchRefaccion.setOnCheckedChangeListener { _, isChecked ->
            binding.cardRefaccionesRegistro.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (!isChecked) {
                refaccionesTemporales.clear()
                clearRefaccionInputs()
                renderRefacciones()
            }
        }

        binding.btnAgregarRefaccionRegistro.setOnClickListener {
            val nombre = binding.etNombreRefaccionRegistro.text?.toString()?.trim().orEmpty()
            val cantidadText = binding.etCantidadRefaccionRegistro.text?.toString()?.trim().orEmpty()
            val observacion = binding.etObservacionRefaccionRegistro.text?.toString()?.trim().orEmpty()

            when {
                nombre.isBlank() -> binding.etNombreRefaccionRegistro.error = "Requerido"
                cantidadText.isBlank() -> binding.etCantidadRefaccionRegistro.error = "Requerido"
                cantidadText.toIntOrNull() == null || cantidadText.toInt() <= 0 ->
                    binding.etCantidadRefaccionRegistro.error = "Cantidad invalida"
                else -> {
                    refaccionesTemporales.add(
                        RefaccionRegistroItem(
                            id = 0,
                            nombre = nombre,
                            cantidad = cantidadText.toInt(),
                            observacion = observacion.ifBlank { null }
                        )
                    )
                    clearRefaccionInputs()
                    renderRefacciones()
                }
            }
        }

        binding.etComentarioRegistro.doAfterTextChanged {
            binding.etComentarioRegistro.error = null
        }

        binding.btnGuardarRegistro.setOnClickListener { guardarRegistro() }
    }

    private fun guardarRegistro() {
        val comentario = binding.etComentarioRegistro.text?.toString()?.trim().orEmpty()
        if (comentario.isBlank()) {
            binding.etComentarioRegistro.error = "Comentario obligatorio"
            return
        }
        if (evidenciaUris.isEmpty()) {
            showMessage("Agrega al menos una foto para el registro")
            return
        }
        if (binding.switchRefaccion.isChecked && refaccionesTemporales.isEmpty()) {
            showMessage("Agrega al menos una refaccion o desactiva la opcion")
            return
        }

        MockMaintainQrData.crearRegistroTecnico(
            ordenId = args.ordenId,
            comentario = comentario,
            evidenciaUris = evidenciaUris.toList(),
            refacciones = refaccionesTemporales.toList()
        )

        findNavController().previousBackStackEntry
            ?.savedStateHandle
            ?.set("refresh", true)
        findNavController().popBackStack()
    }


    private fun renderPreviews() {
        binding.tvResumenFotos.text = if (evidenciaUris.isEmpty()) {
            "No hay fotos seleccionadas"
        } else {
            "${evidenciaUris.size} foto(s) listas para guardar"
        }

        val previewViews = listOf(
            binding.ivPreviewRegistro1,
            binding.ivPreviewRegistro2,
            binding.ivPreviewRegistro3
        )

        previewViews.forEachIndexed { index, imageView ->
            val uri = evidenciaUris.getOrNull(index)
            if (uri == null) {
                imageView.visibility = View.GONE
                imageView.setImageDrawable(null)
            } else {
                imageView.visibility = View.VISIBLE
                loadPreview(imageView, uri)
            }
        }
    }

    private fun renderRefacciones() {
        binding.tvRefaccionesRegistradas.text = if (refaccionesTemporales.isEmpty()) {
            "Sin refacciones capturadas"
        } else {
            refaccionesTemporales.joinToString("\n") { refaccion ->
                val observacion = refaccion.observacion?.takeIf { it.isNotBlank() }?.let {
                    " • $it"
                }.orEmpty()
                "${refaccion.nombre} x${refaccion.cantidad}$observacion"
            }
        }
    }

    private fun clearRefaccionInputs() {
        binding.etNombreRefaccionRegistro.setText("")
        binding.etCantidadRefaccionRegistro.setText("")
        binding.etObservacionRefaccionRegistro.setText("")
        binding.etNombreRefaccionRegistro.error = null
        binding.etCantidadRefaccionRegistro.error = null
    }

    private fun loadPreview(view: ImageView, uri: String) {
        Glide.with(view)
            .load(Uri.parse(uri))
            .centerCrop()
            .into(view)
    }

    private fun saveBitmapToCache(bitmap: Bitmap): String? {
        return runCatching {
            val file = File(requireContext().cacheDir, "registro_${UUID.randomUUID()}.jpg")
            FileOutputStream(file).use { output ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)
            }
            Uri.fromFile(file).toString()
        }.getOrNull()
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
//nada
