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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritosViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val dao = OutletRoomDatabase.obtener(application).favoritoDao()
    private val cuentasRepository = UsuarioCuentasRepository(application.applicationContext)

    private val _ids = MutableStateFlow<Set<String>>(emptySet())
    val idsFavoritos: StateFlow<Set<String>> = _ids.asStateFlow()

    private var usuarioFavoritosId: Int? = null

    init {
        viewModelScope.launch {
            cuentasRepository.sesionUsuarioIdFlow().collectLatest { usuarioId ->
                usuarioFavoritosId = usuarioId
                _ids.value =
                    if (usuarioId == null || usuarioId <= 0) {
                        emptySet()
                    } else {
                        withContext(Dispatchers.IO) { dao.listarIds(usuarioId).toSet() }
                    }
            }
        }
    }

    fun alternarFavorito(idMostrar: String) {
        val uid = usuarioFavoritosId
        if (uid == null || uid <= 0) return
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (dao.existe(uid, idMostrar)) {
                    dao.borrarPorId(uid, idMostrar)
                } else {
                    dao.insertar(
                        FavoritoProductoOutletEntity(usuarioId = uid, idMostrar = idMostrar),
                    )
                }
            }
            _ids.value = withContext(Dispatchers.IO) { dao.listarIds(uid).toSet() }
        }
    }
}
