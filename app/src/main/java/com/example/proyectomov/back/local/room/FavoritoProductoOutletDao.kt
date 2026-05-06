package com.example.proyectomov.back.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoritoProductoOutletDao {
    @Query("SELECT idMostrar FROM favoritos_producto WHERE usuario_id = :usuarioId")
    suspend fun listarIds(usuarioId: Int): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(ent: FavoritoProductoOutletEntity)

    @Query("DELETE FROM favoritos_producto WHERE usuario_id = :usuarioId AND idMostrar = :id")
    suspend fun borrarPorId(usuarioId: Int, id: String)

    @Query(
        "SELECT EXISTS(SELECT 1 FROM favoritos_producto WHERE usuario_id = :usuarioId AND idMostrar = :id LIMIT 1)",
    )
    suspend fun existe(usuarioId: Int, id: String): Boolean
}
