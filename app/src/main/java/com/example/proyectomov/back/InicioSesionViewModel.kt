package com.example.proyectomov.back

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.text.isNotBlank

/**
 * Valida acceso .
 */
class InicioSesionViewModel : ViewModel() {
    var procesando by mutableStateOf(false)
        private set

    var mensajeError by mutableStateOf("")
        private set

    fun intentarEntrar(correo: String, contrasena: String, alTerminar: (exito: Boolean) -> Unit) {
        mensajeError = ""
        procesando = false
        val correoOk = correo.isNotBlank()
        val passOk = contrasena.isNotBlank()
        if (!correoOk || !passOk) {
            mensajeError = "Completa correo y contraseña"
            alTerminar(false)
        } else {
            alTerminar(true)
        }
    }
}
