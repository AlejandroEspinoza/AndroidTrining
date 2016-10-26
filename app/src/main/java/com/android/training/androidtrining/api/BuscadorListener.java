package com.android.training.androidtrining.api;

import com.android.training.androidtrining.customcontrol.DownloaderService;
import com.android.training.androidtrining.modelos.Libro;

/**
 * Created by Alejandro on 10/8/16.
 */
public interface BuscadorListener {
    public void addLibro( Libro libro );
    public void addDownloaderService(DownloaderService service );
    public void tareaTerminada(boolean resultado);
}