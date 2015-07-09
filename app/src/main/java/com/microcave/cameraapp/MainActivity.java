package com.microcave.cameraapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity {
    private Camera mCamera = null;
    private CameraView mCameraView = null;
    private String Folder_Name = "Camera Data";
    OrientationEventListener listner;
    public Context c;

    public boolean Serviceresult;

    SendImage SI = new SendImage();

    String Url = "https://image-judger.herokuapp.com/api/images";

    ArrayList<String> ImagePath = new ArrayList<String>();
    int count = 10;
    ImageView i;
    Intent is;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        c = this;
        super.onCreate(savedInstanceState);
        Log.e("activity ", "started");
        Serviceresult = true;
        is = new Intent(this, backgroundservice.class);


        setContentView(R.layout.activity_main);
        i = (ImageView) findViewById(R.id.imageView);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        try {
            mCamera = Camera.open();//you can use open(int) to use different cameras
        } catch (Exception e) {
            Log.d("ERROR", "Failed to get camera: " + e.getMessage());
        }

        if (mCamera != null) {
            mCameraView = new CameraView(this, mCamera);//create a SurfaceView to show camera data
            FrameLayout camera_view = (FrameLayout) findViewById(R.id.camera_view);
            camera_view.addView(mCameraView);//add the SurfaceView to the layout
        }

        ImageButton imgClose = (ImageButton) findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // System.exit(0);
                Delete();
            }
        });
        listner = new OrientationEventListener(c) {
            @Override
            public void onOrientationChanged(int orientation) {

                if (mCamera == null) {
                    mCamera = Camera.open();
                }

                if (orientation == 180) {
                    mCameraView.rotation_In_progress = true;
                    Log.e("Camera Status", "180 called " + mCameraView.rotation_In_progress);
                }
                if (orientation == 90) {
                    mCameraView.rotation_In_progress = true;
                    Log.e("Camera Status", "90 called " + mCameraView.rotation_In_progress);
                }
                if (orientation == 270) {
                    mCameraView.rotation_In_progress = true;
                    Log.e("Camera Status", "270 called " + mCameraView.rotation_In_progress);
                }
            }
        };

        listner.enable();


    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.e("Camera Status", "Pause called");
        mCamera.release();


        mCameraView.getHolder().removeCallback(mCameraView);

        s.interrupt();

        this.bindService(is, mServerConn, BIND_AUTO_CREATE);
        this.startService(is);
        isconnected = true;
        Serviceresult = false;
        run_thread = false;
        finish();

    }

    protected ServiceConnection mServerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.d("service ", "onServiceConnected");


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("service ", "onServiceDisconnected");
        }
    };
    Boolean isconnected = false;
    Thread s = new cameraintializer();
    Boolean run_thread = true;


    @Override
    protected void onResume() {
        super.onResume();

        Log.e("Camera Status", "resume called");
        if (isconnected) {
            mCamera = null;
            Log.e("is connected ", "true");
            this.stopService(new Intent(this, backgroundservice.class));
            this.unbindService(mServerConn);
            Serviceresult = false;        //-----
            try {
                Thread thread = new cameraintializer();
                thread.start();
                synchronized (thread) {
                    thread.wait(1000);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.finish();


        }


        if (Serviceresult) {
            Log.e("service result", "true");
            run_thread = true;
            s.start();
        }

    }

    private class cameraintializer extends Thread {
        @Override
        public void run() {
            if (run_thread) {
                if (mCamera == null) {
                    mCamera = Camera.open();
                }
                mCameraView.getHolder().addCallback(mCameraView);
                // mCameraView.change(mCamera);

                timer = new Timer();
                ClickPhoto = new TakePhoto();
                ChangeImage = new ChangePhoto();
                t = new Timer();
                count = 10;

                timer.schedule(ChangeImage, 0, 1000);//Update text every second
            }
        }
    }

    public Timer timer = new Timer();

    public Timer t = new Timer();
    public TimerTask ClickPhoto = new TakePhoto();
    ChangePhoto ChangeImage = new ChangePhoto();

    public class ChangePhoto extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (count > 0) {

                        if (count == 10) {
                            i.setVisibility(View.VISIBLE);
                            i.setBackgroundResource(R.drawable.a10);


                        }
                        if (count == 9) {
                            i.setBackgroundResource(R.drawable.a9);
                        }

                        if (count == 8) {
                            i.setBackgroundResource(R.drawable.a8);

                        }
                        if (count == 7) {
                            i.setBackgroundResource(R.drawable.a7);
                        }
                        if (count == 6) {
                            i.setBackgroundResource(R.drawable.a6);
                        }
                        if (count == 5) {
                            i.setBackgroundResource(R.drawable.a5);
                        }
                        if (count == 4) {
                            i.setBackgroundResource(R.drawable.a4);
                        }
                        if (count == 3) {
                            i.setBackgroundResource(R.drawable.a3);
                        }
                        if (count == 2) {
                            i.setBackgroundResource(R.drawable.a2);

                        }

                        if (count == 1) {
                            i.setBackgroundResource(R.drawable.a1);
                        }

                        count--;

                    } else

                    {
                        if (count == 0) {
                            i.setVisibility(View.GONE);
                            timer.cancel();
                            t = new Timer();
                            ClickPhoto = new TakePhoto();
                            t.schedule(ClickPhoto, 0, 10000);//10 =2

                        }
                    }
                }
            });

        }
    }

    public class TakePhoto extends TimerTask {


        @Override
        public void run() {
            if (!mCameraView.rotation_In_progress) {
                if (Serviceresult)                   //checks activity is on top or not
                {
                    mCamera.takePicture(null, null, jpegCallback);
                }
            }
            if (count == 5) {
                t.cancel();
                count = 10;
                Log.e("update display", "");


                timer = new Timer();
                ChangeImage = new ChangePhoto();
                timer.schedule(ChangeImage, 0, 1000);//Update text every second

            }
            count++;
        }
    }


//   public void updateDisplay() {
//        timer = new Timer();
//        count = 10;
//        Log.e("update dasply", "");
//
//        timer.schedule(ChangeImage, 0, 1000);//Update text every second
//
//
//    }


    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            new SaveImageTask().execute(data);
            reset();
            Log.d("TAG", "onPictureTaken - jpeg");
        }

    };

    void reset() {
        mCamera.startPreview();
    }

    public class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground(byte[]... data) {
            FileOutputStream outStream = null;

            // Write to SD Card
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/" + Folder_Name);
                dir.mkdirs();

                String fileName = String.format("%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir, fileName);

                outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);
                outStream.flush();
                outStream.close();

                Log.e("TAG", "onPictureTaken - wrote bytes: " + data.length + " to " + outFile.getAbsolutePath());
                ImagePath.add(outFile.getAbsolutePath());

                refreshGallery(outFile);

                //============ file is succesfully saved ==========================


                SI.sendPost(Url, outFile.getAbsolutePath());


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
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }


    void Delete() {
        try {
            for (int i = 0; i < ImagePath.size(); i++) {

                String image = ImagePath.get(i);
                Log.d("delte Image path", image);
                File f = new File(image);
                f.delete();
            }
            Toast.makeText(this, "All Images are deleted ", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.getMessage();
            Toast.makeText(this, "nothing to delte ", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unbindService(mServerConn);
        this.stopService(is);
        if (mCamera != null) {
            mCamera.release();

            Log.e("Main activty", "Destroy called;");
        }


    }

}