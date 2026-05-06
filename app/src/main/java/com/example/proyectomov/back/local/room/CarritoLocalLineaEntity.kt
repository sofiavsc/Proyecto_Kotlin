package com.example.proyectomov.back.local.room

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "carrito_local_linea",
    primaryKeys = ["usuario_id", "product_id"],
)
data class CarritoLocalLineaEntity(
    @ColumnInfo(name = "usuario_id") val usuarioId: Int,
    @ColumnInfo(name = "product_id") val productId: Int,
    val cantidad: Int,
)
