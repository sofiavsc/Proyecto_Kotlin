package com.example.proyectomov.back.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favoritos_producto")
data class FavoritoProductoOutletEntity(
    @PrimaryKey val idMostrar: String,
)
