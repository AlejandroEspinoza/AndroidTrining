package com.android.training.androidtrining;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alejandro on 9/24/16.
 */
public class RelativeFragment extends Fragment {
    private static String TAG = RelativeFragment.class.getSimpleName();
    private static String param1 = "PARAMETRO_1";

    private RecyclerView recyclerView;
    private Adaptador recyclerAdapter;

    public static Fragment getInstance(String parametro){
        RelativeFragment fragmento = new RelativeFragment();

        Bundle bundle = new Bundle();
        bundle.putString(param1, parametro);

        fragmento.setArguments(bundle);

        return fragmento;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contenedor = inflater.inflate(R.layout.fragment_relative, container, false);

        List<String> contenido = new ArrayList<>();
        contenido.add("Uno");
        contenido.add("Dos");
        contenido.add("Tres");
        contenido.add("Cuatro");
        contenido.add("Cinco");
        contenido.add("Seis");
        contenido.add("Siete");
        contenido.add("Ocho");
        contenido.add("Nueve");
        contenido.add("Dies");
        contenido.add("Once");

        recyclerAdapter = new Adaptador( contenido );
        recyclerView = (RecyclerView) contenedor.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager( new LinearLayoutManager(getActivity().getApplicationContext()) );
        recyclerView.setAdapter( recyclerAdapter );

        return contenedor;
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

    private class Adaptador extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private List<String> datos;

        class Renglon extends RecyclerView.ViewHolder{
            private TextView rowLabel;

            public Renglon( View raiz ){
                super(raiz);
                rowLabel = (TextView) raiz.findViewById(R.id.rowLabel);
            }

            public void setText( String text ){
                rowLabel.setText( text );
            }
        }

        public Adaptador( List<String> datos ){
            super();
            this.datos = datos;
        }

        @Override
        public int getItemCount() {
            return ( datos != null )? datos.size() : 0;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public Renglon onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.row_recycler, parent, false );
            return new Renglon(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((Renglon)holder).setText( datos.get(position) );
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            super.onViewRecycled(holder);
        }
    }
}