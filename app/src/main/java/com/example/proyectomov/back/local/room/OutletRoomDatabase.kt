package com.example.proyectomov.back.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ProductoCatalogoOutletEntity::class,
        FavoritoProductoOutletEntity::class,
        ProductoPublicadoVenderOutletEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class OutletRoomDatabase : RoomDatabase() {

    abstract fun productoCatalogoDao(): ProductoCatalogoOutletDao
    abstract fun favoritoDao(): FavoritoProductoOutletDao
    abstract fun productoPublicadoVenderDao(): ProductoPublicadoVenderOutletDao

    companion object {
        @Volatile
        private var instancia: OutletRoomDatabase? = null

        fun obtener(context: Context): OutletRoomDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    OutletRoomDatabase::class.java,
                    "outlet_room.db",
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build().also { instancia = it }
            }
    }
}
