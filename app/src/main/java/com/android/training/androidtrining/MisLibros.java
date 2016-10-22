package com.android.training.androidtrining;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Alejandro on 10/8/16.
 */
public class MisLibros extends Fragment {

    public static MisLibros getInstance(){
        return new MisLibros();
    }

    public MisLibros(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_mis_libros, container, false );

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
