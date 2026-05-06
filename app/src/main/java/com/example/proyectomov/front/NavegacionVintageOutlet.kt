package com.example.proyectomov.front

import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.proyectomov.R
import com.example.proyectomov.back.CarritoViewModel
import com.example.proyectomov.back.CategoriaDestacadaOutlet
import com.example.proyectomov.back.CatalogoOutletViewModel
import com.example.proyectomov.back.FavoritosViewModel
import com.example.proyectomov.back.InicioSesionViewModel
import com.example.proyectomov.back.PerfilViewModel
import com.example.proyectomov.back.RegistroViewModel
import com.example.proyectomov.ui.theme.ProyectoMovTheme
import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
object RutaAccesoOutlet

@Serializable
object RutaBienvenidaOutlet

@Serializable
object RutaRegistroOutlet

/** Menú principal (pantalla imagen 3). */
@Serializable
object RutaMenuInicioOutlet

@Serializable
object RutaFavoritosOutlet

@Serializable
object RutaCarritoOutlet

@Serializable
object RutaAgregarArticuloOutlet

/** Perfil del usuario (cuenta local). */
@Serializable
object RutaPerfilOutlet

@Serializable
object RutaAjustesOutlet

@Serializable
object RutaCatalogoCompletoOutlet

@Serializable
data class RutaCatalogoPorCategoria(
    val categoria: String,
)

@Serializable
data class RutaDetalleArticulo(
    val idMostrar: String,
    val titulo: String,
    val precioPesosEntero: Int,
    val descripcion: String,
    val imagenUrl: String,
)

private fun iconoParaCategoriaDestacada(categoriaFiltro: String): Int {
    return when (categoriaFiltro.lowercase(Locale.ROOT)) {
        "electronics" -> R.drawable.electronicos
        "jewelery" -> R.drawable.joyeria
        "men's clothing" -> R.drawable.ropa_hombre
        else -> R.drawable.ic_launcher_foreground
    }
}

@Composable
private fun etiquetaCategoriaDestacada(categoriaFiltro: String): String {
    return when (categoriaFiltro.lowercase(Locale.ROOT)) {
        "electronics" -> stringResource(R.string.cat_dest_electronics)
        "jewelery" -> stringResource(R.string.cat_dest_jewelry)
        "men's clothing" -> stringResource(R.string.cat_dest_mens_clothing)
        "women's clothing" -> stringResource(R.string.cat_dest_womens_clothing)
        else -> categoriaFiltro.uppercase(Locale.getDefault())
    }
}

