package com.techapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.techapp.databinding.ActivityLoginBinding
import com.techapp.ui.MainActivity
import com.techapp.utils.Resource

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verificar sesión activa
        viewModel.checkSession { hasSession ->
            if (hasSession) navigateToMain()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etUsuario.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showError("Por favor completa todos los campos")
                return@setOnClickListener
            }
            viewModel.login(email, password)
        }

        observeLogin()
    }

    private fun observeLogin() {
        viewModel.loginState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.btnLogin.isEnabled = false
                    binding.progressLogin.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressLogin.visibility = View.GONE
                    navigateToMain()
                }
                is Resource.Error -> {
                    binding.progressLogin.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    showError(resource.message ?: "Error al iniciar sesión")
                }
            }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showError(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
    }
}
