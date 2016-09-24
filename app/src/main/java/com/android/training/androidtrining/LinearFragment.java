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

    // Evalua si el usuario puede ver el fragmento
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if( isVisibleToUser ){
            Log.i(TAG, TAG + " Es visible");
        } else {
            Log.i(TAG, TAG + " No es visible");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contenedor = inflater.inflate(R.layout.fragment_linear, container, false);
        return contenedor;
    }
}