package com.example.proyectomov.back

import com.example.proyectomov.back.api.FakeStoreApiService
import com.example.proyectomov.back.api.ProductDto
import com.example.proyectomov.back.api.ProductUpsertDto

class ProductosRepository(
    private val api: FakeStoreApiService,
) {
    suspend fun obtenerProductos(): Result<List<ArticuloOutlet>> = runCatching {
        api.getProducts(limit = null).map { it.toArticuloOutlet() }
    }

    /**
     * Une lista general (con [limit] alto) + productos por cada categoría y elimina duplicados por id.
     * Así el catálogo refleja todo lo que la API expone, no solo los ~20 del GET /products por defecto.
     */
    suspend fun obtenerCatalogoCompletoUnificado(): Result<List<ArticuloOutlet>> = runCatching {
        val porId = linkedMapOf<String, ArticuloOutlet>()

        fun agregar(lista: List<ArticuloOutlet>) {
            lista.forEach { art ->
                porId.putIfAbsent(art.idMostrar, art)
            }
        }

        runCatching { api.getProducts(limit = 100).map { it.toArticuloOutlet() } }.onSuccess { agregar(it) }
        val categorias = runCatching { api.getCategories() }.getOrElse { emptyList() }
        categorias.forEach { cat ->
            runCatching {
                api.getProductsByCategory(cat).map { it.toArticuloOutlet() }
            }.onSuccess { agregar(it) }
        }

        if (porId.isEmpty()) {
            agregar(api.getProducts(limit = null).map { it.toArticuloOutlet() })
        }

        porId.values.sortedBy { it.idMostrar }
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
    val url = image?.trim().orEmpty()
    return ArticuloOutlet(
        idMostrar = id.toString().padStart(3, '0'),
        imagenUrl = url,
        titulo = title,
        precioPesosEntero = price.toInt(),
        categoria = category,
        descripcion = description,
        calificacionEstrellas = (rating?.rate ?: 0.0).toFloat(),
    )
}
