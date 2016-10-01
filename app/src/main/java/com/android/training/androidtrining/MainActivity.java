package com.android.training.androidtrining;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.android.training.androidtrining.customcontrol.DownloaderView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getCanonicalName();

    private ViewPager viewPager;
    private Adaptador pagerAdapter;
    private TabLayout tabLayout;
    private DownloaderView downloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        List<Fragment> fragmentos = new ArrayList<>();
        fragmentos.add( GridFragment.getInstance(""));
        fragmentos.add( LinearFragment.getInstance(""));
        fragmentos.add( RelativeFragment.getInstance(""));
        fragmentos.add( TableFragment.getInstance(""));

        viewPager =  (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        pagerAdapter = new Adaptador( this, fragmentos );
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        downloader = (DownloaderView) findViewById(R.id.downloader);

        // Validacion de permisos necesaria para Androis 6+
        int REQUEST_EXTERNAL_STORAGE = 200;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        int writePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        Log.i( TAG, "On Create");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if( requestCode == 200 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
            downloader.setAccesosAprobados(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        downloader.iniciarDescarga();
    }

    private class Adaptador extends FragmentPagerAdapter {
        List<Fragment> paginas;

        public Adaptador( AppCompatActivity contexto, List<Fragment> paginas ){
            super( contexto.getSupportFragmentManager() );
            this.paginas = paginas;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return paginas.get(position).getClass().getSimpleName();
        }

        @Override
        public int getCount() {
            return (paginas == null)? 0 : paginas.size();
        }

        @Override
        public Fragment getItem(int position) {
            return paginas.get(position);
        }
    }
}