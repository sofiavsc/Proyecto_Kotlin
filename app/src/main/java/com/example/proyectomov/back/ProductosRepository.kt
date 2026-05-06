package com.example.proyectomov.back

import com.example.proyectomov.back.api.FakeStoreApiService
import com.example.proyectomov.back.api.ProductDto
import com.example.proyectomov.back.api.ProductUpsertDto

class ProductosRepository(
    private val api: FakeStoreApiService,
) {
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

    suspend fun obtenerCategorias(): Result<List<String>> = runCatching {
        api.getCategories()
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
