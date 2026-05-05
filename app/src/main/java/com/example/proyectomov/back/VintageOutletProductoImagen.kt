package com.example.proyectomov.back

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.example.proyectomov.R

@Composable
fun ProductoImagenConShimmerOutlet(
    imagenUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale,
) {
    if (imagenUrl.isBlank()) {
        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale,
        )
        return
    }

    val painter = rememberAsyncImagePainter(imagenUrl)
    Box(modifier = modifier) {
        when (painter.state) {
            is AsyncImagePainter.State.Loading,
            is AsyncImagePainter.State.Empty ->
                ShimmerPlaceholderOutlet(modifier = Modifier.fillMaxSize())
            is AsyncImagePainter.State.Error -> {
                Image(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale,
                )
            }
            is AsyncImagePainter.State.Success -> {
                Image(
                    painter = painter,
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale,
                )
            }
        }
    }
}

@Composable
private fun ShimmerPlaceholderOutlet(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmerOutlet")
    val translate by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerValue",
    )
    val shimmerColors =
        listOf(
            FondoCrema.copy(alpha = 0.45f),
            Color.White.copy(alpha = 0.9f),
            FondoCrema.copy(alpha = 0.45f),
        )
    Box(
        modifier = modifier.background(
            Brush.linearGradient(
                colors = shimmerColors,
                start = Offset(translate - 300f, 0f),
                end = Offset(translate, 220f),
            ),
        ),
    )
}
