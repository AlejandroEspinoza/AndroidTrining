package com.android.training.androidtrining;

import android.app.Activity;
import android.app.PendingIntent;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.training.androidtrining.api.BuscadorDeLibros;
import com.android.training.androidtrining.basededatos.BaseDeDatos;
import com.android.training.androidtrining.customcontrol.DownloaderService;
import com.android.training.androidtrining.customcontrol.DownloaderView;
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

        recyclerView = (RecyclerView) vista.findViewById(R.id.recyclerView);

        recyclerAdapter = new Adaptador();
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(
                new GridLayoutManager(
                        getActivity().getApplicationContext(),
                        (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)?
                                2 : 4 ) );

        getBundleData(savedInstanceState);

        return vista;
    }

    // Recicler objecto ante reinicio del fragmento
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArray("Libros", recyclerAdapter.libros.toArray(new Libro[ recyclerAdapter.libros.size() ]) );
    }

    public void getBundleData( Bundle bundle ){
        if( bundle == null ){
            recyclerAdapter.addLibros(
                    BaseDeDatos.getInstance( getActivity().getApplicationContext())
                            .getLibros() );
        } else {
            Libro[] libros = (Libro[]) bundle.getParcelableArray("Libros");
            if (libros != null) {
                recyclerAdapter.addLibros(Arrays.asList(libros));
            }
        }
    }

    private class Adaptador extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private List<Libro> libros;

        class LibroHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private ImageView imagen;
            private TextView titulo;
            private TextView autor;

            public LibroHolder( View view ){
                super(view);

                imagen = (ImageView) view.findViewById(R.id.image);
                titulo = (TextView) view.findViewById(R.id.title);
                autor = (TextView) view.findViewById(R.id.autor);

                imagen.setOnClickListener(this );
            }

            @Override
            public void onClick(View view) {
                int itemPosition = this.getAdapterPosition();
                Libro libro = libros.get(itemPosition);

                File file = new File(libro.getPdfFile());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                PendingIntent pendIntent = PendingIntent.getActivity(
                        getActivity().getApplicationContext(), 0, intent, 0);
                pendIntent.send();
                //startActivity(pendIntent);
            }
        }

        public Adaptador(){
            super();
            libros = new ArrayList<>();
        }

        public void addLibro( Libro libro ){
            if( libro == null ){
                return;
            }
            libros.add(libro);
            notifyDataSetChanged();
        }

        public void addLibros( List<Libro> lista ){
            if( lista == null ){
                return;
            }

            libros = lista;
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
            Picasso .with(getActivity().getApplicationContext())
                    .load( getActivity().getFileStreamPath(libro.getImagenFile() ) )
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
            libroHolder.imagen.setImageResource(0);
        }
    }

    private BroadcastReceiver receiver;
    private Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        activity = (Activity) context;

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Refresh
                if( recyclerAdapter != null ){
                    recyclerAdapter.addLibros(
                            BaseDeDatos.getInstance( getActivity().getApplicationContext())
                                    .getLibros() );
                }
            }
        };
        activity.registerReceiver(receiver, new IntentFilter("LIBRO_DESCARGADO"));
    }

    @Override
    public void onDetach() {
        super.onDetach();

        activity.unregisterReceiver(receiver);
    }
}