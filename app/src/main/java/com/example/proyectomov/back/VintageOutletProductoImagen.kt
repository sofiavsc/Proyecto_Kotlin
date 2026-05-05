package com.example.proyectomov.back

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.proyectomov.R

/**
 * [AsyncImage] con [ImageRequest] para que Coil calcule el tamaño según el layout.
 * Solo [String] con rememberAsyncImagePainter no infiere tamaño y falla con frecuencia.
 */
@Composable
fun ProductoImagenConShimmerOutlet(
    imagenUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale,
) {
    val url = imagenUrl.trim()
    if (url.isEmpty()) {
        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale,
        )
        return
    }

    val context = LocalContext.current
    val request: ImageRequest = remember(url, context) {
        ImageRequest.Builder(context)
            .data(url)
            .build()
    }

    val fondoPlaceholder = remember { ColorPainter(FondoCrema) }

    AsyncImage(
        model = request,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        placeholder = fondoPlaceholder,
        error = fondoPlaceholder,
    )
}
