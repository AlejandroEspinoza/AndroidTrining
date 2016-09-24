package com.android.training.androidtrining;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getCanonicalName();

    private Button btnLinear;
    private Button btnFrame;
    private FrameLayout contenedor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        btnLinear = (Button) findViewById(R.id.btnLinear);
        btnFrame =  (Button) findViewById(R.id.btnFrame);
        contenedor =  (FrameLayout) findViewById(R.id.contenedor);

        setFragment( LinearFragment.getInstance(TAG) );

        Log.i( TAG, "On Create");
    }

    private void setFragment(Fragment fragment){
        FragmentManager manager = this.getSupportFragmentManager();
        manager .beginTransaction()
                .add(R.id.contenedor, fragment, fragment.getClass().getSimpleName())
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