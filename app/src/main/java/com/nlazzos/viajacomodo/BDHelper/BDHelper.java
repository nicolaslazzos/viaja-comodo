package com.nlazzos.viajacomodo.BDHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.nlazzos.viajacomodo.BuildConfig;
import com.nlazzos.viajacomodo.DAOs.DAOCalificacion;
import com.nlazzos.viajacomodo.DAOs.DAOColectivo;
import com.nlazzos.viajacomodo.DAOs.DAOEmpresa;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Nico on 12/12/2017.
 */
public class BDHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "viajacomodo.vc";
    private static final String DB_PATH = "data/" + BuildConfig.APPLICATION_ID + "/databases/";
    private static final int DB_SCHEME_VERSION = 1;
    //private String backupFile;

    public BDHelper(Context context) {
        super(context, DB_NAME, null, DB_SCHEME_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //CREACION EMPRESAS
        db.execSQL(DAOEmpresa.CREAR_TABLA);
        db.execSQL(DAOEmpresa.INSERT_INTERCORDOBA);
        db.execSQL(DAOEmpresa.INSERT_SARMIENTO);
        //CREACION CALIFICACIONES
        db.execSQL(DAOCalificacion.CREAR_TABLA);
        db.execSQL(DAOCalificacion.INSERT_EXCELENTE);
        db.execSQL(DAOCalificacion.INSERT_BUENO);
        db.execSQL(DAOCalificacion.INSERT_REGULAR);
        db.execSQL(DAOCalificacion.INSERT_MALO);
        //CREACION COLECTIVOS
        db.execSQL(DAOColectivo.CREAR_TABLA);
        db.execSQL(DAOColectivo.INSERT1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void exportDB() throws IOException {
        File data = Environment.getDataDirectory();
        File sdPath = Environment.getExternalStorageDirectory();
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Date now = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss_dd-MM-yyyy");
            String backupFile  = "Viaja_Comodo_" + dateFormat.format(now) + ".vc";
            File currentDB = new File(data, DB_PATH + DB_NAME);
            File backupDB = new File(sdPath, backupFile);

            if(currentDB.exists()) {
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }
        }
    }

    public void importDB(String rutaBackup) throws IOException {
        File data = Environment.getDataDirectory();
        File currentDB = new File(data, DB_PATH + DB_NAME);

        OutputStream databaseOutputStream = new FileOutputStream(currentDB);
        InputStream databaseInputStream;

        byte[] buffer = new byte[1024];
        int length;

        databaseInputStream = new FileInputStream(rutaBackup);

        while ((length = databaseInputStream.read(buffer)) > 0) {
            databaseOutputStream.write(buffer);
        }

        databaseInputStream.close();
        databaseOutputStream.flush();
        databaseOutputStream.close();
    }

    //METODOS ALTERNATIVOS QUE FUNCIONAN CONSULTANDO Y REINSERTANDO TODOS LOS DATOS EN UNA DB COPIA
    //NO FUNCIONAN SI HAY AUTOINCREMENT
    /*
    public SQLiteDatabase crearArchivoCopia(){
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss_dd-MM-yyyy");
        backupFile  = "Viaja_Comodo_" + dateFormat.format(now) + ".vc";
        SQLiteDatabase dbCopia = SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory() + "/" + backupFile, null);
        dbCopia.execSQL(DAOCalificacion.CREAR_TABLA);
        dbCopia.execSQL(DAOEmpresa.CREAR_TABLA);
        dbCopia.execSQL(DAOColectivo.CREAR_TABLA);
        return dbCopia;
    }

    public void exportarDatos(SQLiteDatabase dbOriginal){
        Cursor cursor;
        DAOCalificacion daoCalificacion = new DAOCalificacion();
        DAOColectivo daoColectivo = new DAOColectivo();
        DAOEmpresa daoEmpresa = new DAOEmpresa();
        SQLiteDatabase dbCopia = crearArchivoCopia();

        cursor = daoCalificacion.consultar(dbOriginal);
        while(cursor.moveToNext()){
            daoCalificacion.insertar(dbCopia, cursor.getInt(0), cursor.getString(1));
        }

        cursor = daoEmpresa.consultarSQL(dbOriginal);
        while(cursor.moveToNext()){
            daoEmpresa.insertarCopia(dbCopia, cursor.getInt(0), cursor.getString(1));
        }

        cursor = daoColectivo.consultarSQL(dbOriginal);
        while(cursor.moveToNext()){
            daoColectivo.insertar(dbCopia, cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getString(3));
        }

        File journal = new File(Environment.getExternalStorageDirectory(), backupFile + "-journal");
        if(journal.exists()){
            journal.delete();
        }
    }

    public void importarDatos(SQLiteDatabase dbOriginal, String rutaBackup){
        Cursor cursor;
        DAOColectivo daoColectivo = new DAOColectivo();
        DAOEmpresa daoEmpresa = new DAOEmpresa();
        File backup = new File(rutaBackup);
        SQLiteDatabase dbCopia = SQLiteDatabase.openOrCreateDatabase(backup, null);

        daoColectivo.eliminarTodo(dbOriginal);
        daoEmpresa.eliminarTodo(dbOriginal);

        cursor = daoEmpresa.consultarSQL(dbCopia);
        while(cursor.moveToNext()){
            daoEmpresa.insertarCopia(dbOriginal, cursor.getInt(0), cursor.getString(1));
        }

        cursor = daoColectivo.consultarSQL(dbCopia);
        while(cursor.moveToNext()){
            daoColectivo.insertar(dbOriginal, cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getString(3));
        }
    }
    */

}
