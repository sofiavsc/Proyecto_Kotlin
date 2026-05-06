package com.example.proyectomov.back

data class ArticuloOutlet(
    val idMostrar: String,
    val imagenUrl: String,
    val titulo: String,
    val precioPesosEntero: Int,
    val categoria: String,
    val descripcion: String,
    val calificacionEstrellas: Float,
)
