package com.example.proyectomov.back

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * Validación contra cuentas guardadas en DataStore.
 */
class InicioSesionViewModel(
    application: Application,
    private val cuentasRepository: UsuarioCuentasRepository =
        UsuarioCuentasRepository(application.applicationContext),
) : AndroidViewModel(application) {
    var procesando by mutableStateOf(false)
        private set

    var mensajeError by mutableStateOf("")
        private set

    var tokenSesion by mutableStateOf("")
        private set

    /** Usuario tras login correcto (id local estable). */
    var usuarioSesionId by mutableStateOf<Int?>(null)
        private set

    fun intentarEntrar(correo: String, contrasena: String, alTerminar: (exito: Boolean) -> Unit = {}) {
        mensajeError = ""
        val correoOk = correo.isNotBlank()
        val passOk = contrasena.isNotBlank()
        if (!correoOk || !passOk) {
            mensajeError = "Completa correo y contraseña"
            alTerminar(false)
        } else {
            viewModelScope.launch {
                procesando = true
                val resultado = cuentasRepository.iniciarSesion(
                    correo = correo,
                    contrasena = contrasena,
                )
                procesando = false
                if (resultado.isSuccess) {
                    val u = resultado.getOrThrow()
                    usuarioSesionId = u.id
                    tokenSesion = "local-${u.id}"
                    alTerminar(true)
                } else {
                    mensajeError =
                        resultado.exceptionOrNull()?.localizedMessage?.ifBlank { null }
                            ?: "No se pudo iniciar sesion."
                    usuarioSesionId = null
                    tokenSesion = ""
                    alTerminar(false)
                }
            }
        }
    }
}
