package com.example.proyectomov.back.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CarritoLocalLineaDao {
    @Query("SELECT * FROM carrito_local_linea WHERE usuario_id = :usuarioId ORDER BY product_id ASC")
    suspend fun lineasPorUsuario(usuarioId: Int): List<CarritoLocalLineaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarOReemplazar(linea: CarritoLocalLineaEntity)

    @Query("DELETE FROM carrito_local_linea WHERE usuario_id = :usuarioId AND product_id = :productId")
    suspend fun borrarLinea(usuarioId: Int, productId: Int)

    @Query("DELETE FROM carrito_local_linea WHERE usuario_id = :usuarioId")
    suspend fun borrarTodoUsuario(usuarioId: Int)
}
