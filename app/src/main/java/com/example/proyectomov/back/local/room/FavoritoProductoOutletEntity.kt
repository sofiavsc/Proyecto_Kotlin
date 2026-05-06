package com.example.proyectomov.back.local.room

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "favoritos_producto",
    primaryKeys = ["usuario_id", "idMostrar"],
)
data class FavoritoProductoOutletEntity(
    @ColumnInfo(name = "usuario_id") val usuarioId: Int,
    @ColumnInfo(name = "idMostrar") val idMostrar: String,
)
