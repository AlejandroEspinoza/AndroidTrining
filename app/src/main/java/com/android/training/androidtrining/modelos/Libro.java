package com.android.training.androidtrining.modelos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alejandro on 10/8/16.
 */
public class Libro implements Parcelable {
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



    // Metodos de parcelable
    public Libro( Parcel parcel ){
        titulo = parcel.readString();
        autor = parcel.readString();
        url = parcel.readString();
        pdfUrl = parcel.readString();
        pdfFile = parcel.readString();
        imagenUrl = parcel.readString();
        imagenFile = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(titulo);
        parcel.writeString(autor);
        parcel.writeString(url);
        parcel.writeString(pdfUrl);
        parcel.writeString(pdfFile);
        parcel.writeString(imagenUrl);
        parcel.writeString(imagenFile);
    }

    public static final Parcelable.Creator<Libro> CREATOR = new Parcelable.Creator<Libro>(){
        @Override
        public Libro createFromParcel(Parcel parcel) {
            return new Libro(parcel);
        }

        @Override
        public Libro[] newArray(int size) {
            return new Libro[size];
        }
    };
}
