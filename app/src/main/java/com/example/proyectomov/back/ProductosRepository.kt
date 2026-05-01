package com.example.proyectomov.back

import com.example.proyectomov.R
import com.example.proyectomov.back.remote.FakeStoreApiService
import com.example.proyectomov.back.remote.ProductDto
import com.example.proyectomov.back.remote.ProductUpsertDto

class ProductosRepository(
    private val api: FakeStoreApiService,
) {
    suspend fun obtenerProductos(): Result<List<ArticuloOutlet>> = runCatching {
        api.getProducts().map { it.toArticuloOutlet() }
    }

    suspend fun obtenerProducto(id: Int): Result<ArticuloOutlet> = runCatching {
        api.getProduct(id).toArticuloOutlet()
    }

    suspend fun obtenerCategorias(): Result<List<String>> = runCatching {
        api.getCategories()
    }

    suspend fun obtenerProductosPorCategoria(categoria: String): Result<List<ArticuloOutlet>> = runCatching {
        api.getProductsByCategory(categoria).map { it.toArticuloOutlet() }
    }

    suspend fun crearProducto(
        titulo: String,
        precio: Double,
        descripcion: String,
        imagen: String,
        categoria: String,
    ): Result<ArticuloOutlet> = runCatching {
        api.createProduct(
            ProductUpsertDto(
                title = titulo,
                price = precio,
                description = descripcion,
                image = imagen,
                category = categoria,
            ),
        ).toArticuloOutlet()
    }

    suspend fun actualizarProducto(
        id: Int,
        titulo: String,
        precio: Double,
        descripcion: String,
        imagen: String,
        categoria: String,
    ): Result<ArticuloOutlet> = runCatching {
        api.updateProduct(
            id = id,
            body = ProductUpsertDto(
                title = titulo,
                price = precio,
                description = descripcion,
                image = imagen,
                category = categoria,
            ),
        ).toArticuloOutlet()
    }

    suspend fun eliminarProducto(id: Int): Result<ArticuloOutlet> = runCatching {
        api.deleteProduct(id).toArticuloOutlet()
    }
}

private fun ProductDto.toArticuloOutlet(): ArticuloOutlet {
    return ArticuloOutlet(
        idMostrar = id.toString().padStart(3, '0'),
        imagenResId = R.drawable.ic_launcher_foreground,
        titulo = title,
        precioPesosEntero = price.toInt(),
        categoria = category,
        descripcion = description,
        calificacionEstrellas = (rating?.rate ?: 0.0).toFloat(),
    )
}
