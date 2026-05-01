package com.example.proyectomov.front

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import kotlin.collections.chunked
import kotlin.collections.forEach

@Composable
fun PantallaMenuInicioOutlet(
    navController: NavHostController,
    articulos: List<ArticuloOutlet>,
    marcas: List<Pair<String, Int>>,
    favoritos: List<String>,
    onToggleFavorito: (String) -> Unit,
) {
    var textoBusqueda by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            BarraNavegacionInferiorOutlet(
                onExplorar = { },
                onDeseos = { navController.navigate(RutaFavoritosOutlet) },
                onVender = { navController.navigate(RutaAgregarArticuloOutlet) },
                onBolsa = { },
                onPerfil = { },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White),
        ) {
            item {
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
            }
            item {
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
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(OlivaVintage)
                            .clickable { /* avance filtro */ },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filtro", tint = Color.White)
                    }
                }
            }
            item {
                Text(
                    text = "MARCAS DESTACADAS",
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
                    items(marcas) { (nombre, resId) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(resId),
                                contentDescription = nombre,
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, GrisBordeCampo, CircleShape),
                                contentScale = ContentScale.Crop,
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(nombre, style = MaterialTheme.typography.labelMedium)
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
                        "ÚLTIMOS ARTÍCULOS",
                        color = OlivaVintage,
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Text(
                        "VER TODO",
                        style = MaterialTheme.typography.labelSmall,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { /* avance */ },
                    )
                }
            }
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
                                        titulo = art.titulo,
                                        precioPesosEntero = art.precioPesosEntero,
                                        descripcion = art.descripcion,
                                        imagenResId = art.imagenResId,
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

@Composable
fun PantallaFavoritosOutlet(
    navController: NavHostController,
    articulos: List<ArticuloOutlet>,
    favoritos: List<String>,
) {
    val articulosFavoritos = remember(articulos, favoritos) {
        articulos.filter { it.idMostrar in favoritos }
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            BarraNavegacionInferiorOutlet(
                onExplorar = { navController.popBackStack() },
                onDeseos = { },
                onVender = { navController.navigate(RutaAgregarArticuloOutlet) },
                onBolsa = { },
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
                                    titulo = art.titulo,
                                    precioPesosEntero = art.precioPesosEntero,
                                    descripcion = art.descripcion,
                                    imagenResId = art.imagenResId,
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
            Image(
                painter = painterResource(articulo.imagenResId),
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
                Image(
                    painter = painterResource(articulo.imagenResId),
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
        ItemBarraInferior("EXPLORAR", Icons.Default.Explore, seleccionado = true, onClick = onExplorar)
        ItemBarraInferior("DESEOS", Icons.Default.FavoriteBorder, seleccionado = false, onClick = onDeseos)
        ItemBarraInferior("VENDER", Icons.Default.Add, seleccionado = false, onClick = onVender)
        ItemBarraInferior("BOLSA", Icons.Default.ShoppingBag, seleccionado = false, onClick = onBolsa)
        ItemBarraInferior(

            "PERFIL", Icons.Default.Person, seleccionado = false, onClick = onPerfil)
    }
}

@Composable
private fun ItemBarraInferior(
    etiqueta: String,
    icono: ImageVector,
    seleccionado: Boolean,
    onClick: () -> Unit,
) {
    val color = if (seleccionado) OlivaVintage else GrisSecundario
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(56.dp)
            .clickable { onClick() },
    ) {
        if (seleccionado) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(OlivaVintage.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icono, contentDescription = etiqueta, tint = OlivaVintage)
            }
        } else {
            Icon(icono, contentDescription = etiqueta, tint = color, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            etiqueta,
            style = MaterialTheme.typography.labelSmall,
            color = color,
        )
    }
}
