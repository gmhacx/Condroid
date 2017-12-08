package com.connect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Conan0xff on 2017/12/8.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        Intent service = new Intent(context,XXXclass);
//        context.startService(service);
        context.startService(new Intent(MyApplication.getContext(), MyService.class));
        Log.v("TAG", "开机自动服务自动启动.....");
        //启动应用，参数为需要自动启动的应用的包名
//        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
//        context.startActivity(intent );
    }

}
