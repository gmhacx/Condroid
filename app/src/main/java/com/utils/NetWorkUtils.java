package com.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

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
 * Created by Conan0xff on 2017/11/22.
 */

public class NetWorkUtils {

    //********************************************************************************************************************************************************
    //判断是否有网络连接
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
    //********************************************************************************************************************************************************
    //判断 WIFI 网络是否可用
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }
    //********************************************************************************************************************************************************
    //判断 MOBILE 网络是否可用
    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }
    //********************************************************************************************************************************************************
    public static InputStream getInputStreamFromUrl(Context context,String urlBase, String urlData) throws UnsupportedEncodingException {

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

        if (NetWorkUtils.isNetworkConnected(context)) {
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
}
