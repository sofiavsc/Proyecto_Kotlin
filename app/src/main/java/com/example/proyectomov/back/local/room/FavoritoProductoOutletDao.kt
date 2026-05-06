package com.example.proyectomov.back.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoritoProductoOutletDao {
    @Query("SELECT idMostrar FROM favoritos_producto")
    suspend fun listarIds(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(ent: FavoritoProductoOutletEntity)

    @Query("DELETE FROM favoritos_producto WHERE idMostrar = :id")
    suspend fun borrarPorId(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favoritos_producto WHERE idMostrar = :id LIMIT 1)")
    suspend fun existe(id: String): Boolean
}
