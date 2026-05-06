package com.example.proyectomov.front

import androidx.compose.foundation.background
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.proyectomov.R
import com.example.proyectomov.back.CatalogoOutletViewModel
import com.example.proyectomov.back.GrisBordeCampo
import com.example.proyectomov.back.MapeoCategoriaApi
import com.example.proyectomov.back.GrisSecundario
import com.example.proyectomov.back.OlivaVintage
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAgregarArticuloOutlet(
    viewModel: CatalogoOutletViewModel,
    onVolver: () -> Unit = {},
    onPublicado: () -> Unit = {},
) {
    var titulo by rememberSaveable { mutableStateOf("") }
    var categoriaSeleccionada by rememberSaveable { mutableStateOf("") }
    var menuCategoriaExpandido by rememberSaveable { mutableStateOf(false) }
    var precio by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var imagenUrl by rememberSaveable { mutableStateOf("") }
    var mensajeError by rememberSaveable { mutableStateOf("") }

    val scroll = rememberScrollState()
    val context = LocalContext.current

    val categorias = viewModel.categorias
    val cargando = viewModel.cargando
    val publicando = viewModel.publicando

    var urlParaVistaPrevia by remember { mutableStateOf("") }
    LaunchedEffect(imagenUrl) {
        val t = imagenUrl.trim()
        if (t.isBlank()) {
            urlParaVistaPrevia = ""
            return@LaunchedEffect
        }
        delay(400)
        if (imagenUrl.trim() == t) {
            urlParaVistaPrevia = t
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scroll)
            .padding(20.dp),
    ) {
        IconButton(onClick = onVolver) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.cd_back),
            )
        }

        Text(
            text = stringResource(R.string.publish_title),
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.publish_subtitle),
            color = GrisSecundario,
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(stringResource(R.string.pub_field_title), style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                imeAction = ImeAction.Next,
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GrisBordeCampo,
                unfocusedBorderColor = GrisBordeCampo,
            ),
            placeholder = { Text(stringResource(R.string.pub_placeholder_title)) },
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(stringResource(R.string.pub_field_category), style = MaterialTheme.typography.labelMedium)
        when {
            cargando && categorias.isEmpty() -> {
                Text(
                    text = stringResource(R.string.pub_categories_loading),
                    color = GrisSecundario,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            categorias.isEmpty() -> {
                Text(
                    text = stringResource(R.string.pub_categories_empty),
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            else -> {
                ExposedDropdownMenuBox(
                    expanded = menuCategoriaExpandido,
                    onExpandedChange = { menuCategoriaExpandido = it },
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        readOnly = true,
                        value = categoriaSeleccionada.takeIf { it.isNotBlank() }
                            ?.let { slug -> MapeoCategoriaApi.etiquetaMostrar(context.resources, slug) }
                            .orEmpty(),
                        onValueChange = {},
                        placeholder = { Text(stringResource(R.string.pub_placeholder_category)) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuCategoriaExpandido)
                        },
                        shape = RoundedCornerShape(6.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GrisBordeCampo,
                            unfocusedBorderColor = GrisBordeCampo,
                        ),
                    )
                    ExposedDropdownMenu(
                        expanded = menuCategoriaExpandido,
                        onDismissRequest = { menuCategoriaExpandido = false },
                    ) {
                        categorias.forEach { cat ->
                            DropdownMenuItem(
                                text = {
                                    Text(MapeoCategoriaApi.etiquetaMostrar(context.resources, cat))
                                },
                                onClick = {
                                    categoriaSeleccionada = cat
                                    menuCategoriaExpandido = false
                                },
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(stringResource(R.string.pub_field_price), style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it.filter { ch -> ch.isDigit() } },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                autoCorrectEnabled = false,
                imeAction = ImeAction.Next,
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GrisBordeCampo,
                unfocusedBorderColor = GrisBordeCampo,
            ),
            placeholder = { Text(stringResource(R.string.pub_placeholder_price)) },
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(stringResource(R.string.pub_field_desc), style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                imeAction = ImeAction.Default,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            shape = RoundedCornerShape(6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GrisBordeCampo,
                unfocusedBorderColor = GrisBordeCampo,
            ),
            placeholder = { Text(stringResource(R.string.pub_placeholder_desc)) },
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(stringResource(R.string.pub_field_image), style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = imagenUrl,
            onValueChange = { imagenUrl = it },
            singleLine = false,
            maxLines = 3,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                autoCorrectEnabled = false,
                imeAction = ImeAction.Done,
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GrisBordeCampo,
                unfocusedBorderColor = GrisBordeCampo,
            ),
            placeholder = { Text(stringResource(R.string.pub_placeholder_image)) },
        )

        val urlPreview = urlParaVistaPrevia.takeIf { it.isNotBlank() }
        if (urlPreview != null) {
            Spacer(modifier = Modifier.height(12.dp))
            val previewRequest: ImageRequest = remember(urlPreview, context) {
                ImageRequest.Builder(context)
                    .data(urlPreview)
                    .crossfade(false)
                    .build()
            }
            AsyncImage(
                model = previewRequest,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (mensajeError.isNotBlank()) {
            Text(
                text = mensajeError,
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (viewModel.errorPublicacion.isNotBlank()) {
            Text(
                text = viewModel.errorPublicacion,
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                mensajeError = when {
                    titulo.isBlank() -> context.getString(R.string.pub_err_title)
                    categorias.isEmpty() || categoriaSeleccionada.isBlank() ->
                        context.getString(R.string.pub_err_category)
                    precio.isBlank() -> context.getString(R.string.pub_err_price)
                    descripcion.isBlank() -> context.getString(R.string.pub_err_desc)
                    imagenUrl.isBlank() -> context.getString(R.string.pub_err_image)
                    else -> ""
                }
                if (mensajeError.isNotBlank()) return@Button

                val precioNum = precio.toDoubleOrNull()
                if (precioNum == null || precioNum <= 0.0) {
                    mensajeError = context.getString(R.string.pub_err_price)
                    return@Button
                }

                viewModel.publicarArticulo(
                    titulo = titulo,
                    precio = precioNum,
                    descripcion = descripcion,
                    imagenUrl = imagenUrl,
                    categoria = categoriaSeleccionada,
                    onExito = onPublicado,
                )
            },
            enabled = !publicando && categorias.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OlivaVintage),
            shape = RoundedCornerShape(6.dp),
        ) {
            if (publicando) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        color = Color.White,
                        strokeWidth = 2.dp,
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.pub_publish_btn),
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}
