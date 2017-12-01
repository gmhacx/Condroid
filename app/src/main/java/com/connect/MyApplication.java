package com.connect;

import android.app.Application;
import android.content.Context;

/**
 * Created by Conan0xff on 2017/11/30.
 */

public class MyApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate(){
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }
}
