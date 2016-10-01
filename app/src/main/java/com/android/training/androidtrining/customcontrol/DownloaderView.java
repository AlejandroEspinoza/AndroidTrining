package com.android.training.androidtrining.customcontrol;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.android.training.androidtrining.R;

import java.util.concurrent.TimeUnit;

/**
 * Created by Alejandro on 10/1/16.
 */
public class DownloaderView extends TextView {
    private static final String TAG = DownloaderView.class.getSimpleName();

    int widthSize;
    int heightSize;
    float margenLeft;
    float margenTop;
    float margenRight;
    float margenBottom;

    int colorFondo;
    int colorBorde;

    boolean configurado = false;

    Paint pinturaBorde;
    Paint pinturaFondo;
    Path arco;

    float diametro;
    float radio;

    float anchoBorde = 0;

    boolean permisosAprobados = false;

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

        // Configuracion
        setText("0%");
        setGravity(Gravity.CENTER);
        setBackgroundColor(ContextCompat.getColor( context, R.color.app_transparente));

        int[] attributeSetList = new int[]{
            android.R.attr.padding,
            android.R.attr.paddingLeft,
            android.R.attr.paddingTop,
            android.R.attr.paddingRight,
            android.R.attr.paddingBottom,
            android.R.attr.layout_height,
            android.R.attr.layout_width
        };

        TypedArray ta = context.obtainStyledAttributes( attrs, attributeSetList );
        float margen = ta.getDimension(0, 0);
        margenLeft = ta.getDimension(1, margen);
        margenTop = ta.getDimension(2, margen);
        margenRight = ta.getDimension(3, margen);
        margenBottom = ta.getDimension(4, margen);
        int heightValue = ta.getInt(5, 0);
        int widthValue = ta.getInt(6, 0);
        ta.recycle();

        ta = context.obtainStyledAttributes( attrs, R.styleable.download_view );
        colorFondo = ta.getColor(R.styleable.download_view_color_fondo, 0);
        colorBorde = ta.getColor(R.styleable.download_view_color_borde, 0);
        ta.recycle();

        anchoBorde = context.getResources().getDimension(R.dimen.download_stroke);

        cargarComponentes(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode( widthMeasureSpec );
        int heightMode = MeasureSpec.getMode( heightMeasureSpec );

        if( widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY ){
            widthSize = MeasureSpec.getSize( widthMeasureSpec );
            heightSize = MeasureSpec.getSize( heightMeasureSpec );
            definirMedidas();
            Log.i(TAG,  "Dimensiones exactas");
        } else if( widthMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.AT_MOST ){
            Log.i(TAG,  "Dimensiones no exactas");
        } else {
            Log.i(TAG,  "Dimensiones no especificadsas");
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(
                radio + margenLeft,
                radio + margenTop,
                radio,
                pinturaFondo);
        canvas.drawPath( arco, pinturaBorde);
    }

    private void definirMedidas(){
        diametro = (heightSize > widthSize)?
                (widthSize - margenLeft - margenRight):
                (heightSize - margenTop - margenBottom);
        radio = diametro / 2;

        setProgreso( 0 );
    }

    public void setAccesosAprobados( boolean permisosAprobados ){
        this.permisosAprobados = permisosAprobados;
    }

    public boolean iniciarDescarga(){
        if( permisosAprobados ) {
            DownloaderTask task = new DownloaderTask(this);
            task.execute();
            return true;
        } else {
            return false;
        }
    }

    private void setProgreso( float progreso ){

        if( progreso < 0){
            progreso = 0;
        } else if( progreso > 100 ){
            progreso = 100;
        }

        this.setText( progreso + "%");
        progreso = progreso * 3.6f;

        arco.addArc(
                margenLeft,
                margenTop,
                margenLeft + diametro,
                margenTop + diametro,
                270,
                progreso );

        this.invalidate();
    }

    private void cargarComponentes(Context context){
        pinturaBorde = new Paint();
        pinturaBorde.setStyle(Paint.Style.STROKE );
        pinturaBorde.setColor( colorBorde );
        pinturaBorde.setStrokeWidth(anchoBorde);

        pinturaFondo = new Paint();
        pinturaFondo.setStyle(Paint.Style.FILL_AND_STROKE );
        pinturaFondo.setColor( colorFondo );
        pinturaFondo.setAlpha( 200 );
        pinturaBorde.setStrokeWidth(anchoBorde);

        arco = new Path();
    }

    private class DownloaderTask extends AsyncTask<Void, Integer, Void>{
        DownloaderView downloader;

        public DownloaderTask( DownloaderView downloader ){
            this.downloader = downloader;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            downloader.setVisibility(VISIBLE);
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.i(TAG, "doInBackground");
            try {
                for (int i = 0; i <= 100 ; i++) {
                    Log.i(TAG, "Progress update " + i );
                    TimeUnit.MILLISECONDS.sleep(100);
                    publishProgress( i );
                }

                TimeUnit.SECONDS.sleep(3);

            } catch ( Exception ex ){
                Log.i(TAG, ex.getMessage());
                return null;
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.i(TAG, "onProgressUpdate");
            super.onProgressUpdate(values);
            downloader.setProgreso( values[0] );
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i(TAG, "onPostExecute");
            super.onPostExecute(aVoid);
            downloader.setVisibility(GONE);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            downloader.setVisibility(GONE);
        }
    }
}
