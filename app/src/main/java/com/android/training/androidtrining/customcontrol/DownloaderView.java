package com.android.training.androidtrining.customcontrol;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.training.androidtrining.R;
import com.android.training.androidtrining.modelos.Libro;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Alejandro on 10/1/16.
 */
public class DownloaderView extends TextView {
    private static final String TAG = DownloaderView.class.getSimpleName();

    // Medidas del componente
    private int widthSize;
    private int heightSize;
    private float margenLeft;
    private float margenTop;
    private float margenRight;
    private float margenBottom;

    // Colores de los componentes
    private int colorFondo;
    private int colorBorde;

    // Configuracion del pintado de pixeles
    private Paint pinturaBorde;

    // Trazo que se pintara in la interfaz
    private Path arco;

    // Diametro del componente
    private float diametro;

    // Ancho del borde del arco
    private float anchoBorde = 0;

    // El contexto contiene el ambiente en el que corre la vista (View)
    private Context context;

    // Servicio de descarga de archivos
    private DownloaderService servicio = null;

    // Libro que se descargara
    private Libro libro = null;

    // Hilo que monitorea la descarga del libro en el componente "Servicio"
    private DownloaderTask obserbador = null;

    // Constructores de la vista
    public DownloaderView(Context context){
        this(context, null);
    }

    public DownloaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownloaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DownloaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        this.context = context;

        // Se alinea el contenido al centroa
        setGravity(Gravity.CENTER);

        // Se limpia el fondo de la vista
        setBackgroundColor(ContextCompat.getColor( context, R.color.app_transparente));

        // Se cargan las medidas (etilos) de la vista (View)
        int[] attributeSetList = new int[]{
            android.R.attr.padding,
            android.R.attr.paddingLeft,
            android.R.attr.paddingTop,
            android.R.attr.paddingRight,
            android.R.attr.paddingBottom
        };

        TypedArray ta = context.obtainStyledAttributes( attrs, attributeSetList );
        float margen = ta.getDimension(0, 0);
        margenLeft = ta.getDimension(1, margen);
        margenTop = ta.getDimension(2, margen);
        margenRight = ta.getDimension(3, margen);
        margenBottom = ta.getDimension(4, margen);
        ta.recycle();

        ta = context.obtainStyledAttributes( attrs, R.styleable.download_view );
        colorFondo = ta.getColor(R.styleable.download_view_color_fondo, ContextCompat.getColor(context, R.color.app_verde));
        colorBorde = ta.getColor(R.styleable.download_view_color_borde, ContextCompat.getColor(context, R.color.app_verde));
        ta.recycle();

        // Se obtiene el borde del arco desde el archivo "dimensions"
        anchoBorde = context.getResources().getDimension(R.dimen.download_stroke);

        // Se cargan Paints para dibujar el arco
        cargarComponentes(context);

