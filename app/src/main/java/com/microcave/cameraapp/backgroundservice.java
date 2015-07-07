package com.microcave.cameraapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import junit.runner.Version;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class backgroundservice extends Service {
    public backgroundservice() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
    private SurfaceHolder sHolder;
    //a variable to control the camera
    private Camera mCamera;
    //the camera parameters
    private Camera.Parameters parameters;
    BroadcastReceiver mReceiver;
    private String Folder_Name="Camera Data";

    public Timer timer = new Timer();

    public   Timer t=new Timer();
    public  TimerTask ClickPhoto= new TakePhoto();
    ChangePhoto ChangeImage= new ChangePhoto();
    int count=5;

    SendImage SI= new SendImage();
    String Url= "https://image-judger.herokuapp.com/api/images";


    public   class ChangePhoto extends  TimerTask
    {

        @Override
        public void run() {
                        count--;
            Log.e("count",""+count);
                        if (count == 0) {
                            timer.cancel();
                            t = new Timer();
                            ClickPhoto = new TakePhoto();
                            Log.e("count","0");
                            t.schedule(ClickPhoto, 0, 10000);//10 =2
                        }
        }
    }

    public   class TakePhoto extends  TimerTask
    {
        @Override
        public void run() {
            Log.e("camera ", "pic" + count);

                    mCamera.takePicture(null, null, jpegCallback);


            if(count==5) {
                t.cancel();
                ClickPhoto.cancel();
                count=10;
                Log.e("update display", "");


                timer= new Timer();
                ChangeImage= new ChangePhoto();
                timer.schedule(ChangeImage, 0, 1000);//Update text every second

            }
            count++;
        }
    }


    SurfaceTexture surfaceTexture;
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);


        IntentFilter filter = new IntentFilter(Intent.ACTION_USER_PRESENT);
       // filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenLockReciever();
        registerReceiver(mReceiver, filter);

       mCamera = Camera.open();
surfaceTexture=new SurfaceTexture(10);
        try {
            mCamera.setPreviewTexture(surfaceTexture);
        } catch (IOException e1) {
            Log.e("Version.APP_ID", e1.getMessage());
        }

        Camera.Parameters params = mCamera.getParameters();

        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        params.setPictureFormat(ImageFormat.JPEG);
        mCamera.setParameters(params);
        mCamera.startPreview();
        mCamera.setPreviewCallbackWithBuffer(null);

        timer = new Timer();
        ClickPhoto = new TakePhoto();
        ChangeImage = new ChangePhoto();
        t = new Timer();
        count = 10;

        timer.schedule(ChangeImage, 0, 1000);//Update text every second

      //  mCamera.takePicture(null, null, null, jpegCallback);        //mcall
    }

    Camera.PictureCallback mCall = new Camera.PictureCallback()
    {


        public void onPictureTaken(byte[] data, Camera camera)
        {
            //decode the data obtained by the camera into a Bitmap

            FileOutputStream outStream = null;
            try{
                outStream = new FileOutputStream("/sdcard/Image.jpg");
                outStream.write(data);
                outStream.close();
                mCamera.release();


                Log.e("Camera Status", "saved photo");
            } catch (FileNotFoundException e){
                Log.d("CAMERA", e.getMessage());
            } catch (IOException e){
                Log.d("CAMERA", e.getMessage());
            }

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Camera Status", "service destry");
       // mCamera.release();

    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
        Log.e("service ", "Starting unbind and activty");

    }

    @Override
    public boolean onUnbind(Intent intent) {

        unregisterReceiver(mReceiver);

        t.cancel();
        timer.cancel();
        ClickPhoto.cancel();
        ChangeImage.cancel();


        mCamera.release();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(i);
        Log.e("service ", "unbind  called");
        return super.onUnbind(intent);

    }
    void reset() {
        mCamera.startPreview();
    }

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            new SaveImageTask().execute(data);
            reset();
            Log.d("TAG", "onPictureTaken - jpeg");
        }

    };


    public class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground(byte[]... data) {
            FileOutputStream outStream = null;

            // Write to SD Card
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File (sdCard.getAbsolutePath() + "/"+Folder_Name);
                dir.mkdirs();

                String fileName = String.format("%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir, fileName);

                outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);
                outStream.flush();
                outStream.close();

                Log.d("TAG", "onPictureTaken - wrote bytes: " + data.length + " to " + outFile.getAbsolutePath());
               // ImagePath.add(outFile.getAbsolutePath());

                refreshGallery(outFile);

                //============ file is succesfully saved ==========================


                SI.sendPost(Url,outFile.getAbsolutePath());

                //===================================================================


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
            return null;
        }

    }
    public void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }




}
