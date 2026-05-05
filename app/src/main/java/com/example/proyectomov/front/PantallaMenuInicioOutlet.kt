package com.example.proyectomov.front

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.proyectomov.back.ArticuloOutlet
import com.example.proyectomov.back.CarritoViewModel
import com.example.proyectomov.back.CategoriaDestacadaOutlet
import com.example.proyectomov.back.ItemCarritoOutlet
import com.example.proyectomov.back.FondoCrema
import com.example.proyectomov.back.GrisBordeCampo
import com.example.proyectomov.back.GrisSecundario
import com.example.proyectomov.back.OlivaVintage
import com.example.proyectomov.back.OlivaVintageOscuro
import com.example.proyectomov.back.ProductoImagenConShimmerOutlet
import java.util.Locale
import kotlin.collections.chunked
import kotlin.collections.forEach

private const val ARTICULOS_INICIO_MAX = 10

private enum class TabBarraOutlet {
    Explorar,
    Deseos,
    Vender,
    Bolsa,
    Perfil,
}

private inline fun <reified T : Any> navegarTabInferior(
    navController: NavHostController,
    ruta: T,
) {
    navController.navigate(ruta) {
        launchSingleTop = true
        restoreState = true
        popUpTo(RutaMenuInicioOutlet) {
            saveState = true
        }
    }
}

/**
 * Cada palabra debe aparecer en título o categoría (sin distinguir mayúsculas).
 */
private fun articulosFiltradosBusqueda(
    articulos: List<ArticuloOutlet>,
    consultaTrimada: String,
): List<ArticuloOutlet> {
    val palabras = consultaTrimada.split("\\s+".toRegex()).filter { it.isNotEmpty() }
    if (palabras.isEmpty()) return articulos
    val locale = Locale.getDefault()
    return articulos.filter { art ->
        val textoTituloCategoria = "${art.titulo} ${art.categoria}".lowercase(locale)
        palabras.all { tok -> textoTituloCategoria.contains(tok.lowercase(locale)) }
    }
}

private fun articulosFiltradosCategorias(
    articulos: List<ArticuloOutlet>,
    categoriasSeleccionadas: Set<String>,
): List<ArticuloOutlet> {
    if (categoriasSeleccionadas.isEmpty()) return articulos
    return articulos.filter { art ->
        categoriasSeleccionadas.any { sel -> art.categoria.equals(sel, ignoreCase = true) }
    }
}

/** Misma convención que [com.example.proyectomov.back.ProductosRepository]: id api → idMostrar con ceros. */
private fun articuloPorProductId(articulos: List<ArticuloOutlet>, productId: Int): ArticuloOutlet? {
    val idMostrar = productId.toString().padStart(3, '0')
    return articulos.find { it.idMostrar == idMostrar }
}

