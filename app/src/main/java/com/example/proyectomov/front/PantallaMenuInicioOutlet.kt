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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.proyectomov.back.ArticuloOutlet
import com.example.proyectomov.back.MapeoCategoriaApi
import com.example.proyectomov.back.CarritoViewModel
import com.example.proyectomov.back.CategoriaDestacadaOutlet
import com.example.proyectomov.back.ItemCarritoOutlet
import com.example.proyectomov.back.FondoCrema
import com.example.proyectomov.back.GrisBordeCampo
import com.example.proyectomov.back.GrisSecundario
import com.example.proyectomov.back.OlivaVintage
import com.example.proyectomov.back.OlivaVintageOscuro
import com.example.proyectomov.back.ProductoImagenConShimmerOutlet
import com.example.proyectomov.R
import java.util.Locale
import kotlin.collections.chunked
import kotlin.collections.forEach

private const val ARTICULOS_INICIO_MAX = 10

internal enum class TabBarraOutlet {
    Explorar,
    Deseos,
    Vender,
    Bolsa,
    Perfil,
}

internal inline fun <reified T : Any> navegarTabInferior(
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
    val recursos = LocalContext.current.resources
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
                onBolsa = onIrCarrito,
                onPerfil = { navegarTabInferior(navController, RutaPerfilOutlet) },
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
                        IconButton(onClick = { navController.navigate(RutaAjustesOutlet) }) {
                            Icon(Icons.Default.Menu, contentDescription = stringResource(R.string.cd_menu))
                        }
                        Text(
                            text = stringResource(R.string.brand_title),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontStyle = FontStyle.Italic,
                            ),
                        )
                        IconButton(
                            onClick = { navegarTabInferior(navController, RutaPerfilOutlet) },
                        ) {
                            Icon(Icons.Default.Person, contentDescription = stringResource(R.string.cd_profile))
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
                            placeholder = {
                                Text(stringResource(R.string.search_placeholder), color = GrisSecundario)
                            },
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
                                Icon(
                                    Icons.Default.FilterList,
                                    contentDescription = stringResource(R.string.cd_filter),
                                    tint = Color.White,
                                )
                            }
                            DropdownMenu(
                                expanded = mostrarFiltroCategorias,
                                onDismissRequest = { mostrarFiltroCategorias = false },
                                modifier = Modifier.heightIn(max = 320.dp),
                            ) {
                                if (hayFiltroCategoriaActivo) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(stringResource(R.string.clear_filters), color = OlivaVintage)
                                        },
                                        onClick = {
                                            categoriasSeleccionadas = emptySet()
                                        },
                                    )
                                }
                                if (todasLasCategorias.isEmpty()) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(stringResource(R.string.no_categories), color = GrisSecundario)
                                        },
                                        onClick = { },
                                        enabled = false,
                                    )
                                } else {
                                    todasLasCategorias.forEach { categoria ->
                                        val marcada = categoriasSeleccionadas.any {
                                            it.equals(categoria, ignoreCase = true)
                                        }
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    MapeoCategoriaApi.etiquetaMostrar(recursos, categoria),
                                                )
                                            },
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
                    text = stringResource(R.string.section_categories),
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
                            hayBusquedaActiva && hayFiltroCategoriaActivo ->
                                stringResource(R.string.subtitle_results_both)
                            hayBusquedaActiva ->
                                stringResource(R.string.subtitle_results_search)
                            hayFiltroCategoriaActivo ->
                                stringResource(R.string.subtitle_results_category)
                            else -> stringResource(R.string.subtitle_latest)
                        },
                        color = OlivaVintage,
                        style = MaterialTheme.typography.labelMedium,
                    )
                    if (!hayBusquedaActiva && !hayFiltroCategoriaActivo) {
                        Text(
                            stringResource(R.string.see_all),
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
                            stringResource(R.string.empty_no_match_both)
                        } else if (hayBusquedaActiva) {
                            stringResource(R.string.empty_no_match_search)
                        } else {
                            stringResource(R.string.empty_no_match_category)
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
                        stringResource(R.string.title_all_items),
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                        )
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
                onBolsa = onIrCarrito,
                onPerfil = { navegarTabInferior(navController, RutaPerfilOutlet) },
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
    val recursosCat = LocalContext.current.resources
    val articulosCategoria = remember(articulos, categoria) {
        articulos.filter { it.categoria.equals(categoria, ignoreCase = true) }
    }
    val tituloBarra = remember(categoria, recursosCat) {
        MapeoCategoriaApi.etiquetaMostrar(recursosCat, categoria)
    }
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
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                        )
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
                onBolsa = onIrCarrito,
                onPerfil = { navegarTabInferior(navController, RutaPerfilOutlet) },
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
                    text = stringResource(R.string.title_no_items_category),
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
                onBolsa = onIrCarrito,
                onPerfil = { navegarTabInferior(navController, RutaPerfilOutlet) },
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
                    text = stringResource(R.string.title_favorite_items),
                    color = OlivaVintage,
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            if (articulosFavoritos.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.empty_favorites),
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
                onPerfil = { navegarTabInferior(navController, RutaPerfilOutlet) },
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
                            text = stringResource(R.string.title_your_cart),
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
                                text = stringResource(R.string.empty_cart),
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
    val titulo = articulo?.titulo
        ?: stringResource(R.string.product_fallback, linea.productId)
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
                    contentDescription = stringResource(R.string.cd_remove_from_cart),
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
                    text = stringResource(R.string.id_label_format, articulo.idMostrar),
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
                contentDescription = stringResource(R.string.cd_favorite),
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
                        contentDescription = stringResource(R.string.cd_favorite),
                        tint = if (esFavorito) OlivaVintage else GrisSecundario,
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                stringResource(R.string.id_label_format, articulo.idMostrar),
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
internal fun BarraNavegacionInferiorOutlet(
    tabSeleccionada: TabBarraOutlet,
    onExplorar: () -> Unit,
    onDeseos: () -> Unit,
    onVender: () -> Unit,
    onBolsa: () -> Unit,
    onPerfil: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(1.dp, GrisBordeCampo)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ItemBarraInferior(
            stringResource(R.string.nav_explore),
            Icons.Default.Explore,
            seleccionado = tabSeleccionada == TabBarraOutlet.Explorar,
            onClick = onExplorar,
            modifier = Modifier.weight(1f),
        )
        ItemBarraInferior(
            stringResource(R.string.nav_wishlist),
            Icons.Default.FavoriteBorder,
            seleccionado = tabSeleccionada == TabBarraOutlet.Deseos,
            onClick = onDeseos,
            modifier = Modifier.weight(1f),
        )
        ItemBarraInferior(
            stringResource(R.string.nav_sell),
            Icons.Default.Add,
            seleccionado = tabSeleccionada == TabBarraOutlet.Vender,
            onClick = onVender,
            modifier = Modifier.weight(1f),
        )
        ItemBarraInferior(
            stringResource(R.string.nav_bag),
            Icons.Default.ShoppingBag,
            seleccionado = tabSeleccionada == TabBarraOutlet.Bolsa,
            onClick = onBolsa,
            modifier = Modifier.weight(1f),
        )
        ItemBarraInferior(
            stringResource(R.string.nav_profile_bar),
            Icons.Default.Person,
            seleccionado = tabSeleccionada == TabBarraOutlet.Perfil,
            onClick = onPerfil,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
internal fun ItemBarraInferior(
    etiqueta: String,
    icono: ImageVector,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = if (seleccionado) OlivaVintageOscuro else GrisSecundario
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
    ) {
        Icon(icono, contentDescription = etiqueta, tint = color, modifier = Modifier.size(26.dp))
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            etiqueta,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                lineHeight = 11.sp,
            ),
            color = color,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Clip,
        )
    }
}
