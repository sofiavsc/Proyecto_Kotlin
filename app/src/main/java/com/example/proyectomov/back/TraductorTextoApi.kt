package com.example.proyectomov.back

import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await

class TraductorTextoApi {

    private val translator: Translator = Translation.getClient(
        TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.SPANISH)
            .build(),
    )

    private val lock = Mutex()

    @Volatile
    private var modeloDescargado = false

    suspend fun prepararModeloSiNecesario() {
        if (modeloDescargado) return
        lock.withLock {
            if (modeloDescargado) return
            runCatching {
                translator.downloadModelIfNeeded().await()
            }.onSuccess {
                modeloDescargado = true
            }
        }
    }

    suspend fun traducirEnToEs(texto: String): String {
        if (texto.isBlank()) return texto
        prepararModeloSiNecesario()
        return runCatching {
            translator.translate(texto).await()
        }.getOrElse { texto }
    }

    fun cerrar() {
        runCatching { translator.close() }
    }
}
