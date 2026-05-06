package com.example.proyectomov.back.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface ProductoCatalogoOutletDao {
    @Query("SELECT * FROM productos_catalogo_cache ORDER BY remoteId ASC")
    suspend fun listarTodos(): List<ProductoCatalogoOutletEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(items: List<ProductoCatalogoOutletEntity>)

    @Query("DELETE FROM productos_catalogo_cache")
    suspend fun borrarTodos()

    @Transaction
    suspend fun reemplazarCatalogo(items: List<ProductoCatalogoOutletEntity>): Unit {
        borrarTodos()
        insertarTodos(items)
    }
}
