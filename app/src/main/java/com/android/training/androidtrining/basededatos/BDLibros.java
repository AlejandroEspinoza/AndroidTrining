package com.android.training.androidtrining.basededatos;

import android.provider.BaseColumns;

/**
 * Created by Alejandro on 10/21/16.
 */
public interface BDLibros extends BaseColumns {
    String TABLE_NAME = "Libros";
    String TITULO = "titulo";
    String AUTOR = "autor";
    String URL = "url";
    String PDF_URL = "pdf_url";
    String PDF_FILE = "pdf_file";
    String IMAGEN_URL = "imagen_url";
    String IMAGEN_FILE = "imagen_file";
}