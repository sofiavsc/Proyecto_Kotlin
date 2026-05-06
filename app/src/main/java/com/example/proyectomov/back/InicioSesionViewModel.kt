package com.example.proyectomov.back

import android.app.Application
import com.example.proyectomov.R
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
) : AndroidViewModel(application) {
    private val cuentasRepository = UsuarioCuentasRepository(application.applicationContext)
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
            mensajeError = getApplication<Application>().getString(R.string.err_fill_email_password)
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
                    cuentasRepository.guardarSesionUsuarioId(u.id)
                    alTerminar(true)
                } else {
                    mensajeError =
                        resultado.exceptionOrNull()?.localizedMessage?.ifBlank { null }
                            ?: getApplication<Application>().getString(R.string.err_login_generic)
                    usuarioSesionId = null
                    tokenSesion = ""
                    alTerminar(false)
                }
            }
        }
    }
}
