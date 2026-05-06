package com.example.proyectomov.front

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.proyectomov.R
import com.example.proyectomov.back.GrisSecundario
import com.example.proyectomov.back.OlivaVintage
import com.example.proyectomov.back.local.PreferenciasIdiomaApp
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAjustesOutlet(
    navController: NavHostController,
    onIdiomaCambiado: () -> Unit = {},
) {
    val context = LocalContext.current
    var tagActual by remember {
        mutableStateOf(PreferenciasIdiomaApp.tagIdiomaResuelto(context))
    }
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.settings_title),
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
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.settings_language),
                style = MaterialTheme.typography.titleSmall,
                color = GrisSecundario,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { mostrarDialogo = true }
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = nombreIdiomaParaTag(tagActual),
                    style = MaterialTheme.typography.bodyLarge,
                    color = OlivaVintage,
                )
                Text(
                    text = stringResource(R.string.settings_tap_to_change),
                    style = MaterialTheme.typography.labelSmall,
                    color = GrisSecundario,
                )
            }
            HorizontalDivider()
        }
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text(stringResource(R.string.settings_choose_language)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilaIdioma(
                        etiqueta = stringResource(R.string.language_name_es),
                        seleccionado = tagActual == PreferenciasIdiomaApp.TAG_ES,
                        onSelect = {
                            aplicarIdioma(context, PreferenciasIdiomaApp.TAG_ES, onIdiomaCambiado) {
                                tagActual = PreferenciasIdiomaApp.TAG_ES
                                mostrarDialogo = false
                            }
                        },
                    )
                    FilaIdioma(
                        etiqueta = stringResource(R.string.language_name_en),
                        seleccionado = tagActual == PreferenciasIdiomaApp.TAG_EN,
                        onSelect = {
                            aplicarIdioma(context, PreferenciasIdiomaApp.TAG_EN, onIdiomaCambiado) {
                                tagActual = PreferenciasIdiomaApp.TAG_EN
                                mostrarDialogo = false
                            }
                        },
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun nombreIdiomaParaTag(tag: String): String =
    when (tag) {
        PreferenciasIdiomaApp.TAG_EN -> stringResource(R.string.language_name_en)
        else -> stringResource(R.string.language_name_es)
    }

@Composable
private fun FilaIdioma(
    etiqueta: String,
    seleccionado: Boolean,
    onSelect: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        RadioButton(
            selected = seleccionado,
            onClick = onSelect,
        )
        Text(etiqueta, style = MaterialTheme.typography.bodyLarge)
    }
}

private fun aplicarIdioma(
    context: android.content.Context,
    tag: String,
    onIdiomaCambiado: () -> Unit,
    alTerminar: () -> Unit,
) {
    PreferenciasIdiomaApp.guardar(context, tag)
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
    onIdiomaCambiado()
    alTerminar()
}
