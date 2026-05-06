package com.example.proyectomov.back

import com.example.proyectomov.back.api.CartDto
import com.example.proyectomov.back.api.CartProductDto
import com.example.proyectomov.back.api.CartUpsertDto
import com.example.proyectomov.back.api.FakeStoreApiService

class CarritoRepository(
    private val api: FakeStoreApiService,
) {
    suspend fun crearCarrito(userId: Int, fecha: String, productos: List<ItemCarritoOutlet>): Result<CarritoOutlet> =
        runCatching {
            api.createCart(
                CartUpsertDto(
                    userId = userId,
                    date = fecha,
                    products = productos.map { CartProductDto(productId = it.productId, quantity = it.cantidad) },
                ),
            ).toCarritoOutlet()
        }

    suspend fun actualizarCarrito(
        id: Int,
        userId: Int,
        fecha: String,
        productos: List<ItemCarritoOutlet>,
    ): Result<CarritoOutlet> = runCatching {
        api.updateCart(
            id = id,
            body = CartUpsertDto(
                userId = userId,
                date = fecha,
                products = productos.map { CartProductDto(productId = it.productId, quantity = it.cantidad) },
            ),
        ).toCarritoOutlet()
    }
}

private fun CartDto.toCarritoOutlet(): CarritoOutlet {
    return CarritoOutlet(
        id = id,
        userId = userId,
        fecha = date,
        productos = products.map { ItemCarritoOutlet(productId = it.productId, cantidad = it.quantity) },
    )
}
