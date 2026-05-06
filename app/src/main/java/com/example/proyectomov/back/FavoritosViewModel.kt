package com.example.proyectomov.back

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomov.back.local.room.FavoritoProductoOutletEntity
import com.example.proyectomov.back.local.room.OutletRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritosViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val dao = OutletRoomDatabase.obtener(application).favoritoDao()

    private val _ids = MutableStateFlow<Set<String>>(emptySet())
    val idsFavoritos: StateFlow<Set<String>> = _ids.asStateFlow()

    init {
        viewModelScope.launch {
            _ids.value = withContext(Dispatchers.IO) { dao.listarIds().toSet() }
        }
    }

    fun alternarFavorito(idMostrar: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (dao.existe(idMostrar)) {
                    dao.borrarPorId(idMostrar)
                } else {
                    dao.insertar(FavoritoProductoOutletEntity(idMostrar))
                }
            }
            _ids.value = withContext(Dispatchers.IO) { dao.listarIds().toSet() }
        }
    }
}
