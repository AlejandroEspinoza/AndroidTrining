package com.android.training.androidtrining;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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
import com.android.training.androidtrining.database.DBManager;
import com.android.training.androidtrining.modelos.Libro;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Alejandro on 10/8/16.
 */
public class BuscadorLibros extends Fragment implements BuscadorListener {
    private DownloaderService service;

    private static final String RECYCLE_BOOKS = "RECYCLE_BOOKS";
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
        recyclerView.setLayoutManager(
                new GridLayoutManager(
                        getActivity().getApplicationContext(),
                        (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) ?
                                1:2 )
                );

        getBundleData(savedInstanceState);

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
            if( librosRecyclados == null ) {
                new BuscadorDeLibros(this).execute();
            } else {
                recyclerAdapter.addLibros( librosRecyclados );
            }
        }
    }

    @Override
    public void addLibro(Libro libro) {
        if( libro == null ||
                DBManager.getInstance(getActivity().getApplicationContext()).exists(libro)){
            return;
        } else {
            recyclerAdapter.addLibro(libro);
        }
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

        public void addLibros( List<Libro> librosReciclados ){
            if( librosReciclados == null ){
                return;
            }

            libros = librosReciclados;
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

            // Agregar la imagen del libro
            Picasso.with(getActivity()
                    .getApplicationContext())
                    .load(libro.getImagenUrl())
                    .into(libroHolder.imagen);
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

            // Limpiar la imagen de lainterfaz de usuario
            libroHolder.imagen.setImageResource(0);
        }
    }




    // Reciclado de objetos
    // Lista utilizada para reciclar libros
    private List<Libro> librosRecyclados = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Se crea el arreglo de parcelables
        Parcelable[] objetosParcelable = recyclerAdapter.libros.toArray(
                new Libro[recyclerAdapter.libros.size()]);

        // Se incluyen los pacelables en el bundle
        outState.putParcelableArray( RECYCLE_BOOKS, objetosParcelable );
    }

    // Obtener libros desde un bundle
    private void getBundleData(Bundle bundle){
        // Se valida que el Bundle no sea nulo,
        // Importante ya que el comportamiento normal inicial lo traera nulo
        if( bundle == null ){
            return;
        }

        // Se obtienen los libros del bundle
        Libro[] libros = (Libro[]) bundle.getParcelableArray(RECYCLE_BOOKS);

        // Se guardan los libros recyclados
        if( libros != null ){
            librosRecyclados = Arrays.asList(libros);
        }
    }
}