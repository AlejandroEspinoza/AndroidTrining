package com.android.training.androidtrining;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.training.androidtrining.customcontrol.DownloaderService;
import com.android.training.androidtrining.customcontrol.DownloaderView;
import com.android.training.androidtrining.database.DBManager;
import com.android.training.androidtrining.modelos.Libro;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Alejandro on 10/8/16.
 */
public class MisLibros extends Fragment {
    private static final String LIBROS_RECICLADOS = "LIBROS_RECICLADOS";
    private RecyclerView recyclerView;
    private Adaptador recyclerAdapter;

    public static MisLibros getInstance(){
        return new MisLibros();
    }

    public MisLibros(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_mis_libros, container, false );

        recyclerAdapter = new Adaptador();
        recyclerView = (RecyclerView) vista.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(recyclerAdapter);

        recyclerView.setLayoutManager(
                new GridLayoutManager(
                        getActivity().getApplicationContext(),
                        (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) ?
                                2:4 )
                );

        getBundleData( savedInstanceState );

        return vista;
    }

    private class Adaptador extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private List<Libro> libros;

        class LibroHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            private ImageView imagen;
            private TextView titulo;
            private TextView autor;

            public LibroHolder( View view ){
                super(view);

                imagen = (ImageView) view.findViewById(R.id.imagen);
                titulo = (TextView) view.findViewById(R.id.titulo);
                autor = (TextView) view.findViewById(R.id.autor);

                imagen.setOnClickListener( this );
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                Libro libro = libros.get( position );

                // Abrir el libro en una nueva actividad
                File file = new File( libro.getPdfFile() );

                // Llamada al sistema para abrir un archivo
                Intent intent = new Intent( Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/pdf" );
                intent.setFlags( Intent.FLAG_ACTIVITY_NO_HISTORY );
                startActivity(intent);
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
            View view = getActivity().getLayoutInflater().inflate(R.layout.row_mi_libro, parent, false);
            return new LibroHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            LibroHolder libroHolder = ((LibroHolder) holder);
            Libro libro = libros.get(position);

            libroHolder.autor.setText( libro.getAutor() );
            libroHolder.titulo.setText( libro.getTitulo() );

            // Agregar la imagen del libro
            Picasso.with(getActivity()
                    .getApplicationContext())
                    .load(new File(libro.getImagenFile()))
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

            // Limpiar la imagen de lainterfaz de usuario
            libroHolder.imagen.setImageResource(0);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArray(
                LIBROS_RECICLADOS,
                recyclerAdapter.libros.toArray(
                        new Libro[recyclerAdapter.libros.size()]));
    }

    private void getBundleData( Bundle bundle ){
        if( bundle == null ){
            recyclerAdapter.addLibros(
                    DBManager.getInstance(
                            getActivity().getApplicationContext())
                            .obtenerLibros()
            );
        } else {
            Libro[] libros = (Libro[]) bundle.getParcelableArray(LIBROS_RECICLADOS);
            if( libros != null ){
                recyclerAdapter.addLibros(Arrays.asList(libros));
            }
        }
    }


    // Escuchador de anuncios
    private Activity actividad;
    private BroadcastReceiver receptorDeAnuncios;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        actividad = (Activity) context;

        receptorDeAnuncios = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // Se buscan los nuevos libros en la base de datos
                recyclerAdapter.addLibros(
                        DBManager.getInstance(
                                getActivity().getApplicationContext())
                                .obtenerLibros()
                );
            }
        };

        actividad.registerReceiver(
                receptorDeAnuncios,
                new IntentFilter(DownloaderService.NUEVO_LIBRO));
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
