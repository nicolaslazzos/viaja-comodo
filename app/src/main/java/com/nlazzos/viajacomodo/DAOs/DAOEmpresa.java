package com.nlazzos.viajacomodo.DAOs;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Nico on 12/12/2017.
 */
public class DAOEmpresa {
    public static final String NOMBRE_TABLA = "Empresas";
    public static final String COL_ID = "idEmpresa";
    public static final String COL_NOMBRE = "nombre";
    public static final String CREAR_TABLA = "CREATE TABLE " + NOMBRE_TABLA + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            COL_NOMBRE + " VARCHAR(15) NOT NULL " +
            "); ";
    public static final String INSERT_INTERCORDOBA = "INSERT INTO " + NOMBRE_TABLA + "(" + COL_NOMBRE + ") VALUES('Intercordoba'); ";
    public static final String INSERT_SARMIENTO = "INSERT INTO " + NOMBRE_TABLA + "(" + COL_NOMBRE + ") VALUES('Sarmiento'); ";

    public ContentValues generarContentValues(String nombre){
        ContentValues valores = new ContentValues();
        valores.put(COL_NOMBRE, nombre);
        return valores;
    }

    public void insertar(SQLiteDatabase db, String nombre){
        db.insert(NOMBRE_TABLA, null, generarContentValues(nombre));
    }

    public Cursor consultar(SQLiteDatabase db){
        String[] columnas = new String[] {"rowid _id", COL_ID, COL_NOMBRE};
        return db.query(NOMBRE_TABLA, columnas, null, null, null, null, null);
    }

    public int obtenerUltimoID(SQLiteDatabase db){
        Cursor cursor = db.rawQuery("SELECT MAX(idEmpresa) FROM Empresas", null);
        int ultimoID = 0;
        while(cursor.moveToNext()){
            ultimoID = cursor.getInt(0);
        }
        return ultimoID;
    }

    public boolean validarExistencia(SQLiteDatabase db, String nombre){
        return (db.rawQuery("SELECT * FROM Empresas WHERE nombre LIKE '" + nombre + "';", null).getCount() == 0);
    }

    public void eliminar(SQLiteDatabase db, int idEmpresa){
        String idEmpresaStr = String.valueOf(idEmpresa);
        db.delete(NOMBRE_TABLA, COL_ID + "=?", new String[] {idEmpresaStr});
    }

    public void modificar(SQLiteDatabase db, int idEmpresa, String nombre){
        String idEmpresaStr = String.valueOf(idEmpresa);
        db.execSQL("UPDATE Empresas SET nombre = '" + nombre + "' WHERE idEmpresa = " + idEmpresaStr + ";");
        //db.update(NOMBRE_TABLA, generarContentValues(nombre), COL_ID + "=?", new String[] {idEmpresaStr});
    }

    public Cursor consultarSQL(SQLiteDatabase db){
        String strQuery = "SELECT " + COL_ID + ", " + COL_NOMBRE + " FROM " + NOMBRE_TABLA + "; ";
        return db.rawQuery(strQuery, null);
    }

    public void insertarCopia(SQLiteDatabase db, int id, String nombre){
        String idStr = String.valueOf(id);
        String strSQL = "INSERT INTO " + NOMBRE_TABLA + "(" + COL_ID + ", " + COL_NOMBRE + ") VALUES(" + idStr + ", '" + nombre + "'); ";
        db.execSQL(strSQL);
    }

    public void eliminarTodo(SQLiteDatabase db){
        db.execSQL("DELETE FROM " + NOMBRE_TABLA + "; ");
    }

}
