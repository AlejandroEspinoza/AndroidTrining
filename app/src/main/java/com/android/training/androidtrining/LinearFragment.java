package com.android.training.androidtrining;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Alejandro on 9/24/16.
 */
public class LinearFragment extends Fragment {
    private static String TAG = LinearFragment.class.getSimpleName();
    private static String param1 = "PARAMETRO_1";

    public static Fragment getInstance(String parametro){
        LinearFragment fragmento = new LinearFragment();

        Bundle bundle = new Bundle();
        bundle.putString(param1, parametro);

        fragmento.setArguments(bundle);

        return fragmento;
    }

    public LinearFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contenedor = inflater.inflate(R.layout.fragment_linear, container, false);
        return contenedor;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if( context == null ){
            Log.i( TAG, "Attached to null");
        } else if( context instanceof  MainActivity ){
            Log.i( TAG, "Attached to MainActivity");
        } else {
            Log.i( TAG, "Attached to " + context.getClass().getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        Log.i( TAG, "Detached");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i( TAG, "On Create");
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.i( TAG, "On Start");
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i( TAG, "On Resume");
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.i( TAG, "On Pause");
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.i( TAG, "On Stop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i( TAG, "On Destroy");
    }
}