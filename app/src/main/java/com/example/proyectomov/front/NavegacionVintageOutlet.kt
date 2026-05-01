package com.example.proyectomov.front

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.proyectomov.R
import com.example.proyectomov.back.CatalogoOutletViewModel
import com.example.proyectomov.back.CatalogoOutletMemoria
import com.example.proyectomov.back.InicioSesionViewModel
import com.example.proyectomov.back.RegistroViewModel
import com.example.proyectomov.ui.theme.ProyectoMovTheme
import kotlinx.serialization.Serializable

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
object RutaAgregarArticuloOutlet

@Serializable
data class RutaDetalleArticulo(
    val titulo: String,
    val precioPesosEntero: Int,
    val descripcion: String,
    val imagenResId: Int,
)

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
    val catalogoVm = remember { CatalogoOutletViewModel() }
    val catalogoMemoria = remember { CatalogoOutletMemoria() }
    val articulos = if (catalogoVm.articulos.isNotEmpty()) {
        catalogoVm.articulos
    } else {
        catalogoMemoria.obtenerArticulosDemo()
    }
    val marcas = if (catalogoVm.categorias.isNotEmpty()) {
        catalogoVm.categorias
            .take(4)
            .map { it.uppercase() to R.drawable.ic_launcher_foreground }
    } else {
        catalogoMemoria.marcasDestacadasDemo()
    }
    val favoritos = remember { mutableStateListOf<String>() }

    NavHost(
        navController = navController,
        startDestination = RutaBienvenidaOutlet,
        modifier = Modifier.padding(innerPadding),
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
                    navController.navigate(RutaMenuInicioOutlet) {
                    }
                },
            )
        }
        composable<RutaMenuInicioOutlet> {
            PantallaMenuInicioOutlet(
                navController = navController,
                articulos = articulos,
                marcas = marcas,
                favoritos = favoritos,
                onToggleFavorito = { id ->
                    if (favoritos.contains(id)) favoritos.remove(id) else favoritos.add(id)
                },
            )
        }
        composable<RutaFavoritosOutlet> {
            PantallaFavoritosOutlet(
                navController = navController,
                articulos = articulos,
                favoritos = favoritos,
            )
        }
        composable<RutaAgregarArticuloOutlet> {
            PantallaAgregarArticuloOutlet(
                onVolver = { navController.popBackStack() },
            )
        }
        composable<RutaDetalleArticulo> {
            val datos: RutaDetalleArticulo = it.toRoute()
            PantallaDetalleArticuloOutlet(
                datos = datos,
                navController = navController,
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
