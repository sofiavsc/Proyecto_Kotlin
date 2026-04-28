package com.example.proyectomov.back

import com.example.proyectomov.R
import kotlin.to

class CatalogoOutletMemoria {

    fun obtenerArticulosDemo(): List<ArticuloOutlet> {
        val img = R.drawable.ic_launcher_foreground
        val lista = mutableListOf<ArticuloOutlet>()
        lista.add(
            ArticuloOutlet(
                idMostrar = "001",
                imagenResId = img,
                titulo = "BOMBER CUERO 80S",
                precioPesosEntero = 85,
                categoria = "Chaquetas",
                descripcion = "Chaqueta aviador estilo 70s. Cuero sintético, talla M.",
                calificacionEstrellas = 4.5f,
            ),
        )
        lista.add(
            ArticuloOutlet(
                idMostrar = "002",
                imagenResId = img,
                titulo = "DENIM LEVI'S 90S",
                precioPesosEntero = 72,
                categoria = "Denim",
                descripcion = "Corte clásico, desgaste leve en rodillas.",
                calificacionEstrellas = 4f,
            ),
        )
        lista.add(
            ArticuloOutlet(
                idMostrar = "003",
                imagenResId = img,
                titulo = "TENIS RETRO BLANCOS",
                precioPesosEntero = 55,
                categoria = "Calzado",
                descripcion = "Suela original, buen estado.",
                calificacionEstrellas = 3.5f,
            ),
        )
        lista.add(
            ArticuloOutlet(
                idMostrar = "004",
                imagenResId = img,
                titulo = "CHAQUETA AVIADOR 1970",
                precioPesosEntero = 125,
                categoria = "Chaquetas",
                descripcion = "Inspiración mockup detalle.",
                calificacionEstrellas = 5f,
            ),
        )
        return lista
    }

    fun marcasDestacadasDemo(): List<Pair<String, Int>> {
        val img = R.drawable.ic_launcher_foreground
        return listOf(
            "DENIM" to img,
            "SPORTS" to img,
            "RETRO" to img,
            "UTILITY" to img,
        )
    }
}
