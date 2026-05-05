package com.example.proyectomov.back

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomov.back.api.ApiClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CarritoViewModel(
    private val repository: CarritoRepository = CarritoRepository(ApiClient.fakeStoreApi),
) : ViewModel() {
    var carritoActivo by mutableStateOf<CarritoOutlet?>(null)
        private set

    var cargando by mutableStateOf(false)
        private set

    var operando by mutableStateOf(false)
        private set

    var error by mutableStateOf("")
        private set

    /** Carrito creado en esta sesión; no se reutilizan carritos demo de GET /carts/user/1. */
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
            // FakeStore no garantiza persistencia real para GET /carts/{id};
            // mantenemos el carrito de la sesión local para evitar errores por body nulo.
            cargando = false
            alTerminar?.invoke()
        }
    }

    /** Cada producto aparece como una línea con cantidad 1 (API); no duplicar líneas al volver a añadir. */
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
                            alResultado(true, "Artículo añadido al carrito.")
                        },
                        onFailure = { ex ->
                            operando = false
                            error = ex.message ?: "No se pudo crear el carrito."
                            alResultado(false, error)
                        },
                    )
                return@launch
            }

            val base = carritoActivo
            if (base == null || base.id != idSesion) {
                // Si se perdió el estado local de sesión, creamos un nuevo carrito de sesión.
                idCarritoSesion = null
                carritoActivo = null
                operando = false
                agregarProducto(productId, alResultado)
                return@launch
            }

            if (base.productos.any { it.productId == productId }) {
                operando = false
                alResultado(true, "Ya está en tu carrito.")
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
                        alResultado(true, "Artículo añadido al carrito.")
                    },
                    onFailure = { ex ->
                        operando = false
                        error = ex.message ?: "No se pudo actualizar el carrito."
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
                    error = ex.message ?: "No se pudo quitar el artículo."
                },
            )
        }
    }
}
