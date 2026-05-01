package com.example.proyectomov.back.local

import kotlinx.serialization.Serializable

@Serializable
data class UsuarioCuentaStored(
    val id: Int,
    val username: String,
    val email: String,
    val passwordSha256Hex: String,
)
