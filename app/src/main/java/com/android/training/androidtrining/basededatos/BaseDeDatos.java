package com.android.training.androidtrining.basededatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.training.androidtrining.modelos.Libro;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alejandro on 10/21/16.
 */
public class BaseDeDatos extends SQLiteOpenHelper{
    private static final String NOMBRE = "BD_LIBROS";
    private static final int VERSION = 4;
    private static BaseDeDatos instancia;
    private static SQLiteDatabase bd;

    private static final String CREATE_LIBROS = "CREATE TABLE " +
            BDLibros.TABLE_NAME  + " ( " +
            BDLibros._ID         + " integer primary key, " +
            BDLibros.TITULO      + " text, " +
            BDLibros.AUTOR       + " text, " +
            BDLibros.URL         + " text, " +
            BDLibros.PDF_URL     + " text, " +
            BDLibros.PDF_FILE    + " text, " +
            BDLibros.IMAGEN_URL  + " text, " +
            BDLibros.IMAGEN_FILE + " text," +
            "UNIQUE ( " + BDLibros.TITULO + " ) ) ";

    private static final String CREATE_LIBROS_INDEX = "CREATE INDEX idx_libro ON  " +
            BDLibros.TABLE_NAME  + " ( " +
            BDLibros.TITULO      + " , " +
            BDLibros.AUTOR       + " ) ";

    private static final String DELETE_LIBROS =
            "DROP TABLE IF EXISTS " + BDLibros.TABLE_NAME ;

    public static BaseDeDatos getInstance(Context contexto){
        if( instancia == null ){
            instancia = new BaseDeDatos( contexto, NOMBRE, VERSION);
        }

        instancia.abrirBaseDeDatos();

        return instancia;
    }

    private BaseDeDatos(Context contexto, String nombre, int version ){
        super(contexto, nombre, null, version);
    }

    private SQLiteDatabase abrirBaseDeDatos(){
        if( bd == null || !bd.isOpen() ){
            bd = instancia.getWritableDatabase();
        }

        return bd;
    }

    private void cerrarBaseDeDatos(){
        if( bd == null ){
            return;
        } else if( bd.isOpen() ){
            bd.close();
        }

        bd = null;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL( CREATE_LIBROS );
        sqLiteDatabase.execSQL( CREATE_LIBROS_INDEX );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL( DELETE_LIBROS );
        sqLiteDatabase.execSQL( CREATE_LIBROS );
        sqLiteDatabase.execSQL( CREATE_LIBROS_INDEX );
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    public boolean exists( Libro libro ){
        String query =
                "SELECT * FROM " + BDLibros.TABLE_NAME +
                " WHERE " + BDLibros.TITULO + " = ? " ;

        try {
            Cursor cursor = bd.rawQuery(query, new String[]{ libro.getTitulo() });
            if (cursor.moveToFirst()) {
                return true;
            } else {
                return false;
            }
        } catch ( Exception ex ){
            ex.printStackTrace();
            return false;
        }
    }

    public List<Libro> getLibros(){
        String query = "SELECT * FROM " + BDLibros.TABLE_NAME ;

        try {
            Cursor cursor = bd.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                List<Libro> libros = new ArrayList<>();
                do {
                    Libro libro = new Libro();
                    libro.setTitulo(cursor.getString(cursor.getColumnIndex(BDLibros.TITULO)));
                    libro.setAutor(cursor.getString(cursor.getColumnIndex(BDLibros.AUTOR)));
                    libro.setUrl(cursor.getString(cursor.getColumnIndex(BDLibros.URL)));
                    libro.setPdfUrl(cursor.getString(cursor.getColumnIndex(BDLibros.PDF_URL)));
                    libro.setPdfFile(cursor.getString(cursor.getColumnIndex(BDLibros.PDF_FILE)));
                    libro.setImagenUrl(cursor.getString(cursor.getColumnIndex(BDLibros.IMAGEN_URL)));
                    libro.setImagenFile(cursor.getString(cursor.getColumnIndex(BDLibros.IMAGEN_FILE)));
                    libros.add(libro);
                } while (cursor.moveToNext());
                return libros;
            }
        } catch ( Exception ex ){
            ex.printStackTrace();
        }
        return null;
    }

    public long addLibro( Libro libro ){
        if( libro == null ){
            return 0;
        }

        ContentValues values = new ContentValues();
        values.put(BDLibros.TITULO, libro.getTitulo() );
        values.put(BDLibros.AUTOR, libro.getAutor() );
        values.put(BDLibros.URL, libro.getUrl() );
        values.put(BDLibros.PDF_URL, libro.getPdfUrl() );
        values.put(BDLibros.PDF_FILE, libro.getPdfFile() );
        values.put(BDLibros.IMAGEN_URL, libro.getImagenUrl() );
        values.put(BDLibros.IMAGEN_FILE, libro.getImagenFile() );
        return bd.insert(BDLibros.TABLE_NAME, null, values );
    }
}