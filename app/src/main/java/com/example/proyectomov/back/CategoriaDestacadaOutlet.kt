package com.example.proyectomov.back

import androidx.annotation.DrawableRes

data class CategoriaDestacadaOutlet(
    val etiqueta: String,
    val categoriaFiltro: String,
    @DrawableRes val iconoResId: Int,
)
