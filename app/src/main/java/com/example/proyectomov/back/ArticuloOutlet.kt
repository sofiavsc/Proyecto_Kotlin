package com.example.proyectomov.back

/**
 * Artículo mostrado en el inicio y al abrir detalle.
 */
data class ArticuloOutlet(
    val idMostrar: String,
    val imagenResId: Int,
    val titulo: String,
    val precioPesosEntero: Int,
    val categoria: String,
    val descripcion: String,
    val calificacionEstrellas: Float,
)
