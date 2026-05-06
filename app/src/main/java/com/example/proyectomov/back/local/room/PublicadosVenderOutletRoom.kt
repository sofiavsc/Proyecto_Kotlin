package com.example.proyectomov.back.local.room

import android.app.Application
import com.example.proyectomov.back.ArticuloOutlet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class PublicadosVenderOutletRoom(
    application: Application,
) {

    private val dao = OutletRoomDatabase.obtener(application).productoPublicadoVenderDao()

    suspend fun guardarTrasPublicarApi(articulo: ArticuloOutlet) {
        withContext(Dispatchers.IO) {
            dao.insertar(
                ProductoPublicadoVenderOutletEntity(
                    idLocal = UUID.randomUUID().toString(),
                    idMostrar = articulo.idMostrar,
                    imagenUrl = articulo.imagenUrl,
                    titulo = articulo.titulo,
                    precioPesosEntero = articulo.precioPesosEntero,
                    categoria = articulo.categoria,
                    descripcion = articulo.descripcion,
                    calificacionEstrellas = articulo.calificacionEstrellas,
                    creadoEnMillis = System.currentTimeMillis(),
                ),
            )
        }
    }

    suspend fun listarOrdenMasReciente(): List<ArticuloOutlet> =
        withContext(Dispatchers.IO) {
            dao.listarTodos().map { it.aArticuloOutlet() }.distinctBy { it.idMostrar }
        }
}
