package com.android.training.androidtrining;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i( TAG, "On Create");
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