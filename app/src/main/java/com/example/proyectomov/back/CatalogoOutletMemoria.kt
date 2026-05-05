package com.example.proyectomov.back

import com.example.proyectomov.R

class CatalogoOutletMemoria {

    fun obtenerArticulosDemo(): List<ArticuloOutlet> {
        val img = R.drawable.ic_launcher_foreground
        val lista = mutableListOf<ArticuloOutlet>()
        lista.add(
            ArticuloOutlet(
                idMostrar = "001",
                imagenUrl = "",
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
                imagenUrl = "",
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
                imagenUrl = "",
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
                imagenUrl = "",
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
