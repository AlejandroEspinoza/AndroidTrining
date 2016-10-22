package com.android.training.androidtrining.modelos;

/**
 * Created by Alejandro on 10/8/16.
 */
public class Libro {
    private String titulo;
    private String autor;
    private String url;
    private String pdfUrl;
    private String pdfFile;
    private String imagenUrl;
    private String imagenFile;
    private int progresoDescarga;

    public Libro(){
    }

    public Libro( String titulo, String pdfUrl ){
        this.titulo = titulo;
        this.pdfUrl = pdfUrl;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getPdfFile() {
        return pdfFile;
    }

    public void setPdfFile(String pdfFile) {
        this.pdfFile = pdfFile;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getImagenFile() {
        return imagenFile;
    }

    public void setImagenFile(String imagenFile) {
        this.imagenFile = imagenFile;
    }

    public int getProgresoDescarga() {
        return progresoDescarga;
    }

    public void setProgresoDescarga(int progresoDescarga) {
        this.progresoDescarga = progresoDescarga;
    }
}
