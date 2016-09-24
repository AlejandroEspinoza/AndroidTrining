package com.android.training.androidtrining;

import android.nfc.Tag;
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
public class GridFragment extends Fragment {
    private static String TAG = GridFragment.class.getSimpleName();
    private static String param1 = "PARAMETRO_1";

    public static Fragment getInstance(String parametro){
        GridFragment fragmento = new GridFragment();

        Bundle bundle = new Bundle();
        bundle.putString(param1, parametro);

        fragmento.setArguments(bundle);

        return fragmento;
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
        View contenedor = inflater.inflate(R.layout.fragment_grid, container, false);
        return contenedor;
    }
}
