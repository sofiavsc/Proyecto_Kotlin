package com.example.proyectomov.back.local.room

import com.example.proyectomov.back.ArticuloOutlet

internal fun ArticuloOutlet.aEntityCache(epochMs: Long = System.currentTimeMillis()): ProductoCatalogoOutletEntity =
    ProductoCatalogoOutletEntity(
        remoteId = idRemotoEstimado(),
        idMostrar = idMostrar,
        imagenUrl = imagenUrl,
        titulo = titulo,
        precioPesosEntero = precioPesosEntero,
        categoria = categoria,
        descripcion = descripcion,
        calificacionEstrellas = calificacionEstrellas,
        guardadoEnEpochMs = epochMs,
    )

internal fun ProductoCatalogoOutletEntity.aArticuloOutlet(): ArticuloOutlet =
    ArticuloOutlet(
        idMostrar = idMostrar,
        imagenUrl = imagenUrl,
        titulo = titulo,
        precioPesosEntero = precioPesosEntero,
        categoria = categoria,
        descripcion = descripcion,
        calificacionEstrellas = calificacionEstrellas,
    )

/**
 * El API usa [ProductDto.id] numérico mapeado a [ArticuloOutlet.idMostrar] con ceros a la izquierda.
 */
private fun ArticuloOutlet.idRemotoEstimado(): Int {
    val sinCeros = idMostrar.trimStart('0').ifEmpty { "0" }
    return sinCeros.toIntOrNull() ?: idMostrar.hashCode()
}

internal fun ProductoPublicadoVenderOutletEntity.aArticuloOutlet(): ArticuloOutlet =
    ArticuloOutlet(
        idMostrar = idMostrar,
        imagenUrl = imagenUrl,
        titulo = titulo,
        precioPesosEntero = precioPesosEntero,
        categoria = categoria,
        descripcion = descripcion,
        calificacionEstrellas = calificacionEstrellas,
    )
