package com.example.proyectomov.front

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.proyectomov.back.CarritoViewModel
import com.example.proyectomov.back.FondoCrema
import com.example.proyectomov.back.GrisSecundario
import com.example.proyectomov.back.OlivaVintage
import com.example.proyectomov.back.ProductoImagenConShimmerOutlet
import com.example.proyectomov.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleArticuloOutlet(
    datos: RutaDetalleArticulo,
    navController: NavHostController,
    esFavorito: Boolean,
    onToggleFavorito: () -> Unit,
    carritoViewModel: CarritoViewModel,
    productIdApi: Int,
    contexto: Context,
) {
    val scroll = rememberScrollState()
    val precioTexto = "$" + datos.precioPesosEntero + ".00"
    val operando by carritoViewModel::operando

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.title_article_detail),
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Button(
                    onClick = {
                        if (productIdApi <= 0) return@Button
                        carritoViewModel.agregarProducto(productIdApi) { _, mensaje ->
                            Toast.makeText(
                                contexto,
                                mensaje,
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    },
                    enabled = productIdApi > 0 && !operando,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OlivaVintage),
                    shape = RoundedCornerShape(50),
                ) {
                    if (operando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            stringResource(R.string.add_to_cart),
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.Black, CircleShape)
                        .background(if (esFavorito) OlivaVintage.copy(alpha = 0.12f) else Color.White)
                        .clickable(onClick = onToggleFavorito),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = stringResource(R.string.cd_favorite),
                        tint = if (esFavorito) OlivaVintage else GrisSecundario,
                    )
                }
            }
        },
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .verticalScroll(scroll),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(FondoCrema),
                contentAlignment = Alignment.Center,
            ) {
                ProductoImagenConShimmerOutlet(
                    imagenUrl = datos.imagenUrl,
                    contentDescription = datos.titulo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .padding(12.dp),
                    contentScale = ContentScale.Fit,
                )
            }
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Text(
                    text = datos.titulo,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontStyle = FontStyle.Italic,
                    ),
                    color = Color.Black,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Black)
                        .padding(12.dp),
                ) {
                    Text(
                        stringResource(R.string.price_final_label),
                        style = MaterialTheme.typography.labelMedium,
                        color = GrisSecundario,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        precioTexto,
                        style = MaterialTheme.typography.headlineLarge,
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    datos.descripcion,
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
