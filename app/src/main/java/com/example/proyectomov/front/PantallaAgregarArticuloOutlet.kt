package com.example.proyectomov.front

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PantallaAgregarArticuloOutlet(
    onVolver: () -> Unit = {},
    onContinuarDetalle: () -> Unit = {},
) {
    var titulo by rememberSaveable { mutableStateOf("") }
    var categoria by rememberSaveable { mutableStateOf("") }
    var precio by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var mensajeError by rememberSaveable { mutableStateOf("") }

    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scroll)
            .padding(20.dp),
    ) {
        IconButton(onClick = onVolver) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atras")
        }

        Text(
            text = "Publicar Articulo",
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Ingresa la informacion principal de tu prenda para crear la publicacion.",
            color = GrisSecundario,
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text("TITULO", style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GrisBordeCampo,
                unfocusedBorderColor = GrisBordeCampo,
            ),
            placeholder = { Text("Ej. Chaqueta aviador 70s") },
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("CATEGORIA", style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = categoria,
            onValueChange = { categoria = it },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GrisBordeCampo,
                unfocusedBorderColor = GrisBordeCampo,
            ),
            placeholder = { Text("Chaquetas, Denim, Calzado...") },
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("PRECIO (MXN)", style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it.filter { ch -> ch.isDigit() } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GrisBordeCampo,
                unfocusedBorderColor = GrisBordeCampo,
            ),
            placeholder = { Text("Ej. 850") },
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("DESCRIPCION", style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            shape = RoundedCornerShape(6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GrisBordeCampo,
                unfocusedBorderColor = GrisBordeCampo,
            ),
            placeholder = { Text("Estado, talla, detalles relevantes...") },
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
                mensajeError = when {
                    titulo.isBlank() -> "Agrega un titulo."
                    categoria.isBlank() -> "Agrega una categoria."
                    precio.isBlank() -> "Agrega un precio."
                    descripcion.isBlank() -> "Agrega una descripcion."
                    else -> ""
                }
                if (mensajeError.isBlank()) onContinuarDetalle()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OlivaVintage),
            shape = RoundedCornerShape(6.dp),
        ) {
            Text(
                text = "CONTINUAR",
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}