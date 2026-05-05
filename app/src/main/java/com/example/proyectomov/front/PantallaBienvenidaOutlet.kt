package com.example.proyectomov.front

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.proyectomov.back.FondoCrema
import com.example.proyectomov.back.GrisSecundario
import com.example.proyectomov.back.OlivaVintage

@Composable
fun PantallaBienvenidaOutlet(
    onIniciarSesion: () -> Unit,
    onRegistrarse: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoCrema)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Vintage Outlet",
            style = MaterialTheme.typography.displayLarge.copy(
                fontStyle = FontStyle.Italic,
            ),
            textAlign = TextAlign.Center,
            color = OlivaVintage,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Compra y vende piezas con historia",
            color = GrisSecundario,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = onIniciarSesion,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OlivaVintage),
        ) {
            Text(
                text = "INICIAR SESION",
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = onRegistrarse,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = OlivaVintage),
        ) {
            Text(
                text = "REGISTRARSE",
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}
