package com.example.proyectomov.back

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomov.back.remote.ApiClient
import kotlinx.coroutines.launch

class CarritoViewModel(
    private val repository: CarritoRepository = CarritoRepository(ApiClient.fakeStoreApi),
) : ViewModel() {
    var carritos by mutableStateOf<List<CarritoOutlet>>(emptyList())
        private set

    var cargando by mutableStateOf(false)
        private set

    var error by mutableStateOf("")
        private set

    fun cargarTodos() {
        viewModelScope.launch {
            cargando = true
            error = ""
            val resultado = repository.obtenerCarritos()
            if (resultado.isSuccess) {
                carritos = resultado.getOrDefault(emptyList())
            } else {
                error = resultado.exceptionOrNull()?.message ?: "No se pudieron cargar los carritos."
            }
            cargando = false
        }
    }
}
