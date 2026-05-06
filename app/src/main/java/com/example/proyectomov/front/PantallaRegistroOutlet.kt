package com.example.proyectomov.front

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectomov.back.GrisBordeCampo
import com.example.proyectomov.back.GrisSecundario
import com.example.proyectomov.back.OlivaVintage
import com.example.proyectomov.R
import com.example.proyectomov.back.RegistroViewModel
import com.example.proyectomov.ui.theme.ProyectoMovTheme

@Composable
fun PantallaRegistroOutlet(
    viewModel: RegistroViewModel,
    onVolver: () -> Unit = {},
    onRegistroExitoso: () -> Unit = {},
) {
    PantallaRegistroOutletContenido(
        procesando = viewModel.procesando,
        mensajeError = viewModel.mensajeError,
        onRegistrar = { u, c, p, cp, cb ->
            viewModel.registrar(u, c, p, cp, cb)
        },
        onVolver = onVolver,
        onRegistroExitoso = onRegistroExitoso,
    )
}

@Composable
private fun PantallaRegistroOutletContenido(
    procesando: Boolean,
    mensajeError: String,
    onRegistrar: (String, String, String, String, (Boolean) -> Unit) -> Unit,
    onVolver: () -> Unit,
    onRegistroExitoso: () -> Unit,
) {
    var username by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var contrasena by rememberSaveable { mutableStateOf("") }
    var confirmarContrasena by rememberSaveable { mutableStateOf("") }
    var verContrasena by remember { mutableStateOf(false) }
    var verConfirmarContrasena by remember { mutableStateOf(false) }

    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scroll)
            .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onVolver) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back),
                )
            }
            Text(
                text = stringResource(R.string.register_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 8.dp),
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.register_subtitle),
            color = GrisSecundario,
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(stringResource(R.string.field_username), style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GrisBordeCampo,
                unfocusedBorderColor = GrisBordeCampo,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(stringResource(R.string.field_email), style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                autoCorrectEnabled = false,
                imeAction = ImeAction.Next,
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GrisBordeCampo,
                unfocusedBorderColor = GrisBordeCampo,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(stringResource(R.string.field_password), style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next,
            ),
            visualTransformation =
                if (verContrasena) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { verContrasena = !verContrasena }) {
                    Icon(
                        imageVector = if (verContrasena) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null,
                    )
                }
            },
            shape = RoundedCornerShape(6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GrisBordeCampo,
                unfocusedBorderColor = GrisBordeCampo,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(stringResource(R.string.field_confirm_password), style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = confirmarContrasena,
            onValueChange = { confirmarContrasena = it },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            visualTransformation =
                if (verConfirmarContrasena) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { verConfirmarContrasena = !verConfirmarContrasena }) {
                    Icon(
                        imageVector =
                            if (verConfirmarContrasena) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null,
                    )
                }
            },
            shape = RoundedCornerShape(6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GrisBordeCampo,
                unfocusedBorderColor = GrisBordeCampo,
            ),
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (mensajeError.isNotBlank()) {
            Text(
                text = mensajeError,
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                onRegistrar(username, correo, contrasena, confirmarContrasena) { ok ->
                    if (ok) onRegistroExitoso()
                }
            },
            enabled = !procesando,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OlivaVintage),
            shape = RoundedCornerShape(6.dp),
        ) {
            Text(
                text = if (procesando) {
                    stringResource(R.string.registering)
                } else {
                    stringResource(R.string.create_account)
                },
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(stringResource(R.string.have_account), style = MaterialTheme.typography.bodyMedium)
            Text(
                text = stringResource(R.string.sign_in_prompt),
                color = OlivaVintage,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable { onVolver() },
            )
        }
    }
}

@Preview(showBackground = true, name = "Registro")
@Composable
private fun PantallaRegistroOutletPreview() {
    ProyectoMovTheme {
        PantallaRegistroOutletContenido(
            procesando = false,
            mensajeError = "",
            onRegistrar = { _, _, _, _, cb -> cb(false) },
            onVolver = {},
            onRegistroExitoso = {},
        )
    }
}