@Composable
fun PantallaMenuInicioOutlet(
    navController: NavHostController,
    articulos: List<ArticuloOutlet>,
    categoriasDestacadas: List<CategoriaDestacadaOutlet>,
    favoritos: List<String>,
    cargandoCatalogo: Boolean = false,
    onToggleFavorito: (String) -> Unit,
    onIrCarrito: () -> Unit,
) {
    var textoBusqueda by remember { mutableStateOf("") }
    var categoriasSeleccionadas by remember { mutableStateOf(setOf<String>()) }
    var mostrarFiltroCategorias by remember { mutableStateOf(false) }
    val hayBusquedaActiva = textoBusqueda.trim().isNotEmpty()
    val hayFiltroCategoriaActivo = categoriasSeleccionadas.isNotEmpty()
    val locale = Locale.getDefault()
    val todasLasCategorias = remember(articulos) {
        articulos
            .map { it.categoria.trim() }
            .filter { it.isNotEmpty() }
            .distinctBy { it.lowercase(locale) }
            .sortedBy { it.lowercase(locale) }
    }
    val articulosLista = remember(
        articulos,
        textoBusqueda,
        categoriasSeleccionadas,
    ) {
        val trimmed = textoBusqueda.trim()
        val porBusqueda = articulosFiltradosBusqueda(articulos, trimmed)
        val porCategoria = articulosFiltradosCategorias(porBusqueda, categoriasSeleccionadas)
        if (trimmed.isEmpty() && categoriasSeleccionadas.isEmpty()) {
            porCategoria.take(ARTICULOS_INICIO_MAX)
        } else {
            porCategoria
        }
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            BarraNavegacionInferiorOutlet(
                tabSeleccionada = TabBarraOutlet.Explorar,
                onExplorar = { navegarTabInferior(navController, RutaMenuInicioOutlet) },
                onDeseos = { navegarTabInferior(navController, RutaFavoritosOutlet) },
                onVender = { navController.navigate(RutaAgregarArticuloOutlet) },
                onBolsa = { navegarTabInferior(navController, RutaCarritoOutlet) },
                onPerfil = { },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (cargandoCatalogo && articulos.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = OlivaVintage,
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = { /* avance */ }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                        Text(
                            text = "Vintage Outlet",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontStyle = FontStyle.Italic,
                            ),
                        )
                        IconButton(onClick = { /* avance */ }) {
                            Icon(Icons.Default.Person, contentDescription = "Perfil")
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedTextField(
                            value = textoBusqueda,
                            onValueChange = { textoBusqueda = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Buscar tesoros vintage...", color = GrisSecundario) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GrisBordeCampo,
                                unfocusedBorderColor = GrisBordeCampo,
                            ),
                        )
                        Box {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(OlivaVintage)
                                    .clickable { mostrarFiltroCategorias = !mostrarFiltroCategorias },
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Default.FilterList, contentDescription = "Filtro", tint = Color.White)
                            }
                            DropdownMenu(
                                expanded = mostrarFiltroCategorias,
                                onDismissRequest = { mostrarFiltroCategorias = false },
                                modifier = Modifier.heightIn(max = 320.dp),
                            ) {
                                if (hayFiltroCategoriaActivo) {
                                    DropdownMenuItem(
                                        text = { Text("Quitar filtros", color = OlivaVintage) },
                                        onClick = {
                                            categoriasSeleccionadas = emptySet()
                                        },
                                    )
                                }
                                if (todasLasCategorias.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { Text("Sin categorías", color = GrisSecundario) },
                                        onClick = { },
                                        enabled = false,
                                    )
                                } else {
                                    todasLasCategorias.forEach { categoria ->
                                        val marcada = categoriasSeleccionadas.any {
                                            it.equals(categoria, ignoreCase = true)
                                        }
                                        DropdownMenuItem(
                                            text = { Text(categoria) },
                                            onClick = {
                                                categoriasSeleccionadas = if (marcada) {
                                                    categoriasSeleccionadas.filterNot {
                                                        it.equals(categoria, ignoreCase = true)
                                                    }.toSet()
                                                } else {
                                                    categoriasSeleccionadas + categoria
                                                }
                                            },
                                            trailingIcon = {
                                                Checkbox(
                                                    checked = marcada,
                                                    onCheckedChange = null,
                                                    colors = CheckboxDefaults.colors(
                                                        checkedColor = OlivaVintage,
                                                    ),
                                                )
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                    ) {
            item {
                Text(
                    text = "CATEGORÍAS",
                    color = OlivaVintage,
                        style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
                )
            }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                ) {
                    items(
                        items = categoriasDestacadas,
                        key = { c -> "${c.categoriaFiltro}_${c.etiqueta}" },
                    ) { categoriaChip ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                navController.navigate(
                                    RutaCatalogoPorCategoria(categoria = categoriaChip.categoriaFiltro),
                                )
                            },
                        ) {
                            Image(
                                painter = painterResource(categoriaChip.iconoResId),
                                contentDescription = categoriaChip.etiqueta,
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, GrisBordeCampo, CircleShape),
                                contentScale = ContentScale.Crop,
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(categoriaChip.etiqueta, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        when {
                            hayBusquedaActiva && hayFiltroCategoriaActivo -> "RESULTADOS"
                            hayBusquedaActiva -> "RESULTADOS DE BÚSQUEDA"
                            hayFiltroCategoriaActivo -> "ARTÍCULOS POR CATEGORÍA"
                            else -> "ÚLTIMOS ARTÍCULOS"
                        },
                        color = OlivaVintage,
                        style = MaterialTheme.typography.labelMedium,
                    )
                    if (!hayBusquedaActiva && !hayFiltroCategoriaActivo) {
                        Text(
                            "VER TODO",
                            style = MaterialTheme.typography.labelSmall,
                            textDecoration = TextDecoration.Underline,
                            color = OlivaVintage,
                            modifier = Modifier.clickable {
                                navController.navigate(RutaCatalogoCompletoOutlet)
                            },
                        )
                    }
                }
            }
            if ((hayBusquedaActiva || hayFiltroCategoriaActivo) && articulosLista.isEmpty()) {
                item {
                    Text(
                        text = if (hayBusquedaActiva && hayFiltroCategoriaActivo) {
                            "No hay artículos que coincidan con la búsqueda y las categorías."
                        } else if (hayBusquedaActiva) {
                            "No hay artículos que coincidan con tu búsqueda."
                        } else {
                            "No hay artículos en las categorías seleccionadas."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrisSecundario,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
            } else {
                items(articulosLista.chunked(2)) { fila ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        fila.forEach { art ->
                            TarjetaPolaroidOutlet(
                                articulo = art,
                                esFavorito = favoritos.contains(art.idMostrar),
                                onToggleFavorito = {
                                    onToggleFavorito(art.idMostrar)
                                },
                                onClickFoto = {
                                    navController.navigate(
                                        RutaDetalleArticulo(
                                            idMostrar = art.idMostrar,
                                            titulo = art.titulo,
                                            precioPesosEntero = art.precioPesosEntero,
                                            descripcion = art.descripcion,
                                            imagenUrl = art.imagenUrl,
                                        ),
                                    )
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }
                        if (fila.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCatalogoCompletoOutlet(
    navController: NavHostController,
    articulos: List<ArticuloOutlet>,
    favoritos: List<String>,
    cargandoCatalogo: Boolean,
    onToggleFavorito: (String) -> Unit,
    onIrCarrito: () -> Unit,
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Todos los artículos",
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
            )
        },
        bottomBar = {
            BarraNavegacionInferiorOutlet(
                tabSeleccionada = TabBarraOutlet.Explorar,
                onExplorar = { navegarTabInferior(navController, RutaMenuInicioOutlet) },
                onDeseos = { navegarTabInferior(navController, RutaFavoritosOutlet) },
                onVender = { navController.navigate(RutaAgregarArticuloOutlet) },
                onBolsa = { navegarTabInferior(navController, RutaCarritoOutlet) },
                onPerfil = { },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (cargandoCatalogo && articulos.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = OlivaVintage,
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                ) {
                    items(articulos.chunked(2)) { fila ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            fila.forEach { art ->
                                TarjetaPolaroidOutlet(
                                    articulo = art,
                                    esFavorito = favoritos.contains(art.idMostrar),
                                    onToggleFavorito = {
                                        onToggleFavorito(art.idMostrar)
                                    },
                                    onClickFoto = {
                                        navController.navigate(
                                            RutaDetalleArticulo(
                                                idMostrar = art.idMostrar,
                                                titulo = art.titulo,
                                                precioPesosEntero = art.precioPesosEntero,
                                                descripcion = art.descripcion,
                                                imagenUrl = art.imagenUrl,
                                            ),
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            if (fila.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

private fun tituloCategoriaParaBarra(raw: String): String {
    if (raw.isBlank()) return raw
    val partes = raw.split(" ", "-").filter { it.isNotEmpty() }
    if (partes.isEmpty()) return raw.replaceFirstChar { it.uppercaseChar() }
    return partes.joinToString(" ") { token ->
        token.replaceFirstChar { ch ->
            if (ch.isLowerCase()) ch.titlecase(Locale.getDefault()) else ch.toString()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCatalogoPorCategoriaOutlet(
    navController: NavHostController,
    categoria: String,
    articulos: List<ArticuloOutlet>,
    favoritos: List<String>,
    cargandoCatalogo: Boolean,
    onToggleFavorito: (String) -> Unit,
    onIrCarrito: () -> Unit,
) {
    val articulosCategoria = remember(articulos, categoria) {
        articulos.filter { it.categoria.equals(categoria, ignoreCase = true) }
    }
    val tituloBarra = remember(categoria) { tituloCategoriaParaBarra(categoria) }
    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        tituloBarra,
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
            )
        },
        bottomBar = {
            BarraNavegacionInferiorOutlet(
                tabSeleccionada = TabBarraOutlet.Explorar,
                onExplorar = { navegarTabInferior(navController, RutaMenuInicioOutlet) },
                onDeseos = { navegarTabInferior(navController, RutaFavoritosOutlet) },
                onVender = { navController.navigate(RutaAgregarArticuloOutlet) },
                onBolsa = { navegarTabInferior(navController, RutaCarritoOutlet) },
                onPerfil = { },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (cargandoCatalogo && articulos.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = OlivaVintage,
                )
            } else if (articulosCategoria.isEmpty()) {
                Text(
                    text = "No hay artículos en esta categoría.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrisSecundario,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                ) {
                    items(articulosCategoria.chunked(2)) { fila ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            fila.forEach { art ->
                                TarjetaPolaroidOutlet(
                                    articulo = art,
                                    esFavorito = favoritos.contains(art.idMostrar),
                                    onToggleFavorito = {
                                        onToggleFavorito(art.idMostrar)
                                    },
                                    onClickFoto = {
                                        navController.navigate(
                                            RutaDetalleArticulo(
                                                idMostrar = art.idMostrar,
                                                titulo = art.titulo,
                                                precioPesosEntero = art.precioPesosEntero,
                                                descripcion = art.descripcion,
                                                imagenUrl = art.imagenUrl,
                                            ),
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            if (fila.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
fun PantallaFavoritosOutlet(
    navController: NavHostController,
    articulos: List<ArticuloOutlet>,
    favoritos: List<String>,
    onIrCarrito: () -> Unit,
) {
    val articulosFavoritos = remember(articulos, favoritos) {
        articulos.filter { it.idMostrar in favoritos }
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            BarraNavegacionInferiorOutlet(
                tabSeleccionada = TabBarraOutlet.Deseos,
                onExplorar = { navegarTabInferior(navController, RutaMenuInicioOutlet) },
                onDeseos = { },
                onVender = { navController.navigate(RutaAgregarArticuloOutlet) },
                onBolsa = { navegarTabInferior(navController, RutaCarritoOutlet) },
                onPerfil = { },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Text(
                    text = "ARTÍCULOS FAVORITOS",
                    color = OlivaVintage,
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            if (articulosFavoritos.isEmpty()) {
                item {
                    Text(
                        text = "Aún no tienes artículos en favoritos.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrisSecundario,
                    )
                }
            } else {
                items(articulosFavoritos, key = { it.idMostrar }) { art ->
                    TarjetaFavoritoOutlet(
                        articulo = art,
                        onClick = {
                            navController.navigate(
                                RutaDetalleArticulo(
                                    idMostrar = art.idMostrar,
                                    titulo = art.titulo,
                                    precioPesosEntero = art.precioPesosEntero,
                                    descripcion = art.descripcion,
                                    imagenUrl = art.imagenUrl,
                                ),
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun PantallaCarritoOutlet(
    navController: NavHostController,
    articulos: List<ArticuloOutlet>,
    carritoViewModel: CarritoViewModel,
) {
    LaunchedEffect(Unit) {
        carritoViewModel.sincronizarCarritoUsuario()
    }

    val carrito = carritoViewModel.carritoActivo
    val cargando = carritoViewModel.cargando
    val lineas = carrito?.productos.orEmpty()
    val errorMsg = carritoViewModel.error

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            BarraNavegacionInferiorOutlet(
                tabSeleccionada = TabBarraOutlet.Bolsa,
                onExplorar = { navegarTabInferior(navController, RutaMenuInicioOutlet) },
                onDeseos = { navegarTabInferior(navController, RutaFavoritosOutlet) },
                onVender = { navController.navigate(RutaAgregarArticuloOutlet) },
                onBolsa = { },
                onPerfil = { },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White),
        ) {
            if (cargando) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = OlivaVintage,
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    item {
                        Text(
                            text = "TU CARRITO",
                            color = OlivaVintage,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                    if (errorMsg.isNotEmpty() && lineas.isEmpty()) {
                        item {
                            Text(
                                text = errorMsg,
                                style = MaterialTheme.typography.bodyMedium,
                                color = GrisSecundario,
                            )
                        }
                    }
                    if (lineas.isEmpty() && errorMsg.isEmpty()) {
                        item {
                            Text(
                                text = "Tu carrito está vacío.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = GrisSecundario,
                            )
                        }
                    } else {
                        items(lineas, key = { it.productId }) { linea ->
                            val art = articuloPorProductId(articulos, linea.productId)
                            TarjetaCarritoLineaOutlet(
                                linea = linea,
                                articulo = art,
                                onIrDetalle = {
                                    art?.let { a ->
                                        navController.navigate(
                                            RutaDetalleArticulo(
                                                idMostrar = a.idMostrar,
                                                titulo = a.titulo,
                                                precioPesosEntero = a.precioPesosEntero,
                                                descripcion = a.descripcion,
                                                imagenUrl = a.imagenUrl,
                                            ),
                                        )
                                    }
                                },
                                onQuitar = { carritoViewModel.quitarProducto(linea.productId) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaCarritoLineaOutlet(
    linea: ItemCarritoOutlet,
    articulo: ArticuloOutlet?,
    onIrDetalle: () -> Unit,
    onQuitar: () -> Unit,
) {
    val titulo = articulo?.titulo ?: "Producto #${linea.productId}"
    val imagenUrl = articulo?.imagenUrl.orEmpty()
    val precioTexto = articulo?.let { "$${it.precioPesosEntero}.00" } ?: "—"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable(enabled = articulo != null) { onIrDetalle() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (articulo != null) {
                    ProductoImagenConShimmerOutlet(
                        imagenUrl = imagenUrl,
                        contentDescription = titulo,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(FondoCrema),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(FondoCrema),
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                    )
                    Text(
                        text = precioTexto,
                        color = OlivaVintage,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
            IconButton(onClick = onQuitar) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Quitar del carrito",
                    tint = GrisSecundario,
                )
            }
        }
    }
}

@Composable
private fun TarjetaFavoritoOutlet(
    articulo: ArticuloOutlet,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ProductoImagenConShimmerOutlet(
                imagenUrl = articulo.imagenUrl,
                contentDescription = articulo.titulo,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(FondoCrema),
                contentScale = ContentScale.Crop,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = articulo.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                )
                Text(
                    text = "ID: ${articulo.idMostrar}",
                    style = MaterialTheme.typography.labelSmall,
                    color = GrisSecundario,
                )
                Text(
                    text = "$${articulo.precioPesosEntero}.00",
                    color = OlivaVintage,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Favorito",
                tint = OlivaVintage,
            )
        }
    }
}

@Composable
private fun TarjetaPolaroidOutlet(
    articulo: ArticuloOutlet,
    esFavorito: Boolean,
    onToggleFavorito: () -> Unit,
    onClickFoto: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .clickable { onClickFoto() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(FondoCrema)
                    .clip(RoundedCornerShape(2.dp)),
            ) {
                ProductoImagenConShimmerOutlet(
                    imagenUrl = articulo.imagenUrl,
                    contentDescription = articulo.titulo,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp),
                    contentScale = ContentScale.Fit,
                )
                IconButton(
                    onClick = onToggleFavorito,
                    modifier = Modifier.align(Alignment.BottomEnd),
                ) {
                    Icon(
                        imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (esFavorito) OlivaVintage else GrisSecundario,
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "ID: ${articulo.idMostrar}",
                style = MaterialTheme.typography.labelSmall,
                color = GrisSecundario,
            )
            Text(
                articulo.titulo,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
            )
            Text(
                "$" + articulo.precioPesosEntero + ".00",
                color = OlivaVintage,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}

@Composable
private fun BarraNavegacionInferiorOutlet(
    tabSeleccionada: TabBarraOutlet,
    onExplorar: () -> Unit,
    onDeseos: () -> Unit,
    onVender: () -> Unit,
    onBolsa: (

            ) -> Unit,
    onPerfil: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(1.dp, GrisBordeCampo)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ItemBarraInferior(
            "EXPLORAR",
            Icons.Default.Explore,
            seleccionado = tabSeleccionada == TabBarraOutlet.Explorar,
            onClick = onExplorar,
        )
        ItemBarraInferior(
            "DESEOS",
            Icons.Default.FavoriteBorder,
            seleccionado = tabSeleccionada == TabBarraOutlet.Deseos,
            onClick = onDeseos,
        )
        ItemBarraInferior(
            "VENDER",
            Icons.Default.Add,
            seleccionado = tabSeleccionada == TabBarraOutlet.Vender,
            onClick = onVender,
        )
        ItemBarraInferior(
            "BOLSA",
            Icons.Default.ShoppingBag,
            seleccionado = tabSeleccionada == TabBarraOutlet.Bolsa,
            onClick = onBolsa,
        )
        ItemBarraInferior(
            "PERFIL",
            Icons.Default.Person,
            seleccionado = tabSeleccionada == TabBarraOutlet.Perfil,
            onClick = onPerfil,
        )
    }
}

@Composable
private fun ItemBarraInferior(
    etiqueta: String,
    icono: ImageVector,
    seleccionado: Boolean,
    onClick: () -> Unit,
) {
    val color = if (seleccionado) OlivaVintageOscuro else GrisSecundario
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(56.dp)
            .clickable { onClick() },
    ) {
        Icon(icono, contentDescription = etiqueta, tint = color, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            etiqueta,
            style = MaterialTheme.typography.labelSmall,
            color = color,
        )
    }
}
