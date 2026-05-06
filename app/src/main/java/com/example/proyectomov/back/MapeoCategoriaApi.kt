package com.example.proyectomov.back

import android.content.res.Resources
import com.example.proyectomov.R
import java.util.Locale

/**
 * Convierte el slug de categoría de Fake Store API a texto mostrable según recursos (es/en).
 * El valor original en inglés se mantiene en el modelo para filtros y navegación.
 */
object MapeoCategoriaApi {

    fun etiquetaMostrar(resources: Resources, slugApi: String): String {
        val key = slugApi.trim().lowercase(Locale.ROOT)
        val resId = when (key) {
            "electronics" -> R.string.api_cat_electronics
            "jewelery", "jewelry" -> R.string.api_cat_jewelry
            "men's clothing" -> R.string.api_cat_mens_clothing
            "women's clothing" -> R.string.api_cat_womens_clothing
            else -> null
        }
        return if (resId != null) {
            resources.getString(resId)
        } else {
            slugApi.replaceFirstChar { ch ->
                if (ch.isLowerCase()) ch.titlecase(Locale.getDefault()) else ch.toString()
            }
        }
    }
}
