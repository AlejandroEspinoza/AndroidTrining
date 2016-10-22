package com.android.training.androidtrining;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.training.androidtrining.api.BuscadorDeLibros;
import com.android.training.androidtrining.api.BuscadorListener;
import com.android.training.androidtrining.customcontrol.DownloaderService;
import com.android.training.androidtrining.customcontrol.DownloaderView;
import com.android.training.androidtrining.modelos.Libro;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alejandro on 10/8/16.
 */
public class BuscadorLibros extends Fragment implements BuscadorListener {
    private DownloaderService service;

    private EditText buscador;
    private RecyclerView recyclerView;
    private Adaptador recyclerAdapter;

    public static BuscadorLibros getInstance(){
        return new BuscadorLibros();
    }

    public BuscadorLibros(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_buscador_libros, container, false );

        buscador = (EditText) vista.findViewById(R.id.buscador);
        recyclerView = (RecyclerView) vista.findViewById(R.id.recyclerView);

        recyclerAdapter = new Adaptador();
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager( getActivity().getApplicationContext()) );

        return vista;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        service = null;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void addDownloaderService(DownloaderService service ){
        this.service = service;

        if( service != null ){
            new BuscadorDeLibros(this).execute();
        }
    }

    @Override
    public void addLibro(Libro libro) {
        recyclerAdapter.addLibro(libro);
    }


    private class Adaptador extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private List<Libro> libros;

        class LibroHolder extends RecyclerView.ViewHolder{
            private ImageView imagen;
            private TextView titulo;
            private TextView autor;
            private DownloaderView descarga;

            public LibroHolder( View view ){
                super(view);

                imagen = (ImageView) view.findViewById(R.id.imagen);
                titulo = (TextView) view.findViewById(R.id.titulo);
                autor = (TextView) view.findViewById(R.id.autor);
                descarga = (DownloaderView) view.findViewById(R.id.descarga);
            }
        }

        public Adaptador(){
            super();
            libros = new ArrayList<>();
        }

        public void addLibro( Libro libro ){
            libros.add(libro);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return libros.size();
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.row_libro, parent, false);
            return new LibroHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            LibroHolder libroHolder = ((LibroHolder) holder);
            Libro libro = libros.get(position);

            libroHolder.autor.setText( libro.getAutor() );
            libroHolder.titulo.setText( libro.getTitulo() );
            libroHolder.descarga.setLibro( service, libro );

            // TODO: Agregar la imagen del libro
            // libroHolder.imagen.setText( linbro.getAutor() );
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            super.onViewRecycled(holder);
            // Se obtiene el holder del renglon por reciclar
            LibroHolder libroHolder = ((LibroHolder) holder);

            // Se limpian los textos del renglon
            libroHolder.autor.setText( "" );
            libroHolder.titulo.setText( "" );

            // Se limpia la interfaz de descarga
            libroHolder.descarga.limpiarComponente();
        }
    }
}