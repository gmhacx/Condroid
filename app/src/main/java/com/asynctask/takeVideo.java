package com.asynctask;

import android.content.Intent;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.connect.MyApplication;
import com.connect.VideoView;
import com.utils.CommonUtils;

import java.io.UnsupportedEncodingException;

/**
 * Created by Conan0xff on 2017/12/5.
 */

public class takeVideo extends AsyncTask<String, Void, String> {
    String i = "0";
    String j = "10000";

    public takeVideo(String i, String j) {
        this.i = i;
        this.j = j;
    }

    @Override
    protected String doInBackground(String... params) {
        int numCameras = Camera.getNumberOfCameras();
        if (numCameras > Integer.parseInt(i)) {
            Intent intent = new Intent(MyApplication.getContext(), VideoView.class);
            intent.putExtra("Camera", i);
            intent.putExtra("Time", j);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MyApplication.getContext().startActivity(intent);
        }
        //NEED TO IMPLEMENT STREAMING
        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
            CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "Recording Video");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        try {
            Thread.sleep(Integer.parseInt(j));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        	PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit().putBoolean("Media",false).commit();
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
