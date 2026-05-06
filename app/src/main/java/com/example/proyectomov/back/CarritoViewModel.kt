package com.example.proyectomov.back

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomov.R
import com.example.proyectomov.back.api.ApiClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CarritoViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val repository: CarritoRepository = CarritoRepository(ApiClient.fakeStoreApi)

    private fun str(id: Int) = getApplication<Application>().getString(id)
    var carritoActivo by mutableStateOf<CarritoOutlet?>(null)
        private set

    var cargando by mutableStateOf(false)
        private set

    var operando by mutableStateOf(false)
        private set

    var error by mutableStateOf("")
        private set

    private var idCarritoSesion: Int? = null

    companion object {
        const val USER_ID_DEMO = 1
    }

    private fun fechaHoyIso(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    fun sincronizarCarritoUsuario(alTerminar: (() -> Unit)? = null) {
        viewModelScope.launch {
            cargando = true
            error = ""
            cargando = false
            alTerminar?.invoke()
        }
    }

    fun agregarProducto(productId: Int, alResultado: (exito: Boolean, mensaje: String) -> Unit) {
        viewModelScope.launch {
            operando = true
            error = ""
            val fecha = fechaHoyIso()
            val idSesion = idCarritoSesion

            if (idSesion == null) {
                repository
                    .crearCarrito(
                        userId = USER_ID_DEMO,
                        fecha = fecha,
                        productos = listOf(ItemCarritoOutlet(productId = productId, cantidad = 1)),
                    )
                    .fold(
                        onSuccess = { guardado ->
                            idCarritoSesion = guardado.id
                            carritoActivo = guardado
                            operando = false
                            alResultado(true, str(R.string.msg_cart_added))
                        },
                        onFailure = { ex ->
                            operando = false
                            error = ex.message ?: str(R.string.cart_err_create)
                            alResultado(false, error)
                        },
                    )
                return@launch
            }

            val base = carritoActivo
            if (base == null || base.id != idSesion) {
                idCarritoSesion = null
                carritoActivo = null
                operando = false
                agregarProducto(productId, alResultado)
                return@launch
            }

            if (base.productos.any { it.productId == productId }) {
                operando = false
                alResultado(true, str(R.string.msg_cart_already))
                return@launch
            }

            val nuevaLista =
                base.productos + ItemCarritoOutlet(productId = productId, cantidad = 1)
            repository
                .actualizarCarrito(id = base.id, userId = USER_ID_DEMO, fecha = fecha, productos = nuevaLista)
                .fold(
                    onSuccess = { guardado ->
                        idCarritoSesion = guardado.id
                        carritoActivo = guardado
                        operando = false
                        alResultado(true, str(R.string.msg_cart_added))
                    },
                    onFailure = { ex ->
                        operando = false
                        error = ex.message ?: str(R.string.cart_err_update)
                        alResultado(false, error)
                    },
                )
        }
    }

    fun quitarProducto(productId: Int) {
        viewModelScope.launch {
            val idSesion = idCarritoSesion ?: return@launch
            operando = true
            error = ""

            val base = carritoActivo
            if (base == null || base.id != idSesion) {
                operando = false
                return@launch
            }

            val nuevaLista = base.productos.filter { it.productId != productId }
            repository.actualizarCarrito(
                id = base.id,
                userId = USER_ID_DEMO,
                fecha = fechaHoyIso(),
                productos = nuevaLista,
            ).fold(
                onSuccess = { guardado ->
                    carritoActivo = guardado
                    operando = false
                },
                onFailure = { ex ->
                    operando = false
                    error = ex.message ?: str(R.string.cart_err_remove)
                },
            )
        }
    }

    /** Vacía el carrito solo en memoria (no persiste en el dispositivo). */
    fun finalizarCompraLocal() {
        idCarritoSesion = null
        carritoActivo = null
        error = ""
    }
}
