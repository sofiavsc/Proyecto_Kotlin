package com.example.proyectomov.back

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomov.R
import com.example.proyectomov.back.api.ApiClient
import com.example.proyectomov.back.local.PreferenciasIdiomaApp
import com.example.proyectomov.back.local.room.OutletRoomDatabase
import com.example.proyectomov.back.local.room.PublicadosVenderOutletRoom
import com.example.proyectomov.back.local.room.aArticuloOutlet
import com.example.proyectomov.back.local.room.aEntityCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CatalogoOutletViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val productosRepository: ProductosRepository = ProductosRepository(ApiClient.fakeStoreApi)
    private val memoria: CatalogoOutletMemoria = CatalogoOutletMemoria()
    private val traductor = TraductorTextoApi()
    private val daoCatalogo = OutletRoomDatabase.obtener(application).productoCatalogoDao()
    private val publicadosVender = PublicadosVenderOutletRoom(application)

    var articulos by mutableStateOf<List<ArticuloOutlet>>(emptyList())
        private set

    var categorias by mutableStateOf<List<String>>(emptyList())
        private set

    var cargando by mutableStateOf(false)
        private set

    var error by mutableStateOf("")
        private set

    var errorPublicacion by mutableStateOf("")
        private set

    var publicando by mutableStateOf(false)
        private set

    init {
        cargarCatalogo()
    }

    override fun onCleared() {
        traductor.cerrar()
        super.onCleared()
    }

    fun cargarCatalogo() {
        viewModelScope.launch {
            cargando = true
            error = ""

            val publicados = publicadosVender.listarOrdenMasReciente()
            val desdeCache = withContext(Dispatchers.IO) {
                daoCatalogo.listarTodos().map { it.aArticuloOutlet() }
            }

            val productos = productosRepository.obtenerCatalogoCompletoUnificado()
            val cats = productosRepository.obtenerCategorias()

            var listaBase = if (productos.isSuccess) {
                val fresh = productos.getOrDefault(emptyList())
                withContext(Dispatchers.IO) {
                    daoCatalogo.reemplazarCatalogo(
                        fresh.map { it.aEntityCache() },
                    )
                }
                fresh
            } else {
                error = getApplication<Application>().getString(R.string.catalog_api_fallback_error)
                if (desdeCache.isNotEmpty()) desdeCache else memoria.obtenerArticulosDemo()
            }

            if (listaBase.isNotEmpty() &&
                PreferenciasIdiomaApp.tagIdiomaResuelto(getApplication()) == PreferenciasIdiomaApp.TAG_ES
            ) {
                listaBase = withContext(Dispatchers.Default) {
                    traductor.prepararModeloSiNecesario()
                    coroutineScope {
                        listaBase.map { art ->
                            async {
                                art.copy(
                                    titulo = traductor.traducirEnToEs(art.titulo),
                                    descripcion = traductor.traducirEnToEs(art.descripcion),
                                )
                            }
                        }.awaitAll()
                    }
                }
            }

            articulos = combinarPublicadosVenderConCatalogo(publicados, listaBase)
            val catsApi = cats.getOrDefault(emptyList())
            categorias = catsApi.ifEmpty {
                articulos.map { it.categoria }.distinct().sorted()
            }
            cargando = false
        }
    }

    fun publicarArticulo(
        titulo: String,
        precio: Double,
        descripcion: String,
        imagenUrl: String,
        categoria: String,
        onExito: () -> Unit,
    ) {
        viewModelScope.launch {
            publicando = true
            errorPublicacion = ""
            val result = productosRepository.crearProducto(
                titulo = titulo.trim(),
                precio = precio,
                descripcion = descripcion.trim(),
                imagen = imagenUrl.trim(),
                categoria = categoria,
            )
            result.fold(
                onSuccess = { nuevo ->
                    publicadosVender.guardarTrasPublicarApi(nuevo)
                    val publicados = publicadosVender.listarOrdenMasReciente()
                    val combinado = combinarPublicadosVenderConCatalogo(
                        publicados,
                        articulos.filter { it.idMostrar != nuevo.idMostrar },
                    )
                    Handler(Looper.getMainLooper()).post {
                        articulos = combinado
                        publicando = false
                        onExito()
                    }
                },
                onFailure = { e ->
                    val msg = e.message?.takeIf { it.isNotBlank() }
                        ?: getApplication<Application>().getString(R.string.pub_err_publish_generic)
                    Handler(Looper.getMainLooper()).post {
                        errorPublicacion = msg
                        publicando = false
                    }
                },
            )
        }
    }

    private fun combinarPublicadosVenderConCatalogo(
        publicadosUsuario: List<ArticuloOutlet>,
        catalogoResto: List<ArticuloOutlet>,
    ): List<ArticuloOutlet> =
        (publicadosUsuario + catalogoResto).distinctBy { it.idMostrar }
}
