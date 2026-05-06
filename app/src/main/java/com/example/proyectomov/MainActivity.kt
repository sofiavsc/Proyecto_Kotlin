package com.example.proyectomov

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import com.example.proyectomov.back.local.PreferenciasIdiomaApp
import com.example.proyectomov.front.NavegacionVintageOutlet
import com.example.proyectomov.ui.theme.ProyectoMovTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        PreferenciasIdiomaApp.aplicarSiGuardado(this)
        super.onCreate(savedInstanceState)
        SingletonImageLoader.setSafe { ctx ->
            ImageLoader.Builder(ctx)
                .components {
                    add(OkHttpNetworkFetcherFactory())
                }
                .build()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            enableEdgeToEdge()
        }
        setContent {
            val locales = LocalConfiguration.current.locales
            key(locales[0]?.toLanguageTag().orEmpty()) {
                ProyectoMovTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        NavegacionVintageOutlet(
                            innerPadding = innerPadding,
                        )
                    }
                }
            }
        }
    }
}