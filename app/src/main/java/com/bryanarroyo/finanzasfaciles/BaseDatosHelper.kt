package com.bryanarroyo.finanzasfaciles

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatosHelper(context: Context) : SQLiteOpenHelper(
    context, NOMBRE_BD, null, VERSION_BD) {

    override fun onCreate(db: SQLiteDatabase) {
        val crearTablaIngresos = """
            CREATE TABLE $TABLA_INGRESOS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_DESCRIPCION TEXT,
                $COL_MONTO REAL,
                $COL_FECHA TEXT
            );
        """.trimIndent()

        val crearTablaGastos = """
            CREATE TABLE $TABLA_GASTOS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_DESCRIPCION TEXT,
                $COL_MONTO REAL,
                $COL_FECHA TEXT
            );
        """.trimIndent()

        db.execSQL(crearTablaIngresos)
        db.execSQL(crearTablaGastos)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLA_INGRESOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_GASTOS")
        onCreate(db)
    }

    companion object {
        // Datos de la Base
        const val NOMBRE_BD = "FinanzasFaciles.db"
        const val VERSION_BD = 1

        // Tabla Ingresos
        const val TABLA_INGRESOS = "Ingresos"
        const val COL_ID = "id"
        const val COL_DESCRIPCION = "descripcion"
        const val COL_MONTO = "monto"
        const val COL_FECHA = "fecha"

        // Tabla Gastos (usa mismas columnas)
        const val TABLA_GASTOS = "Gastos"
    }
}
