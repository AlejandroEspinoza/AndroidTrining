package com.android.training.androidtrining.database;

import android.provider.BaseColumns;

/**
 * Created by Alejandro on 10/22/16.
 */
public interface DBLibro extends BaseColumns {
    static String TABLE_NAME = "libros";
    static String COLUMN_TITULO = "titulo";
    static String COLUMN_AUTOR = "autor";
    static String COLUMN_url = "url";
    static String COLUMN_PDF_URL = "pdfUrl";
    static String COLUMN_PDF_FILE = "pdfFile";
    static String COLUMN_IMAGEN_URL = "imagenUrl";
    static String COLUMN_IMAGEN_FILE = "imagenFile";
}
