package com.example.proyectomov.back

import android.content.Context
import androidx.annotation.StringRes
import com.example.proyectomov.R
import com.example.proyectomov.back.local.contextoLocalizadoApp
import com.example.proyectomov.back.local.PasswordHasher
import com.example.proyectomov.back.local.UsuarioCuentaStored
import com.example.proyectomov.back.local.UsuarioCuentasDataStore
import kotlinx.coroutines.flow.Flow

class UsuarioCuentasRepository(
    context: Context,
) {
    private val appContext = context.applicationContext
    private val dataStore = UsuarioCuentasDataStore(context)

    private fun texto(@StringRes id: Int): String =
        appContext.contextoLocalizadoApp().getString(id)

    suspend fun registrar(
        username: String,
        email: String,
        password: String,
    ): Result<UsuarioOutlet> = runCatching {
        val emailNorm = email.trim().lowercase()
        val usuarioNorm = username.trim()
        if (usuarioNorm.isBlank()) {
            throw IllegalStateException(texto(R.string.repo_err_username_empty))
        }
        if (!emailNorm.contains("@")) {
            throw IllegalStateException(texto(R.string.repo_err_email_invalid))
        }

        val actuales = dataStore.leerTodos()
        if (actuales.any { it.email.equals(emailNorm, ignoreCase = true) }) {
            throw IllegalStateException(texto(R.string.repo_err_email_taken))
        }
        if (actuales.any { it.username.equals(usuarioNorm, ignoreCase = true) }) {
            throw IllegalStateException(texto(R.string.repo_err_username_taken))
        }

        val id = (actuales.maxOfOrNull { it.id } ?: 0) + 1
        val nuevo = UsuarioCuentaStored(
            id = id,
            username = usuarioNorm,
            email = emailNorm,
            passwordSha256Hex = PasswordHasher.sha256Hex(password),
        )
        dataStore.guardarTodos(actuales + nuevo)
        nuevo.aUsuarioOutlet()
    }

    suspend fun iniciarSesion(
        correo: String,
        contrasena: String,
    ): Result<UsuarioOutlet> = runCatching {
        val correoNorm = correo.trim().lowercase()
        if (correoNorm.isBlank() || contrasena.isBlank()) {
            throw IllegalStateException(texto(R.string.repo_err_fill_login))
        }

        val usuarios = dataStore.leerTodos()
        val hash = PasswordHasher.sha256Hex(contrasena)
        val encontrado = usuarios.firstOrNull {
            it.email.equals(correoNorm, ignoreCase = true) &&
                it.passwordSha256Hex == hash
        } ?: throw IllegalStateException(texto(R.string.repo_err_bad_credentials))

        encontrado.aUsuarioOutlet()
    }

    fun sesionUsuarioIdFlow(): Flow<Int?> = dataStore.flujoSesionUsuarioId()

    suspend fun obtenerSesionUsuarioId(): Int? = dataStore.leerSesionUsuarioId()

    suspend fun guardarSesionUsuarioId(id: Int?) {
        dataStore.guardarSesionUsuarioId(id)
    }

    suspend fun obtenerCuentaPorId(id: Int): Result<UsuarioCuentaStored> = runCatching {
        dataStore.leerTodos().firstOrNull { it.id == id }
            ?: error(texto(R.string.repo_err_account_not_found))
    }

    suspend fun actualizarUsername(
        id: Int,
        nuevoUsername: String,
    ): Result<UsuarioCuentaStored> = runCatching {
        val usuarioNorm = nuevoUsername.trim()
        if (usuarioNorm.isBlank()) {
            throw IllegalStateException(texto(R.string.repo_err_username_empty))
        }
        val actuales = dataStore.leerTodos()
        val idx = actuales.indexOfFirst { it.id == id }
        if (idx < 0) {
            throw IllegalStateException(texto(R.string.repo_err_account_not_found))
        }
        if (actuales.any { it.id != id && it.username.equals(usuarioNorm, ignoreCase = true) }) {
            throw IllegalStateException(texto(R.string.repo_err_username_taken))
        }
        val actualizado = actuales[idx].copy(username = usuarioNorm)
        val nuevaLista = actuales.toMutableList().apply { this[idx] = actualizado }
        dataStore.guardarTodos(nuevaLista)
        actualizado
    }

    suspend fun actualizarEmail(
        id: Int,
        nuevoEmail: String,
    ): Result<UsuarioCuentaStored> = runCatching {
        val emailNorm = nuevoEmail.trim().lowercase()
        if (!emailNorm.contains("@")) {
            throw IllegalStateException(texto(R.string.repo_err_email_invalid))
        }
        val actuales = dataStore.leerTodos()
        val idx = actuales.indexOfFirst { it.id == id }
        if (idx < 0) {
            throw IllegalStateException(texto(R.string.repo_err_account_not_found))
        }
        if (actuales.any { it.id != id && it.email.equals(emailNorm, ignoreCase = true) }) {
            throw IllegalStateException(texto(R.string.repo_err_email_taken))
        }
        val actualizado = actuales[idx].copy(email = emailNorm)
        val nuevaLista = actuales.toMutableList().apply { this[idx] = actualizado }
        dataStore.guardarTodos(nuevaLista)
        actualizado
    }

    suspend fun actualizarContrasena(
        id: Int,
        contrasenaActual: String,
        contrasenaNueva: String,
    ): Result<Unit> = runCatching {
        if (contrasenaNueva.isBlank()) {
            throw IllegalStateException(texto(R.string.repo_err_new_password_empty))
        }
        val actuales = dataStore.leerTodos()
        val idx = actuales.indexOfFirst { it.id == id }
        if (idx < 0) {
            throw IllegalStateException(texto(R.string.repo_err_account_not_found))
        }
        val viejo = actuales[idx]
        val hashActual = PasswordHasher.sha256Hex(contrasenaActual)
        if (viejo.passwordSha256Hex != hashActual) {
            throw IllegalStateException(texto(R.string.repo_err_wrong_current_password))
        }
        val actualizado =
            viejo.copy(passwordSha256Hex = PasswordHasher.sha256Hex(contrasenaNueva))
        val nuevaLista = actuales.toMutableList().apply { this[idx] = actualizado }
        dataStore.guardarTodos(nuevaLista)
    }
}

private fun UsuarioCuentaStored.aUsuarioOutlet(): UsuarioOutlet =
    UsuarioOutlet(
        id = id,
        email = email,
        username = username,
        nombreCompleto = username,
        ciudad = "",
        telefono = "",
    )
