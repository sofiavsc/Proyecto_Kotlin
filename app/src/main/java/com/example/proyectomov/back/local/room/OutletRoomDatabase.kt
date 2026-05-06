package com.example.proyectomov.back.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        ProductoCatalogoOutletEntity::class,
        FavoritoProductoOutletEntity::class,
        ProductoPublicadoVenderOutletEntity::class,
        CarritoLocalLineaEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
abstract class OutletRoomDatabase : RoomDatabase() {

    abstract fun productoCatalogoDao(): ProductoCatalogoOutletDao
    abstract fun favoritoDao(): FavoritoProductoOutletDao
    abstract fun productoPublicadoVenderDao(): ProductoPublicadoVenderOutletDao
    abstract fun carritoLocalLineaDao(): CarritoLocalLineaDao

    companion object {
        @Volatile
        private var instancia: OutletRoomDatabase? = null

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS favoritos_producto_new " +
                        "(usuario_id INTEGER NOT NULL, idMostrar TEXT NOT NULL, " +
                        "PRIMARY KEY(usuario_id, idMostrar))",
                )
                db.execSQL(
                    "INSERT INTO favoritos_producto_new (usuario_id, idMostrar) " +
                        "SELECT 1 AS usuario_id, idMostrar FROM favoritos_producto",
                )
                db.execSQL("DROP TABLE favoritos_producto")
                db.execSQL("ALTER TABLE favoritos_producto_new RENAME TO favoritos_producto")
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS carrito_local_linea " +
                        "(usuario_id INTEGER NOT NULL, product_id INTEGER NOT NULL, " +
                        "cantidad INTEGER NOT NULL, PRIMARY KEY(usuario_id, product_id))",
                )
            }
        }

        fun obtener(context: Context): OutletRoomDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    OutletRoomDatabase::class.java,
                    "outlet_room.db",
                )
                    .addMigrations(MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
                    .build().also { instancia = it }
            }
    }
}
