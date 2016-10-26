package com.android.training.androidtrining;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ConfigurationHelper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.training.androidtrining.api.BuscadorDeLibros;
import com.android.training.androidtrining.api.BuscadorListener;
import com.android.training.androidtrining.basededatos.BaseDeDatos;
import com.android.training.androidtrining.customcontrol.DownloaderService;
import com.android.training.androidtrining.customcontrol.DownloaderView;
import com.android.training.androidtrining.modelos.Libro;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Alejandro on 10/8/16.
 */
public class BuscadorLibros extends Fragment implements BuscadorListener {
    private DownloaderService service;

    private EditText buscador;
    private RecyclerView recyclerView;
    private Adaptador recyclerAdapter;
    private List<Libro> librosReciclados;

    private EditText.OnEditorActionListener keyListener = new EditText.OnEditorActionListener(){
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // Se evalua que sea presionado el boton de "GO"
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    // Se oculta el teclado
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    // Se buscan los libros
                    searchForLibros();
                    return true;
                }
            return false;
        }
    };

    public static BuscadorLibros getInstance(){
        return new BuscadorLibros();
    }

    public BuscadorLibros(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_buscador_libros, container, false );

        // Se obtienen los componentes de la interfaz
        buscador = (EditText) vista.findViewById(R.id.buscador);
        recyclerView = (RecyclerView) vista.findViewById(R.id.recyclerView);

        // Se asigna el adaptador al recycler view
        recyclerAdapter = new Adaptador();
        recyclerView.setAdapter(recyclerAdapter);

        // Se le da el manejador de lienzo al recycler view
        recyclerView.setLayoutManager(
                    new GridLayoutManager(
                            getActivity().getApplicationContext(),
                            (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)?
                            1 : 2 ) );

        // Se agrega un escuchador para los eventos de teclado
        buscador.setOnEditorActionListener(keyListener);

        // Se buscan libros en los parametros del fragmento
        getBundleData(savedInstanceState);

        return vista;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Se remueve el servicio de descarga al ser removido de su actividad
        service = null;
    }

    // Se buscan libros en base a la entrada del usuario
    public void searchForLibros(){
        if(     service == null ||
                buscador.getText() == null ||
                buscador.getText().toString() == null ||
                buscador.getText().toString().trim().length() < 1 ){
            return;
        }

        // Se remueven los libros de la lista
        recyclerAdapter.removeLibros();

        // Se buscan nuevos libros
        new BuscadorDeLibros(this)
                .execute(buscador.getText().toString().trim());

        // Se inhabilita el campo de busqueda
        buscador.setEnabled(false);
    }

    // Se le agrega el servicio de descarga al fragmento
    public void addDownloaderService(DownloaderService service ){
        if( service == null ){
            return;
        }

        // Se agrega el servicio de descarga al fragmento
        this.service = service;

        if( librosReciclados != null && librosReciclados.size() > 0 ){
            // Si hay libros recyclados se agregan a la lista
            recyclerAdapter.addLibros(librosReciclados);
        } else {
            // Si no hay libros reciclados se buscan en la internet
            new BuscadorDeLibros(this).execute();
        }
    }

    @Override
    public void addLibro(Libro libro) {
        // Se valida si el fragmento esta adherido a su actividad
        if( libro == null || !isAdded() ){
            return;
        }

        // Se valida si el libro en cuestion ya se encuantra en la base de datos
        if(BaseDeDatos.getInstance(getActivity().getApplicationContext()).exists(libro)){
            return;
        }

        // Se agrega el libro al arreglo de libros
        recyclerAdapter.addLibro(libro);
    }

    @Override
    public void tareaTerminada(boolean resultado) {
        buscador.setEnabled(true);
    }

    private class Adaptador extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private List<Libro> libros;

        // Holder para el renglon de libros encontrados
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

        public void removeLibros(){
            libros = new ArrayList<>();
            notifyDataSetChanged();
        }

        public void addLibro( Libro libro ){
            libros.add(libro);
            notifyDataSetChanged();
        }

        public void addLibros( List<Libro> lista ){
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
            Picasso.with(getActivity().getApplicationContext()).load(libro.getImagenUrl()).into(libroHolder.imagen);
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

            // Se limpia la imagen
            libroHolder.imagen.setImageResource(0);
        }
    }

    // Recicler objecto ante reinicio del fragmento
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArray("Libros", recyclerAdapter.libros.toArray(new Libro[ recyclerAdapter.libros.size() ]) );
    }

    public void getBundleData( Bundle bundle ){
        if( bundle == null ){
            return;
        }

        Libro[] libros = (Libro[]) bundle.getParcelableArray("Libros");
        if( libros != null ){
            librosReciclados = Arrays.asList(libros);
        }
    }
}