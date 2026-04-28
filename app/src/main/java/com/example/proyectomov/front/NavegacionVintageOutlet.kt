package com.example.proyectomov.front

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.proyectomov.back.InicioSesionViewModel
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
data class RutaDetalleArticulo(
    val titulo: String,
    val precioPesosEntero: Int,
    val descripcion: String,
    val imagenResId: Int,
)

@Preview(showBackground = true)
@Composable
fun NavegacionVintageOutlet(innerPadding: PaddingValues = PaddingValues(0.dp)) {
    val navController = rememberNavController()
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
            val vm = remember { InicioSesionViewModel() }
            PantallaAccesoOutlet(
                viewModel = vm,
                alEntrarOk = {
                    navController.navigate(RutaMenuInicioOutlet) {
                    }
                },
                onVolver = { navController.popBackStack() },
            )
        }
        composable<RutaRegistroOutlet> {
            PantallaRegistroOutlet(
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
