package com.example.proyectomov.back.local

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

internal object PasswordHasher {
    fun sha256Hex(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(password.toByteArray(StandardCharsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }
}
