package com.nlazzos.viajacomodo.DAOs;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Nico on 12/12/2017.
 */
public class DAOCalificacion {
    public static final String NOMBRE_TABLA = "Calificaciones";
    public static final String COL_ID = "idCalificacion";
    public static final String COL_NOMBRE = "nombre";
    public static final String CREAR_TABLA = "CREATE TABLE " + NOMBRE_TABLA + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            COL_NOMBRE + " VARCHAR(15) NOT NULL " +
            "); ";
    public static final String INSERT_EXCELENTE = "INSERT INTO " + NOMBRE_TABLA + "(" + COL_NOMBRE + ") VALUES('Excelente'); ";
    public static final String INSERT_BUENO = "INSERT INTO " + NOMBRE_TABLA + "(" + COL_NOMBRE + ") VALUES('Bueno'); ";
    public static final String INSERT_REGULAR = "INSERT INTO " + NOMBRE_TABLA + "(" + COL_NOMBRE + ") VALUES('Regular'); ";
    public static final String INSERT_MALO = "INSERT INTO " + NOMBRE_TABLA + "(" + COL_NOMBRE + ") VALUES('Malo'); ";

    public ContentValues generarContentValues(String nombre){
        ContentValues valores = new ContentValues();
        valores.put(COL_NOMBRE, nombre);
        return valores;
    }

    public int consultarId(SQLiteDatabase db, String calificacion){
        String strQuery = "SELECT " + COL_ID + " FROM " + NOMBRE_TABLA + " WHERE " + COL_NOMBRE + " LIKE '" + calificacion + "';";
        Cursor cursor = db.rawQuery(strQuery, null);
        cursor.moveToNext();
        return cursor.getInt(0);
    }

    public Cursor consultar(SQLiteDatabase db){
        String strQuery = "SELECT " + COL_ID + ", " + COL_NOMBRE + " FROM " + NOMBRE_TABLA + ";";
        return db.rawQuery(strQuery, null);
    }

    public void insertar(SQLiteDatabase db, int id, String nombre){
        String idStr = String.valueOf(id);
        String strSQL = "INSERT INTO " + NOMBRE_TABLA + "(" + COL_ID + ", " + COL_NOMBRE + ") VALUES(" + idStr + ", '" + nombre + "'); ";
        db.execSQL(strSQL);
    }

    public void eliminar(SQLiteDatabase db, int idCalificacion){
        String idCalificacionStr = String.valueOf(idCalificacion);
        db.delete(NOMBRE_TABLA, COL_ID + "=?", new String[] {idCalificacionStr});
    }

    public void modificar(SQLiteDatabase db, int idCalificacion, String nombre){
        String idCalificacionStr = String.valueOf(idCalificacion);
        db.update(NOMBRE_TABLA, generarContentValues(nombre), COL_ID + "=?", new String[] {idCalificacionStr});
    }
}
