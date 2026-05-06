package com.example.proyectomov.back.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos_catalogo_cache")
data class ProductoCatalogoOutletEntity(
    @PrimaryKey val remoteId: Int,
    val idMostrar: String,
    val imagenUrl: String,
    val titulo: String,
    val precioPesosEntero: Int,
    val categoria: String,
    val descripcion: String,
    val calificacionEstrellas: Float,
    val guardadoEnEpochMs: Long,
)
