package com.example.proyectomov.back

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomov.back.api.ApiClient
import kotlinx.coroutines.launch

class UsuariosViewModel(
    private val repository: UsuariosRepository = UsuariosRepository(ApiClient.fakeStoreApi),
) : ViewModel() {
    var usuarios by mutableStateOf<List<UsuarioOutlet>>(emptyList())
        private set

    var cargando by mutableStateOf(false)
        private set

    var error by mutableStateOf("")
        private set

    fun cargarUsuarios() {
        viewModelScope.launch {
            cargando = true
            error = ""
            val resultado = repository.obtenerUsuarios()
            if (resultado.isSuccess) {
                usuarios = resultado.getOrDefault(emptyList())
            } else {
                error = resultado.exceptionOrNull()?.message ?: "No se pudieron cargar los usuarios."
            }
            cargando = false
        }
    }
}
