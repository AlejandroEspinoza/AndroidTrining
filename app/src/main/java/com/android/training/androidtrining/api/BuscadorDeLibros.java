package com.android.training.androidtrining.api;

import android.os.AsyncTask;
import android.util.Log;

import com.android.training.androidtrining.modelos.Libro;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Alejandro on 10/8/16.
 */
public class BuscadorDeLibros extends AsyncTask<String, Libro, Boolean> {
    private final String TAG = BuscadorDeLibros.class.getSimpleName();
    private String BOOKS_URL = "http://lelibros.org/";
    private String BOOKS_AVENTURA_URL = "http://lelibros.org/categoria/aventura";
    private String BOOKS_SEARCH_URL = "http://lelibros.org/?s=";
    private String BOOKS_ELEMENT_CLASS = "books";
    private String BOOK_IMG_TAG = "img";
    private String BOOK_TITLE_TAG = "strong";
    private String BOOK_AUTOR_TAG = "span";
    private String BOOK_PAGE_LINK_TAG = "a";
    private String BOOK_PDF_LINK_TITLE = "title";
    private String BOOK_PDF_LINK_VALUE = "Download PDF";
    private String NODE_SOURCE = "src";
    private String NODE_REFERENCE = "href";
    private String NODE_LIST_ELEMENT = "li";

    private BuscadorListener listener;

    public BuscadorDeLibros(BuscadorListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        String targetURL;
        try {
            if (strings != null && strings.length > 0) {
                targetURL = BOOKS_SEARCH_URL + URLEncoder.encode(strings[0], "UTF-8");
            } else {
                targetURL = BOOKS_URL;
                targetURL = BOOKS_AVENTURA_URL;
            }

            Document doc = Jsoup.parse(new URL(targetURL), 2000);
            Elements temas = doc.getElementsByClass(BOOKS_ELEMENT_CLASS);

            if( temas.size() < 1 ){
                return false;
            } else {
                for (Element libros : temas) {
                    for (Element elemento : libros.getElementsByTag(NODE_LIST_ELEMENT)) {
                        Elements imageElement = elemento.getElementsByTag(BOOK_IMG_TAG);
                        Elements tituloElement = elemento.getElementsByTag(BOOK_TITLE_TAG);
                        Elements autorElement = elemento.getElementsByTag(BOOK_AUTOR_TAG);
                        Elements pageLinkElement = elemento.getElementsByTag(BOOK_PAGE_LINK_TAG);

                        if (imageElement == null || tituloElement == null || autorElement == null || pageLinkElement == null) {
                            continue;
                        }

                        try {
                            Libro libro = new Libro();
                            libro.setAutor(autorElement.text());
                            libro.setImagenUrl(BOOKS_URL + imageElement.attr(NODE_SOURCE));
                            libro.setUrl(pageLinkElement.attr(NODE_REFERENCE));
                            libro.setTitulo(tituloElement.text());

                            Document subDoc = Jsoup.parse(new URL(BOOKS_URL + libro.getUrl()), 2000);
                            Elements descarga = subDoc.getElementsByAttributeValueMatching(BOOK_PDF_LINK_TITLE, BOOK_PDF_LINK_VALUE);

                            if (descarga != null) {
                                libro.setPdfUrl(descarga.get(0).attr(NODE_REFERENCE));
                                publishProgress(libro);
                            }
                        } catch (Exception ex) {
                            Log.i(TAG, ex.getMessage());
                            return false;
                        }
                    }
                }
                return true;
            }
        } catch (Exception ex) {
            Log.i(TAG, ex.getMessage());
            return false;
        }
    }

    @Override
    protected void onProgressUpdate(Libro... values) {
        super.onProgressUpdate(values);

        if (listener != null) {
            listener.addLibro(values[0]);
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        listener.tareaTerminada(result);
    }
}