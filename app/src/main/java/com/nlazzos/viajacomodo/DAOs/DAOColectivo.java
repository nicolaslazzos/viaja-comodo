package com.nlazzos.viajacomodo.DAOs;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Nico on 12/12/2017.
 */
public class DAOColectivo {
    public static final String NOMBRE_TABLA = "Colectivos";
    public static final String COL_NRO = "nroColectivo";
    public static final String COL_EMPRESA = "idEmpresa";
    public static final String COL_CALIFICACION = "idCalificacion";
    public static final String COL_OBSERVACIONES = "observaciones";
    public static final String CREAR_TABLA = "CREATE TABLE " + NOMBRE_TABLA + " (" +
            COL_NRO + " INTEGER NOT NULL, " +
            COL_EMPRESA + " INTEGER NOT NULL, " +
            COL_CALIFICACION + " INTEGER NOT NULL, " +
            COL_OBSERVACIONES + " VARCHAR(45), " +
            "PRIMARY KEY (" + COL_NRO + ", " + COL_EMPRESA + "), " +
            "FOREIGN KEY (" + COL_EMPRESA + ") REFERENCES " + DAOEmpresa.NOMBRE_TABLA + "(" + DAOEmpresa.COL_ID + "), " +
            "FOREIGN KEY (" + COL_CALIFICACION + ") REFERENCES " + DAOCalificacion.NOMBRE_TABLA + "(" + DAOCalificacion.COL_ID + ") " +
            "); ";
    public static final String INSERT1 = "INSERT INTO Colectivos(nroColectivo, idEmpresa, idCalificacion, observaciones) VALUES(99, 1, 1, 'Observacion de prueba'); ";

    public ContentValues generarContentValues(int nroColectivo, int idEmpresa, int idCalificacion, String observaciones){
        ContentValues valores = new ContentValues();
        valores.put(COL_NRO, nroColectivo);
        valores.put(COL_EMPRESA, idEmpresa);
        valores.put(COL_CALIFICACION, idCalificacion);
        valores.put(COL_OBSERVACIONES, observaciones);
        return valores;
    }

    public void insertar(SQLiteDatabase db, int nroColectivo, int idEmpresa, int idCalificacion, String observaciones){
        db.insert(NOMBRE_TABLA, observaciones, generarContentValues(nroColectivo, idEmpresa, idCalificacion, observaciones));
    }

    public Cursor consultar(SQLiteDatabase db){
        //CONSULTA UTILIZANDO METODOS DEL ANDROID STUDIO
        //String[] columnas = new String[] {"rowid _id", COL_NRO, COL_EMPRESA, COL_CALIFICACION, COL_OBSERVACIONES};
        //return db.query(NOMBRE_TABLA, columnas, null, null, null, null, null);

        //CONSULTA TRADICIONAL POR FACILIDAD PARA LOS JOINS Y LOS ALIAS
        String strQuery = "SELECT c.rowid _id, c.nroColectivo, e.nombre AS Empresa, ca.nombre AS Calificacion, c.observaciones, c.idEmpresa " +
                "FROM Colectivos c INNER JOIN Empresas e ON (c.idEmpresa = e.idEmpresa) " +
                "INNER JOIN Calificaciones ca ON (c.idCalificacion = ca.idCalificacion) " +
                "ORDER BY c.nroColectivo ASC";
        return db.rawQuery(strQuery, null);
    }

    public Cursor consultarPorNro(SQLiteDatabase db, String numero){
        //CONSULTA TRADICIONAL POR FACILIDAD PARA LOS JOINS Y LOS ALIAS
        String strQuery = "SELECT c.rowid _id, c.nroColectivo, e.nombre AS Empresa, ca.nombre AS Calificacion, c.observaciones, c.idEmpresa " +
                "FROM Colectivos c INNER JOIN Empresas e ON (c.idEmpresa = e.idEmpresa) " +
                "INNER JOIN Calificaciones ca ON (c.idCalificacion = ca.idCalificacion) " +
                "WHERE c.nroColectivo = " + numero + " " +
                "ORDER BY c.nroColectivo ASC";
        return db.rawQuery(strQuery, null);
    }

    public boolean consultarExistencia(SQLiteDatabase db, String numero, String idEmpresa){
        //CONSULTA TRADICIONAL POR FACILIDAD PARA LOS JOINS Y LOS ALIAS
        String strQuery = "SELECT c.rowid _id, (c.nroColectivo || ' - ' || e.nombre) AS Titulo, ca.nombre AS Calificacion, c.observaciones " +
                "FROM Colectivos c INNER JOIN Empresas e ON (c.idEmpresa = e.idEmpresa) " +
                "INNER JOIN Calificaciones ca ON (c.idCalificacion = ca.idCalificacion) " +
                "WHERE c.nroColectivo = " + numero + " AND c.idEmpresa = " + idEmpresa + " " +
                "ORDER BY c.nroColectivo ASC";
        return (db.rawQuery(strQuery, null).getCount() == 0);
    }

    public void eliminar(SQLiteDatabase db, int nroColectivo, int idEmpresa){
        String nroColectivoStr = String.valueOf(nroColectivo);
        String idEmpresaStr = String.valueOf(idEmpresa);
        //db.delete(NOMBRE_TABLA, COL_NRO + "=? AND " + COL_EMPRESA + "=?", new String[] {nroColectivoStr, idEmpresaStr});
        db.execSQL("DELETE FROM Colectivos WHERE nroColectivo = " + nroColectivoStr + " AND idEmpresa = " + idEmpresaStr + ";");
    }

    public void eliminarPorEmpresa(SQLiteDatabase db, int idEmpresa){
        String idEmpresaStr = String.valueOf(idEmpresa);
        db.delete(NOMBRE_TABLA, COL_EMPRESA + "=?", new String[] {idEmpresaStr});
    }

    public void modificar(SQLiteDatabase db, int nroColectivo, int idEmpresa, int idCalificacion, String observaciones){
        String nroColectivoStr = String.valueOf(nroColectivo);
        String idEmpresaStr = String.valueOf(idEmpresa);
        String idCalificacionStr = String.valueOf(idCalificacion);
        //db.update(NOMBRE_TABLA, generarContentValues(nroColectivo, idEmpresa, idCalificacion, observaciones), COL_NRO + "=? AND " + COL_EMPRESA + "=?", new String[] {nroColectivoStr, idEmpresaStr});
        db.execSQL("UPDATE Colectivos SET idCalificacion = " + idCalificacionStr + ", observaciones = '" + observaciones + "' WHERE " +
                "nroColectivo = " + nroColectivoStr + " AND idEmpresa = " + idEmpresaStr + ";");
    }

    public Cursor consultarSQL(SQLiteDatabase db){
        String strQuery = "SELECT " + COL_NRO + ", " + COL_EMPRESA + ", " + COL_CALIFICACION + ", " + COL_OBSERVACIONES + " FROM " + NOMBRE_TABLA + "; ";
        return db.rawQuery(strQuery, null);
    }

    public void eliminarTodo(SQLiteDatabase db){
        db.execSQL("DELETE FROM " + NOMBRE_TABLA + "; ");
    }
}
