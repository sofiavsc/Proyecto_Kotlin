package com.example.proyectomov.back

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.text.contains

/**
 * Simula una espera de red al entrar (corrutinas como en clase).
 */
class InicioSesionViewModel : ViewModel() {
    var procesando by mutableStateOf(false)
        private set

    var mensajeError by mutableStateOf("")
        private set

    fun intentarEntrar(correo: String, contrasena: String, alTerminar: (exito: Boolean) -> Unit) {
        viewModelScope.launch {
            procesando = true
            mensajeError = ""
            withContext(Dispatchers.IO) {
                delay(700)
            }
            procesando = false
            val correoOk = correo.contains("@") && correo.length > 5
            val passOk = contrasena.length >= 3
            if (!correoOk || !passOk) {
                mensajeError = "Correo o contraseña no válidos"
                alTerminar(false)
            } else {
                alTerminar(true)
            }
        }
    }
}
