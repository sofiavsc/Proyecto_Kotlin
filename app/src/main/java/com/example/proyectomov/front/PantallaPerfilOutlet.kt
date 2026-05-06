package com.example.proyectomov.front

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.example.proyectomov.back.GrisSecundario
import com.example.proyectomov.back.OlivaVintage
import com.example.proyectomov.back.PerfilViewModel
import com.example.proyectomov.R

private enum class DialogoEdicionPerfil {
    Ninguno,
    Username,
    Email,
    Password,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPerfilOutlet(
    navController: NavHostController,
    viewModel: PerfilViewModel,
) {
    val contexto = LocalContext.current
    val dialogo = remember { mutableStateOf(DialogoEdicionPerfil.Ninguno) }
    val textoUsername = remember { mutableStateOf("") }
    val textoEmail = remember { mutableStateOf("") }
    val passActual = remember { mutableStateOf("") }
    val passNueva = remember { mutableStateOf("") }
    val passNueva2 = remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            BarraNavegacionInferiorOutlet(
                tabSeleccionada = TabBarraOutlet.Perfil,
                onExplorar = { navegarTabInferior(navController, RutaMenuInicioOutlet) },
                onDeseos = { navegarTabInferior(navController, RutaFavoritosOutlet) },
                onVender = { navController.navigate(RutaAgregarArticuloOutlet) },
                onBolsa = { navegarTabInferior(navController, RutaCarritoOutlet) },
                onPerfil = { navegarTabInferior(navController, RutaPerfilOutlet) },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when {
                viewModel.cargando -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = OlivaVintage,
                    )
                }
                viewModel.errorCarga.isNotEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = viewModel.errorCarga,
                            style = MaterialTheme.typography.bodyLarge,
                            color = GrisSecundario,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { navController.navigate(RutaAccesoOutlet) }) {
                            Text(stringResource(R.string.profile_go_login))
                        }
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = stringResource(R.string.profile_label_user),
                            modifier = Modifier.size(88.dp),
                            tint = OlivaVintage,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.profile_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = OlivaVintage,
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        FilaPerfilEditable(
                            etiqueta = stringResource(R.string.profile_label_user),
                            valor = viewModel.username,
                            onEditar = {
                                textoUsername.value = viewModel.username
                                dialogo.value = DialogoEdicionPerfil.Username
                            },
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        FilaPerfilEditable(
                            etiqueta = stringResource(R.string.profile_label_email),
                            valor = viewModel.email,
                            onEditar = {
                                textoEmail.value = viewModel.email
                                dialogo.value = DialogoEdicionPerfil.Email
                            },
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        FilaPerfilEditable(
                            etiqueta = stringResource(R.string.profile_label_password),
                            valor = stringResource(R.string.profile_password_masked),
                            onEditar = {
                                passActual.value = ""
                                passNueva.value = ""
                                passNueva2.value = ""
                                dialogo.value = DialogoEdicionPerfil.Password
                            },
                        )
                    }
                }
            }
        }
    }

    if (dialogo.value == DialogoEdicionPerfil.Username) {
        AlertDialog(
            onDismissRequest = { dialogo.value = DialogoEdicionPerfil.Ninguno },
            title = { Text(stringResource(R.string.dlg_change_username)) },
            text = {
                OutlinedTextField(
                    value = textoUsername.value,
                    onValueChange = { textoUsername.value = it },
                    label = { Text(stringResource(R.string.field_username_dlg)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.guardarUsername(textoUsername.value) { ok, msg ->
                            Toast.makeText(contexto, msg, Toast.LENGTH_SHORT).show()
                            if (ok) dialogo.value = DialogoEdicionPerfil.Ninguno
                        }
                    },
                    enabled = !viewModel.guardando,
                ) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { dialogo.value = DialogoEdicionPerfil.Ninguno }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    if (dialogo.value == DialogoEdicionPerfil.Email) {
        AlertDialog(
            onDismissRequest = { dialogo.value = DialogoEdicionPerfil.Ninguno },
            title = { Text(stringResource(R.string.dlg_change_email)) },
            text = {
                OutlinedTextField(
                    value = textoEmail.value,
                    onValueChange = { textoEmail.value = it },
                    label = { Text(stringResource(R.string.field_email_dlg)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.guardarEmail(textoEmail.value) { ok, msg ->
                            Toast.makeText(contexto, msg, Toast.LENGTH_SHORT).show()
                            if (ok) dialogo.value = DialogoEdicionPerfil.Ninguno
                        }
                    },
                    enabled = !viewModel.guardando,
                ) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { dialogo.value = DialogoEdicionPerfil.Ninguno }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    if (dialogo.value == DialogoEdicionPerfil.Password) {
        AlertDialog(
            onDismissRequest = { dialogo.value = DialogoEdicionPerfil.Ninguno },
            title = { Text(stringResource(R.string.dlg_change_password)) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    OutlinedTextField(
                        value = passActual.value,
                        onValueChange = { passActual.value = it },
                        label = { Text(stringResource(R.string.field_pass_current)) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = passNueva.value,
                        onValueChange = { passNueva.value = it },
                        label = { Text(stringResource(R.string.field_pass_new)) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = passNueva2.value,
                        onValueChange = { passNueva2.value = it },
                        label = { Text(stringResource(R.string.field_pass_repeat)) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (passNueva.value != passNueva2.value) {
                            Toast.makeText(
                                contexto,
                                contexto.getString(R.string.toast_new_password_mismatch),
                                Toast.LENGTH_SHORT,
                            ).show()
                            return@TextButton
                        }
                        viewModel.guardarContrasena(passActual.value, passNueva.value) { ok, msg ->
                            Toast.makeText(contexto, msg, Toast.LENGTH_SHORT).show()
                            if (ok) dialogo.value = DialogoEdicionPerfil.Ninguno
                        }
                    },
                    enabled = !viewModel.guardando,
                ) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { dialogo.value = DialogoEdicionPerfil.Ninguno }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun FilaPerfilEditable(
    etiqueta: String,
    valor: String,
    onEditar: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = etiqueta.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = GrisSecundario,
            )
            Text(
                text = valor,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        IconButton(onClick = onEditar) {
            Icon(
                Icons.Default.Edit,
                contentDescription = stringResource(R.string.cd_edit_field, etiqueta),
                tint = OlivaVintage,
            )
        }
    }
}
