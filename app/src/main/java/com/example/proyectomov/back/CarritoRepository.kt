package com.example.proyectomov.back

import com.example.proyectomov.back.remote.CartDto
import com.example.proyectomov.back.remote.CartProductDto
import com.example.proyectomov.back.remote.CartUpsertDto
import com.example.proyectomov.back.remote.FakeStoreApiService

class CarritoRepository(
    private val api: FakeStoreApiService,
) {
    suspend fun obtenerCarritos(): Result<List<CarritoOutlet>> = runCatching {
        api.getCarts().map { it.toCarritoOutlet() }
    }

    suspend fun obtenerCarrito(id: Int): Result<CarritoOutlet> = runCatching {
        api.getCart(id).toCarritoOutlet()
    }

    suspend fun obtenerCarritosPorUsuario(userId: Int): Result<List<CarritoOutlet>> = runCatching {
        api.getCartsByUser(userId).map { it.toCarritoOutlet() }
    }

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

    suspend fun eliminarCarrito(id: Int): Result<CarritoOutlet> = runCatching {
        api.deleteCart(id).toCarritoOutlet()
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
