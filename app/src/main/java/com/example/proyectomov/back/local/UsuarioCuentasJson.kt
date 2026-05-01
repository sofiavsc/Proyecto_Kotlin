package com.example.proyectomov.back.local

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

private val listSerializer = ListSerializer(UsuarioCuentaStored.serializer())

internal object UsuarioCuentasJson {
    fun encode(lista: List<UsuarioCuentaStored>): String =
        json.encodeToString(listSerializer, lista)

    fun decode(raw: String): List<UsuarioCuentaStored> =
        json.decodeFromString(listSerializer, raw)
}
