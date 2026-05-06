package com.example.proyectomov.back.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProductoPublicadoVenderOutletDao {
    @Query("SELECT * FROM productos_publicados_vender ORDER BY creadoEnMillis DESC")
    suspend fun listarTodos(): List<ProductoPublicadoVenderOutletEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(ent: ProductoPublicadoVenderOutletEntity)
}
