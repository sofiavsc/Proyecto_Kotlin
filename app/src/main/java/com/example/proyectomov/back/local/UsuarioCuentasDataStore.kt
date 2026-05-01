package com.example.proyectomov.back.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.usuarioCuentasDataStore by preferencesDataStore(name = "usuario_cuentas_outlet")

private val KEY_USUARIOS_JSON = stringPreferencesKey("usuarios_json")

class UsuarioCuentasDataStore(context: Context) {
    private val dataStore = context.applicationContext.usuarioCuentasDataStore

    suspend fun leerTodos(): List<UsuarioCuentaStored> =
        dataStore.data
            .map { prefs ->
                val raw = prefs[KEY_USUARIOS_JSON].orEmpty()
                if (raw.isBlank()) emptyList()
                else runCatching { UsuarioCuentasJson.decode(raw) }.getOrElse { emptyList() }
            }.first()

    suspend fun guardarTodos(lista: List<UsuarioCuentaStored>) {
        dataStore.edit { prefs ->
            prefs[KEY_USUARIOS_JSON] =
                if (lista.isEmpty()) "" else UsuarioCuentasJson.encode(lista)
        }
    }
}
