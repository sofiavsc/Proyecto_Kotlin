package com.example.proyectomov.back

data class CarritoOutlet(
    val id: Int,
    val userId: Int,
    val fecha: String,
    val productos: List<ItemCarritoOutlet>,
)

data class ItemCarritoOutlet(
    val productId: Int,
    val cantidad: Int,
)
