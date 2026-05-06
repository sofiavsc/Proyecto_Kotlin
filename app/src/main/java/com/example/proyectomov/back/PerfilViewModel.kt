package com.example.proyectomov.back

import android.app.Application
import androidx.annotation.StringRes
import com.example.proyectomov.R
import com.example.proyectomov.back.local.contextoLocalizadoApp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PerfilViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val cuentasRepository = UsuarioCuentasRepository(application.applicationContext)

    private fun texto(@StringRes id: Int): String =
        getApplication<Application>().applicationContext.contextoLocalizadoApp().getString(id)

    var cargando by mutableStateOf(true)
        private set

    var errorCarga by mutableStateOf("")
        private set

    var username by mutableStateOf("")
        private set

    var email by mutableStateOf("")
        private set

    private var usuarioId: Int? = null

    var guardando by mutableStateOf(false)
        private set

    init {
        refrescar()
    }

    fun refrescar() {
        viewModelScope.launch {
            cargando = true
            errorCarga = ""
            val id = cuentasRepository.obtenerSesionUsuarioId()
            if (id == null) {
                usuarioId = null
                username = ""
                email = ""
                errorCarga = texto(R.string.err_sign_in_for_profile)
                cargando = false
                return@launch
            }
            val resultado = cuentasRepository.obtenerCuentaPorId(id)
            cargando = false
            if (resultado.isSuccess) {
                val c = resultado.getOrThrow()
                usuarioId = c.id
                username = c.username
                email = c.email
            } else {
                usuarioId = null
                username = ""
                email = ""
                errorCarga =
                    resultado.exceptionOrNull()?.localizedMessage
                        ?: texto(R.string.err_load_profile)
            }
        }
    }

    fun guardarUsername(
        valor: String,
        alTerminar: (exito: Boolean, mensaje: String) -> Unit,
    ) {
        val id = usuarioId
        if (id == null) {
            alTerminar(false, texto(R.string.err_no_active_session))
            return
        }
        viewModelScope.launch {
            guardando = true
            val r = cuentasRepository.actualizarUsername(id, valor)
            guardando = false
            if (r.isSuccess) {
                username = r.getOrThrow().username
                alTerminar(true, texto(R.string.msg_username_saved))
            } else {
                alTerminar(
                    false,
                    r.exceptionOrNull()?.localizedMessage
                        ?: texto(R.string.err_save_failed),
                )
            }
        }
    }

    fun guardarEmail(
        valor: String,
        alTerminar: (exito: Boolean, mensaje: String) -> Unit,
    ) {
        val id = usuarioId
        if (id == null) {
            alTerminar(false, texto(R.string.err_no_active_session))
            return
        }
        viewModelScope.launch {
            guardando = true
            val r = cuentasRepository.actualizarEmail(id, valor)
            guardando = false
            if (r.isSuccess) {
                email = r.getOrThrow().email
                alTerminar(true, texto(R.string.msg_email_saved))
            } else {
                alTerminar(
                    false,
                    r.exceptionOrNull()?.localizedMessage
                        ?: texto(R.string.err_save_failed),
                )
            }
        }
    }

    fun guardarContrasena(
        actual: String,
        nueva: String,
        alTerminar: (exito: Boolean, mensaje: String) -> Unit,
    ) {
        val id = usuarioId
        if (id == null) {
            alTerminar(false, texto(R.string.err_no_active_session))
            return
        }
        viewModelScope.launch {
            guardando = true
            val r = cuentasRepository.actualizarContrasena(id, actual, nueva)
            guardando = false
            if (r.isSuccess) {
                alTerminar(true, texto(R.string.msg_password_saved))
            } else {
                alTerminar(
                    false,
                    r.exceptionOrNull()?.localizedMessage
                        ?: texto(R.string.err_save_failed),
                )
            }
        }
    }
}
