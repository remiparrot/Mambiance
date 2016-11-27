package org.fasol.mambiance;

/**
 * Created by fasol on 27/11/16.
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;

public class RoseSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    SurfaceHolder mSurfaceHolder;
    DrawingThread mThread;

    Paint mPaint;

    SeekBar o,t,v,a;
    FrameLayout frame_layout;

    /**
     * Constructeur utilisé pour inflater avec un style
     */
    public RoseSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Constructeur utilisé pour inflater sans style
     */
    public RoseSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Constructeur utilisé pour construire dans le code
     */
    public RoseSurfaceView(Context context, SeekBar o, SeekBar t, SeekBar v, SeekBar a) {
        super(context);

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        mThread = new DrawingThread();

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLACK);

        this.o=o;
        this.a=a;
        this.t=t;
        this.v=v;

    }


    @Override
    protected void onDraw(Canvas pCanvas) {

        pCanvas.drawColor(Color.WHITE);

        // trace axe
        mPaint.setStrokeWidth(2);
        pCanvas.drawLine(this.getX()+this.getWidth()/2, this.getY(),this.getX()+this.getWidth()/2,this.getY()+this.getHeight(),mPaint);
        pCanvas.drawLine(this.getX(), this.getY()+this.getHeight()/2,this.getX()+this.getWidth(),this.getY()+this.getHeight()/2,mPaint);

        // trace rose
        mPaint.setStrokeWidth(3);
        float oThumbPosX = (this.getWidth()/2) + this.getX();
        float oThumbPosY = (this.getHeight()/2) + this.getY() - (this.getHeight()/2) * o.getProgress() / o.getMax();
        float tThumbPosX = (this.getWidth()/2) + this.getX() + (this.getWidth()/2) * t.getProgress() / t.getMax();
        float tThumbPosY = (this.getHeight()/2) + this.getY();
        float vThumbPosX = (this.getWidth()/2) + this.getX();
        float vThumbPosY = (this.getHeight()/2) + this.getY() + (this.getHeight()/2) * v.getProgress() / v.getMax();
        float aThumbPosX = (this.getWidth()/2) + this.getX() - (this.getWidth()/2) * a.getProgress() / a.getMax();
        float aThumbPosY = (this.getHeight()/2) + this.getY();
        
        pCanvas.drawLine(oThumbPosX,oThumbPosY,tThumbPosX,tThumbPosY,mPaint);
        pCanvas.drawLine(tThumbPosX,tThumbPosY,vThumbPosX,vThumbPosY,mPaint);
        pCanvas.drawLine(vThumbPosX,vThumbPosY,aThumbPosX,aThumbPosY,mPaint);
        pCanvas.drawLine(aThumbPosX,aThumbPosY,oThumbPosX,oThumbPosY,mPaint);

        super.onDraw(pCanvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mThread.keepDrawing = true;
        mThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mThread.keepDrawing = false;

        boolean joined = false;
        while (!joined) {
            try {
                mThread.join();
                joined = true;
            } catch (InterruptedException e) {}
        }
    }

    private class DrawingThread extends Thread {
        // Utilisé pour arrêter le dessin quand il le faut
        boolean keepDrawing = true;

        @Override
        public void run() {

            while (keepDrawing) {
                Canvas canvas = null;

                try {
                    // On récupère le canvas pour dessiner dessus
                    canvas = mSurfaceHolder.lockCanvas();
                    // On s'assure qu'aucun autre thread n'accède au holder
                    synchronized (mSurfaceHolder) {
                        // Et on dessine
                        onDraw(canvas);
                    }
                } finally {
                    // Notre dessin fini, on relâche le Canvas pour que le dessin s'affiche
                    if (canvas != null)
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                }

                // Pour dessiner à 50 fps
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {}
            }
        }
    }
}

