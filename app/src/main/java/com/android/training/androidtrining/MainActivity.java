package com.android.training.androidtrining;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.android.training.androidtrining.api.BuscadorListener;
import com.android.training.androidtrining.basededatos.BaseDeDatos;
import com.android.training.androidtrining.customcontrol.DownloaderService;
import com.android.training.androidtrining.customcontrol.DownloaderView;
import com.android.training.androidtrining.modelos.Libro;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DownloaderActivityInterface {
    private static final String TAG = MainActivity.class.getCanonicalName();

    private ViewPager viewPager;
    private Adaptador pagerAdapter;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        // Cargar la interfaz en la actividad
        viewPager =  (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        pagerAdapter = new Adaptador( this );
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        pagerAdapter.cargarPaginas();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // Controlador del paginador principal
    private class Adaptador extends FragmentPagerAdapter {
        // Lista de paginas del paginador principal
        List<Fragment> paginas;
        List<String> nombres;

        public Adaptador( AppCompatActivity contexto ){
            super( contexto.getSupportFragmentManager() );
            this.paginas = new ArrayList<>();
            this.nombres = new ArrayList<>();
        }

        // Se agrega una pagina a la interfaz principal
        public void addPagina( Fragment pagina, String nombre){
            paginas.add(pagina);
            nombres.add(nombre);
            pagerAdapter.notifyDataSetChanged();
        }

        // Retornamos el titulo de las paginas
        @Override
        public CharSequence getPageTitle(int position) {
            return nombres.get(position);
        }

        // Retornamos el numero de paginas
        @Override
        public int getCount() {
            return (paginas == null)? 0 : paginas.size();
        }

        // Retornamos las paginas a mostrar
        @Override
        public Fragment getItem(int position) {
            return paginas.get(position);
        }

        public void cargarPaginas(){
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if( fragments != null && fragments.size() > 1 ){
                addPagina( getSupportFragmentManager().getFragments().get(0), getString(R.string.titulo_buscador));
                addPagina( getSupportFragmentManager().getFragments().get(1), getString(R.string.titulo_mis_libros));
            } else {
                addPagina(BuscadorLibros.getInstance(), getString(R.string.titulo_buscador));
                addPagina(MisLibros.getInstance(), getString(R.string.titulo_mis_libros));
            }
        }

        public void addService( DownloaderService service ){
            for( Fragment fragment : paginas ){
                if( fragment instanceof BuscadorListener ) {
                    ((BuscadorListener) fragment).addDownloaderService(service);
                }
            }
        }
    }



    @Override
    protected void onStart() {
        super.onStart();

        // Pedir permisos al usuario
        pedirPermisos();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Se desliga la actividad del servicio
        unbindService(connection);
    }

    private DownloaderService service = null;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            // Se obtiene el servicio del parametro iBinder
            DownloaderService.DownloaderBinder binder = (DownloaderService.DownloaderBinder) iBinder;
            service = binder.getService();
            pagerAdapter.addService( service );
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // Se vacian las variables de la actividad
            service = null;
            connection = null;
        }
    };

    // Pedir permisos de escritura al usuario
    public void pedirPermisos(){
        // Validacion de permisos necesaria para Androis 6+
        int REQUEST_EXTERNAL_STORAGE = 200;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        int writePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // Pedir permisos al usuario para escribir en disco
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            // Correr el servicio de descarga
            iniciarServicio();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if( requestCode == 200 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
            iniciarServicio();
        }
    }

    // Obtencion de la instancia del servicio de descarga
    public void iniciarServicio(){
        // Se crea el servicio
        Intent intent = new Intent(getApplicationContext(), DownloaderService.class );
        // Se inicia el servicio
        startService(intent);
        // Nos ligamos al servicio
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    @Override
    public DownloaderService getDownloader() {
        return service;
    }
}