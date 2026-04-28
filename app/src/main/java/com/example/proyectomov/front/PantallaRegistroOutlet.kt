package com.example.proyectomov.front

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PantallaRegistroOutlet(
    onVolver: () -> Unit = {},
    onRegistroExitoso: () -> Unit = {},
) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var contrasena by rememberSaveable { mutableStateOf("") }
    var confirmarContrasena by rememberSaveable { mutableStateOf("") }
    var mensajeError by rememberSaveable { mutableStateOf("") }

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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atras")
            }
            Text(
                text = "Registro",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier.padding(start = 8.dp),
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Crea tu cuenta para comprar y vender piezas vintage.",
            color = GrisSecundario,
            fontSize = 14.sp,
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("NOMBRE", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GrisBordeCampo,
                unfocusedBorderColor = GrisBordeCampo,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("EMAIL", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GrisBordeCampo,
                unfocusedBorderColor = GrisBordeCampo,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("CONTRASENA", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GrisBordeCampo,
                unfocusedBorderColor = GrisBordeCampo,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("CONFIRMAR CONTRASENA", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
        OutlinedTextField(
            value = confirmarContrasena,
            onValueChange = { confirmarContrasena = it },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
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
                fontSize = 13.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                val correoValido = correo.contains("@") && correo.length > 5
                val passValida = contrasena.length >= 3
                val passIguales = contrasena == confirmarContrasena

                mensajeError = when {
                    nombre.isBlank() -> "Escribe tu nombre."
                    !correoValido -> "Correo no valido."
                    !passValida -> "La contrasena debe tener al menos 3 caracteres."
                    !passIguales -> "Las contrasenas no coinciden."
                    else -> ""
                }

                if (mensajeError.isBlank()) onRegistroExitoso()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OlivaVintage),
            shape = RoundedCornerShape(6.dp),
        ) {
            Text(
                text = "CREAR CUENTA",
                color = Color.White,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text("Ya tienes cuenta? ")
            Text(
                text = "Inicia sesion",
                color = OlivaVintage,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onVolver() },
            )
        }
    }
}