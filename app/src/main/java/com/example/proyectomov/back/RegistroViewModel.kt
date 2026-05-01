package com.example.proyectomov.back

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RegistroViewModel(
    application: Application,
    private val cuentasRepository: UsuarioCuentasRepository =
        UsuarioCuentasRepository(application.applicationContext),
) : AndroidViewModel(application) {
    var procesando by mutableStateOf(false)
        private set

    var mensajeError by mutableStateOf("")
        private set

    fun registrar(
        username: String,
        correo: String,
        contrasena: String,
        confirmarContrasena: String,
        alTerminar: (Boolean) -> Unit = {},
    ) {
        mensajeError = ""
        val correoValido = correo.contains("@") && correo.length > 5
        val passValida = contrasena.length >= 3
        val passIguales = contrasena == confirmarContrasena

        mensajeError = when {
            username.isBlank() -> "Escribe un username."
            !correoValido -> "Correo no valido."
            !passValida -> "La contrasena debe tener al menos 3 caracteres."
            !passIguales -> "Las contrasenas no coinciden."
            else -> ""
        }

        if (mensajeError.isNotBlank()) {
            alTerminar(false)
            return
        }

        viewModelScope.launch {
            procesando = true
            val resultado = cuentasRepository.registrar(
                username = username,
                email = correo.trim(),
                password = contrasena,
            )
            procesando = false
            if (resultado.isSuccess) {
                alTerminar(true)
            } else {
                mensajeError = resultado.exceptionOrNull()?.localizedMessage?.ifBlank { null }
                    ?: "No se pudo registrar."
                alTerminar(false)
            }
        }
    }
}
