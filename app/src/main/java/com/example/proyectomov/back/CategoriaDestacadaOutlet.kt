package com.example.proyectomov.back

import androidx.annotation.DrawableRes

/**
 * Chip de categoría destacada: [etiqueta] visible; [categoriaFiltro] coincide con [ArticuloOutlet.categoria].
 */
data class CategoriaDestacadaOutlet(
    val etiqueta: String,
    val categoriaFiltro: String,
    @DrawableRes val iconoResId: Int,
)
