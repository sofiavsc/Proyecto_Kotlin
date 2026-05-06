package com.example.proyectomov.back.local

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

/**
 * Persiste español / inglés y aplica [AppCompatDelegate.setApplicationLocales] al arranque.
 */
object PreferenciasIdiomaApp {

    private const val PREFS = "vintage_outlet_app_prefs"
    private const val KEY_LANGUAGE_TAG = "language_tag"

    const val TAG_ES = "es"
    const val TAG_EN = "en"

    fun guardar(context: Context, languageTag: String) {
        context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE_TAG, languageTag)
            .apply()
    }

    fun obtenerTagGuardado(context: Context): String? =
        context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE_TAG, null)

    fun aplicarSiGuardado(context: Context) {
        val tag = obtenerTagGuardado(context) ?: return
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
    }

    /**
     * Resuelve "es" o "en" para mostrar en UI (preferencia guardada, delegado de AppCompat o sistema).
     */
    fun tagIdiomaResuelto(context: Context): String {
        obtenerTagGuardado(context)?.let { return it }
        val app = AppCompatDelegate.getApplicationLocales()
        if (!app.isEmpty()) {
            return when (app[0]?.language) {
                TAG_EN -> TAG_EN
                else -> TAG_ES
            }
        }
        val sys = context.resources.configuration.locales[0]?.language ?: TAG_ES
        return if (sys == TAG_EN) TAG_EN else TAG_ES
    }
}

/**
 * Misma configuración de idioma que la UI ([AppCompatDelegate] / preferencia guardada).
 * [android.app.Application.getString] suele usar solo el idioma del sistema.
 */
fun Context.contextoLocalizadoApp(): Context {
    val base = applicationContext
    val locales: LocaleListCompat = when {
        !AppCompatDelegate.getApplicationLocales().isEmpty ->
            AppCompatDelegate.getApplicationLocales()
        else -> {
            val tag = PreferenciasIdiomaApp.obtenerTagGuardado(base) ?: return this
            LocaleListCompat.forLanguageTags(tag)
        }
    }
    if (locales.isEmpty) return this
    val configuration = Configuration(base.resources.configuration)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        when (val unwrapped = locales.unwrap()) {
            is LocaleList -> configuration.setLocales(unwrapped)
            else -> return this
        }
    } else {
        @Suppress("DEPRECATION")
        configuration.locale = locales[0] ?: return this
    }
    return base.createConfigurationContext(configuration)
}
