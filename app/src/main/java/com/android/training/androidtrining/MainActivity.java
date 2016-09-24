package com.android.training.androidtrining;

import android.content.Context;
import android.support.design.widget.TabLayout;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getCanonicalName();

    private ViewPager viewPager;
    private Adaptador pagerAdapter;
    private TabLayout tabLayout;

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

        Log.i( TAG, "On Create");
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