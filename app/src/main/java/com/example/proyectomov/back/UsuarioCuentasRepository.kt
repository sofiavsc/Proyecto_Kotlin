package com.example.proyectomov.back

import android.content.Context
import com.example.proyectomov.back.local.PasswordHasher
import com.example.proyectomov.back.local.UsuarioCuentaStored
import com.example.proyectomov.back.local.UsuarioCuentasDataStore

class UsuarioCuentasRepository(
    context: Context,
) {
    private val dataStore = UsuarioCuentasDataStore(context)

    suspend fun registrar(
        username: String,
        email: String,
        password: String,
    ): Result<UsuarioOutlet> = runCatching {
        val emailNorm = email.trim().lowercase()
        val usuarioNorm = username.trim()
        if (usuarioNorm.isBlank()) throw IllegalStateException("Escribe un username.")
        if (!emailNorm.contains("@")) throw IllegalStateException("Correo no valido.")

        val actuales = dataStore.leerTodos()
        if (actuales.any { it.email.equals(emailNorm, ignoreCase = true) }) {
            throw IllegalStateException("Ese correo ya esta registrado.")
        }
        if (actuales.any { it.username.equals(usuarioNorm, ignoreCase = true) }) {
            throw IllegalStateException("Ese username ya esta en uso.")
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
            throw IllegalStateException("Completa correo y contraseña.")
        }

        val usuarios = dataStore.leerTodos()
        val hash = PasswordHasher.sha256Hex(contrasena)
        val encontrado = usuarios.firstOrNull {
            it.email.equals(correoNorm, ignoreCase = true) &&
                it.passwordSha256Hex == hash
        } ?: throw IllegalStateException("Correo o contraseña incorrectos.")

        encontrado.aUsuarioOutlet()
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