@Composable
fun NavegacionVintageOutlet(innerPadding: PaddingValues = PaddingValues(0.dp)) {
    if (LocalInspectionMode.current) {
        ProyectoMovTheme {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                PantallaBienvenidaOutlet(
                    onIniciarSesion = {},
                    onRegistrarse = {},
                )
            }
        }
        return
    }

    val navController = rememberNavController()
    val catalogoVm = viewModel<CatalogoOutletViewModel>()
    val favoritosVm = viewModel<FavoritosViewModel>()
    val favoritosIds by favoritosVm.idsFavoritos.collectAsStateWithLifecycle()
    val favoritosLista = favoritosIds.toList().sorted()
    val articulos = catalogoVm.articulos
    val iconPorDefecto = R.drawable.ic_launcher_foreground
    val categoriasDestacadas = if (catalogoVm.categorias.isNotEmpty()) {
        catalogoVm.categorias.take(3).map { cat ->
            CategoriaDestacadaOutlet(
                etiqueta = etiquetaCategoriaDestacada(cat),
                categoriaFiltro = cat,
                iconoResId = iconoParaCategoriaDestacada(cat),
            )
        }
    } else {
        listOf(
            CategoriaDestacadaOutlet(
                stringResource(R.string.cat_chip_jackets),
                "Chaquetas",
                iconPorDefecto,
            ),
            CategoriaDestacadaOutlet(stringResource(R.string.cat_chip_denim), "Denim", iconPorDefecto),
            CategoriaDestacadaOutlet(
                stringResource(R.string.cat_chip_footwear),
                "Calzado",
                iconPorDefecto,
            ),
        )
    }
    val contexto = LocalContext.current
    val carritoVm = viewModel<CarritoViewModel>()

    LaunchedEffect(Unit) {
        carritoVm.sincronizarCarritoUsuario()
    }

    val navFadeMs = 200
    NavHost(
        navController = navController,
        startDestination = RutaBienvenidaOutlet,
        modifier = Modifier.padding(innerPadding),
        enterTransition = { fadeIn(animationSpec = tween(navFadeMs)) },
        exitTransition = { fadeOut(animationSpec = tween(navFadeMs)) },
        popEnterTransition = { fadeIn(animationSpec = tween(navFadeMs)) },
        popExitTransition = { fadeOut(animationSpec = tween(navFadeMs)) },
    ) {
        composable<RutaBienvenidaOutlet> {
            PantallaBienvenidaOutlet(
                onIniciarSesion = { navController.navigate(RutaAccesoOutlet) },
                onRegistrarse = { navController.navigate(RutaRegistroOutlet) },
            )
        }
        composable<RutaAccesoOutlet> {
            val vm: InicioSesionViewModel = viewModel()
            PantallaAccesoOutlet(
                viewModel = vm,
                alEntrarOk = {
                    navController.navigate(RutaMenuInicioOutlet) {
                    }
                },
                onVolver = { navController.popBackStack() },
                onIrARegistro = { navController.navigate(RutaRegistroOutlet) },
            )
        }
        composable<RutaRegistroOutlet> {
            val vmRegistro: RegistroViewModel = viewModel()
            PantallaRegistroOutlet(
                viewModel = vmRegistro,
                onVolver = { navController.popBackStack() },
                onRegistroExitoso = {
                    Toast.makeText(
                        contexto,
                        contexto.getString(R.string.toast_registration_ok),
                        Toast.LENGTH_SHORT,
                    ).show()
                    navController.navigate(RutaAccesoOutlet) {
                        popUpTo(RutaBienvenidaOutlet) { inclusive = false }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable<RutaMenuInicioOutlet> {
            PantallaMenuInicioOutlet(
                navController = navController,
                articulos = articulos,
                categoriasDestacadas = categoriasDestacadas,
                favoritos = favoritosLista,
                cargandoCatalogo = catalogoVm.cargando,
                onToggleFavorito = { id -> favoritosVm.alternarFavorito(id) },
                onIrCarrito = { navController.navigate(RutaCarritoOutlet) },
            )
        }
        composable<RutaCatalogoCompletoOutlet> {
            PantallaCatalogoCompletoOutlet(
                navController = navController,
                articulos = articulos,
                favoritos = favoritosLista,
                cargandoCatalogo = catalogoVm.cargando,
                onToggleFavorito = { id -> favoritosVm.alternarFavorito(id) },
                onIrCarrito = { navController.navigate(RutaCarritoOutlet) },
            )
        }
        composable<RutaCatalogoPorCategoria> {
            val args: RutaCatalogoPorCategoria = it.toRoute()
            PantallaCatalogoPorCategoriaOutlet(
                navController = navController,
                categoria = args.categoria,
                articulos = articulos,
                favoritos = favoritosLista,
                cargandoCatalogo = catalogoVm.cargando,
                onToggleFavorito = { id -> favoritosVm.alternarFavorito(id) },
                onIrCarrito = { navController.navigate(RutaCarritoOutlet) },
            )
        }
        composable<RutaCarritoOutlet> {
            PantallaCarritoOutlet(
                navController = navController,
                articulos = articulos,
                carritoViewModel = carritoVm,
            )
        }
        composable<RutaFavoritosOutlet> {
            PantallaFavoritosOutlet(
                navController = navController,
                articulos = articulos,
                favoritos = favoritosLista,
                onIrCarrito = { navController.navigate(RutaCarritoOutlet) },
            )
        }
        composable<RutaPerfilOutlet> {
            val perfilVm: PerfilViewModel = viewModel()
            PantallaPerfilOutlet(
                navController = navController,
                viewModel = perfilVm,
            )
        }
        composable<RutaAjustesOutlet> {
            PantallaAjustesOutlet(
                navController = navController,
                onIdiomaCambiado = { catalogoVm.cargarCatalogo() },
            )
        }
        composable<RutaAgregarArticuloOutlet> {
            PantallaAgregarArticuloOutlet(
                viewModel = catalogoVm,
                onVolver = { navController.popBackStack() },
                onPublicado = {
                    Toast.makeText(
                        contexto,
                        contexto.getString(R.string.toast_article_published),
                        Toast.LENGTH_SHORT,
                    ).show()
                    navController.popBackStack()
                },
            )
        }
        composable<RutaDetalleArticulo> {
            val datos: RutaDetalleArticulo = it.toRoute()
            val productIdApi = datos.idMostrar.toIntOrNull() ?: 0
            PantallaDetalleArticuloOutlet(
                datos = datos,
                navController = navController,
                esFavorito = favoritosIds.contains(datos.idMostrar),
                onToggleFavorito = {
                    favoritosVm.alternarFavorito(datos.idMostrar)
                },
                carritoViewModel = carritoVm,
                productIdApi = productIdApi,
                contexto = contexto,
            )
        }
    }
}

/**
 * En el Preview, [LocalInspectionMode] está activo y [NavegacionVintageOutlet]
 * solo pinta la bienvenida (sin NavHost ni ViewModels).
 */
@Preview(showBackground = true, name = "Navegacion · bienvenida")
@Composable
private fun NavegacionVintageOutletPreview() {
    NavegacionVintageOutlet()
}
