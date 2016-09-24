package com.android.training.androidtrining;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getCanonicalName();

    private Button btnLinear;
    private Button btnRelative;
    private Button btnGrid;
    private Button btnTable;

    private FrameLayout contenedor;

    private View.OnClickListener listener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            switch ( view.getId() ){
                case R.id.btnLinear:
                    setFragment( LinearFragment.getInstance("") );
                    break;
                case R.id.btnGrid:
                    setFragment( GridFragment.getInstance("") );
                    break;
                case R.id.btnRelative:
                    setFragment( RelativeFragment.getInstance("") );
                    break;
                case R.id.btnTable:
                    setFragment( TableFragment.getInstance("") );
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        btnLinear = (Button) findViewById(R.id.btnLinear);
        btnRelative =  (Button) findViewById(R.id.btnRelative);
        btnGrid =  (Button) findViewById(R.id.btnGrid);
        btnTable =  (Button) findViewById(R.id.btnTable);

        contenedor =  (FrameLayout) findViewById(R.id.contenedor);

        btnLinear  .setOnClickListener( listener );
        btnRelative.setOnClickListener( listener );
        btnGrid    .setOnClickListener( listener );
        btnTable    .setOnClickListener( listener );

        setFragment( LinearFragment.getInstance(TAG) );

        Log.i( TAG, "On Create");
    }

    private void setFragment(Fragment fragment){
        FragmentManager manager = this.getSupportFragmentManager();
        manager .beginTransaction()
                .replace(R.id.contenedor, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.i( TAG, "On Start");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i( TAG, "On Resume");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.i( TAG, "On Pause");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.i( TAG, "On Stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i( TAG, "On Destroy");
    }
}