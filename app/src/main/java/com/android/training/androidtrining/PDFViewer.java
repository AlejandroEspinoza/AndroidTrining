package com.android.training.androidtrining;

import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.android.training.androidtrining.modelos.Libro;

/**
 * Created by Alejandro on 10/22/16.
 */
public class PDFViewer extends AppCompatActivity {
    private Libro libro;
    private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pdf_viewer);

        webView = (WebView) findViewById(R.id.webView);

        if( getIntent().getExtras() != null && getIntent().getExtras().getParcelable("LIBRO") != null ){
            libro = (Libro) getIntent().getExtras().getParcelable("LIBRO");

            cargarPDF();
        }
    }

    private void cargarPDF(){
        Uri path = Uri.parse(libro.getPdfFile());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(path.getPath());
    }
}