package com.utils;

import android.util.Log;

import com.connect.MyApplication;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Conan0xff on 2017/11/30.
 */

public class CommonUtils {
    //********************************************************************************************************************************************************
    public static InputStream getInputStreamFromUrl(String urlBase, String urlData) throws UnsupportedEncodingException {

        Log.i("com.connect", "base:" + urlBase);
        Log.i("com.connect", "data:" + urlData);

        String urlDataFormatted = urlData;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH:mm:ss");
        String currentDateandTime = "[" + sdf.format(new Date()) + "] - ";
        currentDateandTime = URLEncoder.encode(currentDateandTime, "UTF-8");

        if (urlData.length() > 1) {
            Log.d("com.connect", urlBase + urlData);

            urlData = currentDateandTime + URLEncoder.encode(urlData, "UTF-8");
            urlDataFormatted = urlData.replaceAll("\\.", "~period");

            Log.i("com.connect", urlBase + urlDataFormatted);
        }

        //if (NetWorkUtils.isNetworkConnected(getApplicationContext())) {
        if (NetWorkUtils.isNetworkConnected(MyApplication.getContext())) {
            InputStream content = null;
            try {
                Log.i("com.connect", "network push POST");
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(new HttpGet(urlBase + urlDataFormatted));
                content = response.getEntity().getContent();
                httpclient.getConnectionManager().shutdown();
            } catch (Exception e) {
                Log.e("com.connect", "exception", e);
            }
            return content;
        }
        return null;
    }
    //********************************************************************************************************************************************************
    public static String removeBlankSpace(StringBuilder sb) {
        int j = 0;
        for (int i = 0; i < sb.length(); i++) {
            if (!Character.isWhitespace(sb.charAt(i))) {
                sb.setCharAt(j++, sb.charAt(i));
            }
        }
        sb.delete(j, sb.length());
        return sb.toString();
    }
    //********************************************************************************************************************************************************
}
