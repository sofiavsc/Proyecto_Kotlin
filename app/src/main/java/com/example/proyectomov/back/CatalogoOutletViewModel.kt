package com.example.proyectomov.back

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomov.back.api.ApiClient
import kotlinx.coroutines.launch

class CatalogoOutletViewModel(
    private val productosRepository: ProductosRepository = ProductosRepository(ApiClient.fakeStoreApi),
    private val memoria: CatalogoOutletMemoria = CatalogoOutletMemoria(),
) : ViewModel() {
    var articulos by mutableStateOf<List<ArticuloOutlet>>(emptyList())
        private set

    var categorias by mutableStateOf<List<String>>(emptyList())
        private set

    var cargando by mutableStateOf(false)
        private set

    var error by mutableStateOf("")
        private set

    init {
        cargarCatalogo()
    }

    fun cargarCatalogo() {
        viewModelScope.launch {
            cargando = true
            error = ""

            val productos = productosRepository.obtenerCatalogoCompletoUnificado()
            val cats = productosRepository.obtenerCategorias()

            if (productos.isSuccess) {
                articulos = productos.getOrDefault(emptyList())
            } else {
                articulos = memoria.obtenerArticulosDemo()
                error = "No se pudo cargar desde API. Se muestra catálogo local."
            }

            categorias = cats.getOrDefault(emptyList())
            cargando = false
        }
    }
}
