package com.example.proyectomov.back

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomov.R
import com.example.proyectomov.back.local.room.CarritoLocalLineaEntity
import com.example.proyectomov.back.local.room.OutletRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CarritoViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val dao = OutletRoomDatabase.obtener(application).carritoLocalLineaDao()
    private val cuentasRepository = UsuarioCuentasRepository(application.applicationContext)

    private fun str(id: Int) = getApplication<Application>().getString(id)

    var carritoActivo by mutableStateOf<CarritoOutlet?>(null)
        private set

    var operando by mutableStateOf(false)
        private set

    var error by mutableStateOf("")
        private set

    companion object {
        /** Carrito persistente sólo en Room; mismo id estable para todas las líneas locales. */
        private const val CARRITO_LOCAL_ID_UI = -1
    }

    private fun fechaHoyIso(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    private suspend fun cargarParaUsuario(usuarioId: Int?) {
        when {
            usuarioId == null || usuarioId <= 0 -> carritoActivo = null
            else -> {
                val lineas = withContext(Dispatchers.IO) {
                    dao.lineasPorUsuario(usuarioId).map { fila ->
                        ItemCarritoOutlet(productId = fila.productId, cantidad = fila.cantidad)
                    }
                }
                carritoActivo = CarritoOutlet(
                    id = CARRITO_LOCAL_ID_UI,
                    userId = usuarioId,
                    fecha = fechaHoyIso(),
                    productos = lineas,
                )
            }
        }
        error = ""
    }

    init {
        viewModelScope.launch {
            cuentasRepository.sesionUsuarioIdFlow().collectLatest { usuarioId ->
                cargarParaUsuario(usuarioId)
            }
        }
    }

    fun sincronizarCarritoUsuario(alTerminar: (() -> Unit)? = null) {
        viewModelScope.launch {
            error = ""
            val uid =
                withContext(Dispatchers.IO) { cuentasRepository.obtenerSesionUsuarioId() }
            cargarParaUsuario(uid)
            alTerminar?.invoke()
        }
    }

    fun agregarProducto(productId: Int, alResultado: (exito: Boolean, mensaje: String) -> Unit) {
        viewModelScope.launch {
            operando = true
            error = ""
            val usuarioId =
                withContext(Dispatchers.IO) { cuentasRepository.obtenerSesionUsuarioId() }
            if (usuarioId == null || usuarioId <= 0) {
                operando = false
                error = str(R.string.err_no_active_session)
                alResultado(false, error)
                return@launch
            }

            val lineasPrevias = withContext(Dispatchers.IO) { dao.lineasPorUsuario(usuarioId) }
            if (lineasPrevias.any { it.productId == productId }) {
                operando = false
                alResultado(true, str(R.string.msg_cart_already))
                return@launch
            }

            withContext(Dispatchers.IO) {
                dao.insertarOReemplazar(
                    CarritoLocalLineaEntity(
                        usuarioId = usuarioId,
                        productId = productId,
                        cantidad = 1,
                    ),
                )
            }
            cargarParaUsuario(usuarioId)
            operando = false
            alResultado(true, str(R.string.msg_cart_added))
        }
    }

    fun quitarProducto(productId: Int) {
        viewModelScope.launch {
            val usuarioId =
                withContext(Dispatchers.IO) { cuentasRepository.obtenerSesionUsuarioId() }
                ?: return@launch
            operando = true
            error = ""
            withContext(Dispatchers.IO) { dao.borrarLinea(usuarioId, productId) }
            cargarParaUsuario(usuarioId)
            operando = false
        }
    }

    fun finalizarCompraLocal() {
        viewModelScope.launch {
            val usuarioId =
                withContext(Dispatchers.IO) { cuentasRepository.obtenerSesionUsuarioId() }
                ?: return@launch
            withContext(Dispatchers.IO) { dao.borrarTodoUsuario(usuarioId) }
            cargarParaUsuario(usuarioId)
            error = ""
        }
    }
}
