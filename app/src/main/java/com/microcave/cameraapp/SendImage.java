package com.microcave.cameraapp;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;

import org.json.JSONException;
import org.json.JSONObject;


public class SendImage {
    public static void sendPost(String url, String imagePath) throws IOException, ClientProtocolException {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

            HttpPost httppost = new HttpPost(url);
            File file = new File(imagePath);
            Log.e("File path " , ""+file.toString());

            MultipartEntity mpEntity = new MultipartEntity();
            ContentBody cbFile = new FileBody(file, "image/jpeg");
            mpEntity.addPart("image", cbFile);

            httppost.setEntity(mpEntity);

            Log.e("executing request " + httppost.getRequestLine(), "");

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            Log.e("res.entity" , response.getStatusLine()+ "");

            if (resEntity != null) {
                String val=EntityUtils.toString(resEntity);


                JSONObject obj= new JSONObject(val);
                if(obj.getString("result").equals("ok"))
                {
                    Log.e("status", "successfully uploaded");
                    File f = new File(imagePath);
                    f.delete();
                    Log.e("status", "successfully Deleted"+imagePath+".jpg");
                }
            }
            if (resEntity != null) {
                resEntity.consumeContent();
            }

            httpclient.getConnectionManager().shutdown();
        }catch (HttpHostConnectException e)
        {
            Log.e("SendImage", e.getMessage());
        }catch (UnknownHostException e)
        {
            Log.e("Camera_app host error", e.getMessage());

            // this is handled when wifi or internet is unavailabe.
        }
        catch (JSONException e)
        {
            Log.e("JSon error", e.getMessage());

        }
        catch (Exception e)
        {
            Log.e("Camera app", e.getMessage());

        }


    }

    void Delete()
    {
//        try {
//            for (int i = 0; i < ImagePath.size(); i++) {
//
//                String image = ImagePath.get(i);
//                Log.d("delte Image path", image);
//                File f = new File(image);
//                f.delete();
//            }
//            Toast.makeText(this, "All Images are deleted ", Toast.LENGTH_SHORT).show();
//        }catch(Exception e)
//        {
//            e.getMessage();
//            Toast.makeText(this, "nothing to delte ", Toast.LENGTH_SHORT).show();
    }

    }

