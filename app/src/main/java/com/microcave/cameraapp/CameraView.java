package com.microcave.cameraapp;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback{

    private SurfaceHolder mHolder;
    private Camera mCamera;
    public Thread t;
    boolean show=false;

Context c;

    public CameraView(Context context, Camera camera){
        super(context);
        c=context;
        mCamera = camera;
        mCamera.setDisplayOrientation(0);
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try{
            //when the surface is created, we can set the camera to draw images in this surfaceholder

            if(mCamera==null)
            {
                mCamera=Camera.open();
            }
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();

        } catch (IOException e) {
            Log.d("ERROR", "Camera error on surfaceCreated " + e.getMessage());
        }
    }
public boolean isPreviewRunning= false;
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int width, int height) {
        //before changing the application orientation, you need to stop the preview, rotate and then start it again

            if (isPreviewRunning)
            {
                mCamera.stopPreview();
            }

            Camera.Parameters parameters = mCamera.getParameters();
            Display display = ((WindowManager)c.getSystemService(c.WINDOW_SERVICE)).getDefaultDisplay();
        if(display.getRotation() == Surface.ROTATION_0)
        {
            parameters.setPreviewSize(height, width);
            mCamera.setDisplayOrientation(90);
        }

        if(display.getRotation() == Surface.ROTATION_90)
        {
            parameters.setPreviewSize(width, height);
            mCamera.setDisplayOrientation(0);

        }
//
//        if(display.getRotation() == Surface.ROTATION_180)
//        {
//            parameters.setPreviewSize(height, width);
//        }
//
        if(display.getRotation() == Surface.ROTATION_270)
        {
            parameters.setPreviewSize(width, height);
            mCamera.setDisplayOrientation(180);
        }
//
//        mCamera.setParameters(parameters);
        start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //our app has only one screen, so we'll destroy the camera in the surface
        //if you are unsing with more screens, please move this code your activity
       // pause();
        Log.e("Camera Status", "Destroy called");
        mCamera.release();


    }





    void pause()
    {
        mCamera.stopPreview();
        //mCamera.release();
        show=false;

    }

    void start()
    {
        show = true;

        t= new Thread(new Runnable() {
            @Override
            public void run() {
                if(show) {
                    if (mHolder.getSurface() == null)//check if the surface is ready to receive camera data
                        return;

                    try {
                        mCamera.stopPreview();
                    } catch (Exception e) {
                        //this will happen when you are trying the camera if it's not running
                    }

                    //now, recreate the camera preview
                    try {
                        mCamera.setPreviewDisplay(mHolder);
                        mCamera.startPreview();
                        isPreviewRunning= true;
                    } catch (IOException e) {
                        Log.d("ERROR", "Camera error on surfaceChanged " + e.getMessage());
                    }
                }
            }
        });

        t.start();
    }



}