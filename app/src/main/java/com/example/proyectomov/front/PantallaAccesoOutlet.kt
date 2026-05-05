package com.example.proyectomov.front

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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectomov.back.GrisBordeCampo
import com.example.proyectomov.back.GrisSecundario
import com.example.proyectomov.back.InicioSesionViewModel
import com.example.proyectomov.back.OlivaVintage
import com.example.proyectomov.ui.theme.ProyectoMovTheme

@Composable
fun PantallaAccesoOutlet(
    viewModel: InicioSesionViewModel,
    alEntrarOk: () -> Unit,
    onVolver: () -> Unit,
    onIrARegistro: () -> Unit = {},
) {
    PantallaAccesoOutletContenido(
        procesando = viewModel.procesando,
        mensajeError = viewModel.mensajeError,
        onIntentarEntrar = { c, p, cb -> viewModel.intentarEntrar(c, p, cb) },
        alEntrarOk = alEntrarOk,
        onVolver = onVolver,
        onIrARegistro = onIrARegistro,
    )
}

@Composable
private fun PantallaAccesoOutletContenido(
    procesando: Boolean,
    mensajeError: String,
    onIntentarEntrar: (String, String, (Boolean) -> Unit) -> Unit,
    alEntrarOk: () -> Unit,
    onVolver: () -> Unit,
    onIrARegistro: () -> Unit,
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var verContrasena by remember { mutableStateOf(false) }
    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scroll),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onVolver) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
            }
            Text(
                text = "Sign In",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.size(48.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 0.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF8B7355), Color(0xFF5C4A38), Color(0xFF3D2E22)),
                        ),
                    ),
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
            ) {
                Text(
                    text = "VINTAGE OUTLET",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Text(
                text = "Rediscover Style",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontStyle = FontStyle.Italic,
                ),
                color = Color.Black,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Log in to browse our curated second-hand archives.",
                style = MaterialTheme.typography.bodyMedium,
                color = GrisSecundario,
            )
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "EMAIL ADDRESS",
                style = MaterialTheme.typography.labelMedium,
            )
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("vintage@collector.com", color = GrisSecundario) },
                singleLine = true,
                shape = RoundedCornerShape(4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GrisBordeCampo,
                    unfocusedBorderColor = GrisBordeCampo,
                ),
            )
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "PASSWORD",
                    style = MaterialTheme.typography.labelMedium,
                )
                Text(
                    text = "Forgot?",
                    color = OlivaVintage,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(end = 4.dp),
                )
            }
            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (verContrasena) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { verContrasena = !verContrasena }) {
                        Icon(
                            imageVector = if (verContrasena) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null,
                        )
                    }
                },
                shape = RoundedCornerShape(4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GrisBordeCampo,
                    unfocusedBorderColor = GrisBordeCampo,
                ),
            )
            if (mensajeError.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = mensajeError,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    onIntentarEntrar(correo, contrasena) { ok ->
                        if (ok) alEntrarOk()
                    }
                },
                enabled = !procesando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OlivaVintage),
                shape = RoundedCornerShape(2.dp),
            ) {
                Text(
                    text = if (procesando) "VALIDANDO..." else "ENTER",
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = GrisBordeCampo)
                Text(
                    text = "  OR CONNECT WITH  ",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontStyle = FontStyle.Italic,
                    ),
                    color = GrisSecundario,
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = GrisBordeCampo)
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(4.dp),
                ) {
                    Text(
                        "GOOGLE",
                        color = Color.Black,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(4.dp),
                ) {
                    Text(
                        "APPLE",
                        color = Color.Black,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
            Spacer(modifier = Modifier.height(28.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    "Don't have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "Register Here",
                    color = OlivaVintage,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontStyle = FontStyle.Italic,
                    ),
                    modifier = Modifier.clickable { onIrARegistro() },
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, name = "Acceso · login")
@Composable
private fun PantallaAccesoOutletPreviewNormal() {
    ProyectoMovTheme {
        PantallaAccesoOutletContenido(
            procesando = false,
            mensajeError = "",
            onIntentarEntrar = { _, _, cb -> cb(false) },
            alEntrarOk = {},
            onVolver = {},
            onIrARegistro = {},
        )
    }
}

@Preview(showBackground = true, name = "Acceso · error")
@Composable
private fun PantallaAccesoOutletPreviewError() {
    ProyectoMovTheme {
        PantallaAccesoOutletContenido(
            procesando = false,
            mensajeError = "Correo o contraseña incorrectos.",
            onIntentarEntrar = { _, _, _ -> },
            alEntrarOk = {},
            onVolver = {},
            onIrARegistro = {},
        )
    }
}
