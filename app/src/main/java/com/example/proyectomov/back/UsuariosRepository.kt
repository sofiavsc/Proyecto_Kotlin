package com.example.proyectomov.back

import com.example.proyectomov.back.api.FakeStoreApiService
import com.example.proyectomov.back.api.CreateUserRequestDto
import com.example.proyectomov.back.api.CreateUserResponseDto
import com.example.proyectomov.back.api.UserAddressDto
import com.example.proyectomov.back.api.UserDto
import com.example.proyectomov.back.api.UserNameDto
import com.example.proyectomov.back.api.UserUpsertDto

class UsuariosRepository(
    private val api: FakeStoreApiService,
) {
    suspend fun obtenerUsuarios(): Result<List<UsuarioOutlet>> = runCatching {
        api.getUsers().map { it.toUsuarioOutlet() }
    }

    suspend fun obtenerUsuario(id: Int): Result<UsuarioOutlet> = runCatching {
        api.getUser(id).toUsuarioOutlet()
    }

    suspend fun crearUsuario(
        id: Int,
        username: String,
        email: String,
        password: String,
    ): Result<UsuarioOutlet> = runCatching {
        api.createUser(
            CreateUserRequestDto(
                id = id,
                username = username,
                email = email,
                password = password,
            ),
        ).toUsuarioOutletDesdeCreado(
            idSolicitado = id,
            usernameSolicitado = username,
            emailSolicitado = email,
        )
    }

    suspend fun actualizarUsuario(
        id: Int,
        email: String,
        username: String,
        password: String,
        firstname: String,
        lastname: String,
        city: String,
        street: String,
        number: Int,
        zipcode: String,
        phone: String,
    ): Result<UsuarioOutlet> = runCatching {
        api.updateUser(
            id = id,
            body = UserUpsertDto(
                email = email,
                username = username,
                password = password,
                name = UserNameDto(firstname = firstname, lastname = lastname),
                address = UserAddressDto(city = city, street = street, number = number, zipcode = zipcode),
                phone = phone,
            ),
        ).toUsuarioOutlet()
    }

    suspend fun eliminarUsuario(id: Int): Result<UsuarioOutlet> = runCatching {
        api.deleteUser(id).toUsuarioOutlet()
    }
}

private fun CreateUserResponseDto.toUsuarioOutletDesdeCreado(
    idSolicitado: Int,
    usernameSolicitado: String,
    emailSolicitado: String,
): UsuarioOutlet {
    val idFinal = id ?: idSolicitado
    val usernameFinal = username?.ifBlank { null } ?: usernameSolicitado
    val emailFinal = email?.ifBlank { null } ?: emailSolicitado
    return UsuarioOutlet(
        id = idFinal,
        email = emailFinal,
        username = usernameFinal,
        nombreCompleto = usernameFinal,
        ciudad = "",
        telefono = "",
    )
}

private fun UserDto.toUsuarioOutlet(): UsuarioOutlet {
    val nombreCompleto = listOfNotNull(name?.firstname, name?.lastname)
        .joinToString(" ")
        .ifBlank { username }
    return UsuarioOutlet(
        id = id,
        email = email,
        username = username,
        nombreCompleto = nombreCompleto,
        ciudad = address?.city ?: "",
        telefono = phone ?: "",
    )
}