        // Se pone el estado inicial de la vista
        estadiInicial();
    }

    // Escucha interacciones con el usuario
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch ( event.getAction() ){
            case MotionEvent.ACTION_DOWN:
                // Se atrapa el evento para escuchar las distintas acciones del mismo
                return true;
            case MotionEvent.ACTION_UP:
                // Se inicia la descarga una vez que el usuario despega el dedo de la pantalla
                iniciarDescarga();
        }

        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Se obtiene el modo de la medida del componente
        int widthMode = MeasureSpec.getMode( widthMeasureSpec );
        int heightMode = MeasureSpec.getMode( heightMeasureSpec );

        // Una vez que los modos sean exactos se cargan las medidas del componente
        if( widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY ){
            // Dimensiones exactas
            widthSize = MeasureSpec.getSize( widthMeasureSpec );
            heightSize = MeasureSpec.getSize( heightMeasureSpec );
            definirMedidas();
        } else if( widthMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.AT_MOST ){
            // Dimensiones no exactas
        } else {
            // Dimensiones no especificadas
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Se dibuja un trazo en pantalla
        canvas.drawPath( arco, pinturaBorde);
    }

    // Se definen las medidas que tendra el circulo de progreso
    private void definirMedidas(){
        // Se evalua si el componente es cuadrado
        // En caso de tener medidas iguales
        if( heightSize == widthSize ) {
            // Se calcula el diametro del componente
            diametro = widthSize - margenLeft - margenRight;
        // En caso de tener diferentes medidas por lado, se igualan y se invalida el componente
        } else if( heightSize > widthSize ) {
            this.getLayoutParams().height = widthSize;
            this.requestLayout();
        } else {
            this.getLayoutParams().width = heightSize;
            this.requestLayout();
        }
    }

    // Se cargan componentes necesarios para la ejecucion de la descarga
    public void setLibro( DownloaderService servicio, Libro libro ){
        this.servicio = servicio;
        this.libro = libro;

        int progreso = servicio.getProgreso(libro);

        // Si el libro se esta descargando se inicia el monitoreo de la descarga
        if( progreso >= 100 ){
            estadoCompletado();
        } else if( progreso < 0 ){
            estadiInicial();
        } else {
            iniciarDescarga();
            setTag("DOWNLOADING");
        }
    }

    // Se reinicia el componente
    public void limpiarComponente(){
        // Eliminar ligas anteriores
        this.servicio = null;
        this.libro = null;

        // Parar el monitoreo de la descarga
        if( obserbador != null && obserbador.getStatus() != AsyncTask.Status.FINISHED ){
            obserbador.cancel(true);
            obserbador = null;
        }

        // Limpiar la interfaz
        this.estadiInicial();

        // Se calcula el arco respectivo al progreso
        arco.addArc(
                margenLeft,
                margenTop,
                margenLeft + diametro,
                margenTop + diametro,
                270,
                0 );

        // Se invalida la interfaz para repintarse el progreso
        this.invalidate();
    }

    public boolean iniciarDescarga(){
        // Validamos si contamos con el libro a descargar
        // Y el servicio de descarga
        if( libro == null || servicio == null ){
            return false;
        }

        // Se valida si el obserbador de la descarga esta activo
        if( obserbador != null && obserbador.getStatus() != AsyncTask.Status.FINISHED ){
            return true;
        }

        if( obserbador != null ){
            Log.i(TAG, "Obserbador not null,  state: " + obserbador.getStatus());
        } else {
            Log.i(TAG, "Obserbador null,  state: NULL");
        }
        // Se crea un hilo y se ejecuta de manera secuencial
        obserbador = new DownloaderTask();
        obserbador.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        // Iniciar la descarga del archivo
        servicio.agregarAPila(libro);

        return true;
    }

    // Se agrega el progreso de la descarga al libro en cuestion
    private void setProgreso( float progreso ){

        // Se alinea el progreso a las medidas permitidas (0 - 100)
        if( progreso < 0){
            progreso = 0;
        } else if( progreso > 100 ){
            progreso = 100;
        }

        // Se pinta el progreso en pantalla
        estadoDescarga( progreso );
        progreso = progreso * 3.6f;

        // Se calcula el arco respectivo al progreso
        arco.addArc(
                margenLeft,
                margenTop,
                margenLeft + diametro,
                margenTop + diametro,
                270,
                progreso );

        // Se invalida la interfaz para repintarse el progreso
        this.invalidate();
    }

    // Se crea el pincel (Paint) con el que se pintara el arco y se crea el objeto que pintara la ruta
    private void cargarComponentes(Context context){
        pinturaBorde = new Paint();
        pinturaBorde.setStyle(Paint.Style.STROKE );
        pinturaBorde.setColor( colorBorde );
        pinturaBorde.setStrokeWidth(anchoBorde);

        arco = new Path();
    }

    // Tarea ascincrona que monitorea el progreso de las descargas
    private class DownloaderTask extends AsyncTask<Void, Void, Void>{
        private float progreso = 0;

        public DownloaderTask(){
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Se busca actualizacion del progreso de la descarga
            progreso = servicio.getProgreso(libro);

            if( progreso > 0 ){
                setProgreso( progreso );
            } else {
                estadoEspera();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {

            // mientras el progreso sea menor a l 100%  se monitorea el avance de la descarga
            while ( progreso < 100  ){
                try{
                    // Se espera a que el servicio de descarga notifique un avance
                    synchronized (servicio) {
                        servicio.wait(500);
                    }
                } catch (Exception ex ){
                    Log.i(TAG, ex.getMessage());
                }
                publishProgress();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

            // Se busca actualizacion del progreso de la descarga
            progreso = servicio.getProgreso(libro);

            if( progreso > 0 ){
                setProgreso( progreso );
            } else {
                estadoEspera();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // La descarga fue completada
            estadoCompletado();
        }
    }



    // Estados de la descarga
    private enum Estado{
        INICIAL, ESPERA, DESCARGA, COMPLETADO, ERROR
    }
    private Estado estado;

    private void estadiInicial(){
        if( estado != null && estado == Estado.INICIAL ){
            return;
        }

        estado = Estado.INICIAL;
        // Agregar imagen de fondo
        setBackground( ContextCompat.getDrawable(context, R.drawable.download));
        // Agregar el texto requerido
        setText("");
    }

    private void estadoEspera(){
        if( estado != null && estado == Estado.ESPERA ){
            return;
        }

        estado = Estado.ESPERA;
        // Agregar imagen de fondo
        setBackground( ContextCompat.getDrawable(context, R.drawable.waiting));
        // Agregar el texto requerido
        setText("");
        Log.i(TAG, "Libro:" + libro.getTitulo() + "   Estado: ESPERA");
    }

    private void estadoDescarga(){
        estadoDescarga(0);
    }

    private void estadoDescarga( float position ){
        // Agregar el texto requerido
        setText( position + "%");

        if( estado != null && estado == Estado.DESCARGA ){
            return;
        }

        estado = Estado.DESCARGA;
        // Agregar imagen de fondo
        setBackground( ContextCompat.getDrawable(context, R.drawable.processing));

        Log.i(TAG, "Libro:" + libro.getTitulo() + "   Estado: DESCARGA");
    }

    private void estadoCompletado(){
        if( estado != null && estado == Estado.COMPLETADO ){
            return;
        }

        estado = Estado.COMPLETADO;
        // Agregar imagen de fondo
        setBackground( ContextCompat.getDrawable(context, R.drawable.completed));
        // Agregar el texto requerido
        setText("");
    }

    private void estadiError(){
        if( estado != null && estado == Estado.ERROR ){
            return;
        }

        estado = Estado.ERROR;
        // Agregar imagen de fondo
        setBackground( ContextCompat.getDrawable(context, R.drawable.error));
        // Agregar el texto requerido
        setText("");
    }
}