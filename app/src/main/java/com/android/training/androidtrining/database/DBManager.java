package com.android.training.androidtrining.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.android.training.androidtrining.modelos.Libro;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alejandro on 10/22/16.
 */
public class DBManager extends SQLiteOpenHelper {
    private static final String TAG = DBManager.class.getSimpleName();
    private static final String DB_NAME = "LibrosDB";
    private static final int VERSION = 2;
    private static DBManager instance;
    private static SQLiteDatabase db;

    private static String CREATE_LIBROS = " CREATE TABLE " +
            DBLibro.TABLE_NAME + " ( " +
            DBLibro._ID + " integer primary key," +
            DBLibro.COLUMN_TITULO + " text, " +
            DBLibro.COLUMN_AUTOR + " text, " +
            DBLibro.COLUMN_url + " text, " +
            DBLibro.COLUMN_PDF_URL + " text, " +
            DBLibro.COLUMN_PDF_FILE  + " text, " +
            DBLibro.COLUMN_IMAGEN_URL + " text, " +
            DBLibro.COLUMN_IMAGEN_FILE + " text, " +
            " UNIQUE ( " +  DBLibro.COLUMN_TITULO + " ) ) ";
    private static final String CREATE_LIBROS_INDEX =  " CREATE INDEX idx_libro ON " +
            DBLibro.TABLE_NAME + " ( " +
            DBLibro.COLUMN_TITULO + " ) ";
    private static final String DELETE_LIBROS =
            " DROP TABLE IF EXISTS " + DBLibro.TABLE_NAME;

    public static DBManager getInstance ( Context contexto ){
        if( instance == null ) {
            instance = new DBManager(contexto);
        }

        return instance;
    };

    private DBManager(Context context){
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_LIBROS);
        sqLiteDatabase.execSQL(CREATE_LIBROS_INDEX);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DELETE_LIBROS);
        sqLiteDatabase.execSQL(CREATE_LIBROS);
        sqLiteDatabase.execSQL(CREATE_LIBROS_INDEX);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    public SQLiteDatabase abrirDaseDeDatos(){
        if( db == null ){
            instance.getWritableDatabase();
        }

        return db;
    }

    public void cerrarBaseDeDatos(){
        if( db == null ){
            return;
        } else if( db.isOpen() ){
            db.close();
        }
        db = null;
    }

    public boolean exists(Libro libro){
        SQLiteDatabase database = getWritableDatabase();

        // Se crea el query para obtener valores de la tabla de libros
        String query =
                "SELECT * FROM " + DBLibro.TABLE_NAME +
                        " WHERE " + DBLibro.COLUMN_TITULO + " = ? ";

        try{
            // Se crea un cursor que contendra los registros encontrados
            Cursor cursor = database.rawQuery(query, new String[]{libro.getTitulo()} );

            if( cursor.moveToFirst() ){
                return true;
            }

            return false;
        } catch (Exception ex ){
            Log.e(TAG, ex.getMessage());
            return false;
        }
    }

    public long guardarLibro(Libro libro ){
        SQLiteDatabase database = getWritableDatabase();

        if( libro == null ){
            return -1;
        }

        // Contenedor de datos de la tabla a insertar
        ContentValues values = new ContentValues();
        values.put(DBLibro.COLUMN_TITULO, libro.getTitulo() );
        values.put(DBLibro.COLUMN_AUTOR, libro.getAutor() );
        values.put(DBLibro.COLUMN_url, libro.getUrl() );
        values.put(DBLibro.COLUMN_PDF_FILE, libro.getPdfFile() );
        values.put(DBLibro.COLUMN_PDF_URL, libro.getPdfUrl() );
        values.put(DBLibro.COLUMN_IMAGEN_FILE, libro.getImagenFile() );
        values.put(DBLibro.COLUMN_IMAGEN_URL, libro.getImagenUrl() );

        // Insercion de datos a la tabla de Libro
        return database.insert(DBLibro.TABLE_NAME, null, values);
    }

    public List<Libro> obtenerLibros(){
        SQLiteDatabase database = getWritableDatabase();

        // Se crea el query para obtener valores de la tabla de libros
        String query = "SELECT * FROM " + DBLibro.TABLE_NAME;

        try{
            // Se crea un cursor que contendra los registros encontrados
            Cursor cursor = database.rawQuery(query, null);

            if( cursor.moveToFirst() ){
                List<Libro> libros = new ArrayList<>();
                do {
                    Libro libro = new Libro();

                    libro.setTitulo( cursor.getString( cursor.getColumnIndex(DBLibro.COLUMN_TITULO) ) );
                    libro.setAutor( cursor.getString( cursor.getColumnIndex(DBLibro.COLUMN_AUTOR) ) );
                    libro.setUrl( cursor.getString( cursor.getColumnIndex(DBLibro.COLUMN_url) ) );
                    libro.setPdfFile( cursor.getString( cursor.getColumnIndex(DBLibro.COLUMN_PDF_FILE) ) );
                    libro.setPdfUrl( cursor.getString( cursor.getColumnIndex(DBLibro.COLUMN_PDF_URL) ) );
                    libro.setImagenFile( cursor.getString( cursor.getColumnIndex(DBLibro.COLUMN_IMAGEN_FILE) ) );
                    libro.setImagenUrl( cursor.getString( cursor.getColumnIndex(DBLibro.COLUMN_IMAGEN_URL) ) );
                    libros.add(libro);
                } while (cursor.moveToNext());
                return libros;
            }

            return null;
        } catch (Exception ex ){
            Log.e(TAG, ex.getMessage());
            return null;
        }
    }
}
