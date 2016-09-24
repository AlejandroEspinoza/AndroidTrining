package com.android.training.androidtrining;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Alejandro on 9/24/16.
 */
public class TableFragment extends Fragment {
    private static String param1 = "PARAMETRO_1";

    public static Fragment getInstance(String parametro){
        TableFragment fragmento = new TableFragment();

        Bundle bundle = new Bundle();
        bundle.putString(param1, parametro);

        fragmento.setArguments(bundle);

        return fragmento;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contenedor = inflater.inflate(R.layout.fragment_table, container, false);
        return contenedor;
    }
}