package com.android.training.androidtrining.customcontrol;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.training.androidtrining.R;
import com.android.training.androidtrining.database.DBManager;
import com.android.training.androidtrining.modelos.Libro;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Alejandro on 10/8/16.
 */
public class DownloaderService extends Service {
    private final String TAG = DownloaderService.class.getSimpleName();
    private LinkedHashMap<String, Libro> pila = new LinkedHashMap<>();
    private LinkedHashMap<String, Libro> completados = new LinkedHashMap<>();
    private HiloDeDescarga tarea = null;
    private final DownloaderBinder binder = new DownloaderBinder();
    private Context contexto;

    public static final String NUEVO_LIBRO = "NUEVO_LIBRO";

    public class DownloaderBinder extends Binder {
        public DownloaderService getService(){
            return DownloaderService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Servicion iniciado");
        contexto = getApplicationContext();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Servicion ligado a una actividad");
        contexto = getApplicationContext();
        return binder;
    }

    // Metodos para el manejo de la descarga de libros
    public boolean agregarAPila(Libro libro){
        // Checar si el libro es valido
        if( libro == null || libro.getTitulo() == null ){
            return false;
        }

        // Validar si las pilas ya contienen el libro
        if( completados.containsKey(libro.getTitulo()) ||
                pila.containsKey(libro.getTitulo())){
            return false;
        }

        pila.put(libro.getTitulo(), libro);

        iniciarDescarga();

        return true;
    }

    // Obtener el progreso de la descarga del libro
    public int getProgreso( Libro libro ){
        int progreso = -1;

        if( libro == null || libro.getTitulo() == null ){
            progreso = -1;
        } else if( completados.containsKey(libro.getTitulo()) ){
            progreso = 100;
        } else if( pila.containsKey(libro.getTitulo()) ){
            progreso = pila.get( libro.getTitulo() ).getProgresoDescarga();
        } else {
            progreso = -1;
        }

        //Log.i(TAG, "Libro: " + libro.getTitulo()  + "   Progeso: " + progreso);
        return progreso;
    }

    // Metodo para actualizar las descargas completadas
    public void setDescargaCompleta(Libro libro){
        if( libro == null || libro.getTitulo() == null ){
            return;
        }

        // Se registra en la pila el lbro descargado
        pila.remove( libro.getTitulo() );
        completados.put( libro.getTitulo(), libro);

        // Se guarda el nuevo libro en la base de datos
        DBManager.getInstance(getApplicationContext()).guardarLibro(libro);

        // Se public la insercion del nuevo libro
        sendBroadcast( new Intent(NUEVO_LIBRO));

        tarea = null;

        iniciarDescarga();
    }


    public void iniciarDescarga(){
        // Validar si existe hilo de descarga y esta corriendo
        if( tarea != null && tarea.getStatus() != AsyncTask.Status.FINISHED){
            return;
        }

        // Vlidar si se tienen descargas pendientes
        if( pila.size() < 1 ){
            return;
        }

        // Obtener el siguiente elemento en la pila
        Map.Entry<String, Libro> entry = pila.entrySet().iterator().next();

        // Agregar libro al hilo de descarga
        tarea = new HiloDeDescarga( entry.getValue() );
        tarea.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR );
    }

    // Hilo de descarga de libros
    private class HiloDeDescarga extends AsyncTask<Void, Integer, Libro>{
        private Libro libro;

        public HiloDeDescarga( Libro libro ){
            this.libro = libro;
            Log.i(TAG, "INIT DOWNLOAD Libro: " + libro.getTitulo());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            libro.setImagenFile(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS).getPath() +
                            File.separator +
                            libro.getTitulo() + "_" + libro.getAutor() + ".jpg"
            );

            libro.setPdfFile(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS).getPath() +
                            File.separator +
                            libro.getTitulo() + "_" + libro.getAutor() + ".pdf"
            );

            Picasso.with(getApplicationContext())
                    .load(libro.getImagenUrl())
                    .into( new ImageDownloadTarget( libro ));
        }

        @Override
        protected Libro doInBackground(Void... objects) {
            FileOutputStream fos = null;
            InputStream is = null;
            File file = null;
            float length = 0;
            int bytes;
            float progress;

            int porcentage = 0;
            int lastPorcentage = 0;

            Log.i(TAG, "doInBackground");
            try {

                // Validando que se tenga el nombre y url del archivo
                if( libro.getPdfUrl() == null ){
                    return null;
                }

                // Se crea la conexion con el servidor
                URL url = new URL(libro.getPdfUrl() );
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(20000);
                connection.setReadTimeout(20000);
                connection.setRequestMethod(contexto.getString(R.string.download_method));
                connection.setDoInput(true);

                // Se obtiene el tamaño del contenido
                List<String> content = connection.getHeaderFields().get("content-Length");
                if(content == null || content.size() < 1 ){
                    return null;
                } else {
                    for (String element : content) {
                        length += Integer.parseInt(element);
                    }
                }

                // Se establece la conexion para la descarga
                connection.connect();
                is = connection.getInputStream();

                // Se crea el archivo en el almacenamiento del dispositivo
                file = new File( libro.getPdfFile() );

                if( file.exists() ) {
                    is.skip(file.length());
                    fos = new FileOutputStream( file, true);
                } else {
                    fos = new FileOutputStream( file );
                }

                // Se inicia la descarga
                while((bytes = is.read()) != -1 ){

                    // Se escriben los bytes leidos en el dispositivo
                    fos.write( bytes );

                    // Se obtiene el porcentaje de progreso
                    progress = ((file.length() * 100) /  length );
                    porcentage = Math.round(progress);

                    // Se publica el porcentaje en caso que sea requerido
                    if( porcentage != lastPorcentage ){
                        lastPorcentage = porcentage;
                        publishProgress(  lastPorcentage );
                    }
                }
            } catch ( Exception ex ){
                Log.i(TAG, ex.getMessage());
                return null;
            }finally {

                // Liberando recursos
                try {
                    if( fos != null ) {
                        fos.close();
                    }
                }catch ( Exception ex ){
                    Log.i(TAG, ex.getMessage());
                }
                try{
                    if( is != null ){
                        is.close();
                    }
                } catch (Exception ex){
                    Log.i(TAG, ex.getMessage());
                }
            }

            return libro;
        }

        @Override
        protected void onProgressUpdate(Integer[] values) {
            super.onProgressUpdate(values);

            libro.setProgresoDescarga( values[0] );
            synchronized (DownloaderService.this){
                DownloaderService.this.notifyAll();
            }
        }

        @Override
        protected void onPostExecute(Libro libro) {
            super.onPostExecute(libro);

            setDescargaCompleta(libro);
            Log.i(TAG, "END  DOWNLOAD Libro: " + libro.getTitulo());
        }
    }

    private class ImageDownloadTarget implements Target {
        private Libro libro;
        public ImageDownloadTarget( Libro libro ){
            this.libro = libro;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            if( libro == null || libro.getImagenFile() == null ){
                return;
            }

            try{
                File archivo = new File( libro.getImagenFile() );
                archivo.createNewFile();
                FileOutputStream fos = new FileOutputStream(archivo);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                fos.flush();
                fos.close();
            } catch (Exception ex){
                Log.e(TAG, ex.getMessage());
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }
}