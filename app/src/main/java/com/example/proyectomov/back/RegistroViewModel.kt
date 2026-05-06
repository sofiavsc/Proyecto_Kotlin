package com.example.proyectomov.back

import android.app.Application
import com.example.proyectomov.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RegistroViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val cuentasRepository = UsuarioCuentasRepository(application.applicationContext)
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

        val app = getApplication<Application>()
        mensajeError = when {
            username.isBlank() -> app.getString(R.string.err_username_empty)
            !correoValido -> app.getString(R.string.err_email_invalid)
            !passValida -> app.getString(R.string.err_password_short)
            !passIguales -> app.getString(R.string.err_passwords_mismatch)
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
                    ?: getApplication<Application>().getString(R.string.err_register_failed)
                alTerminar(false)
            }
        }
    }
}
