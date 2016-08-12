package com.wgc.cmwgc.Until;

import java.util.ArrayList;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;

public class TtsServiceManager {
	
	public static final String PACKAGE_NAME = "com.rmt.speech.helper";
	public static final String SERVICE_NAME = "com.rmt.speech.helper.service.TtsService";
	
	public static void start(Context context) {
		if (isRunning(context)) {
			return;
		}
		Intent intent = new Intent();
		intent.setClassName(PACKAGE_NAME, SERVICE_NAME);
		context.startService(intent);
	}
	
	public static boolean isRunning(Context context) {
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager.getRunningServices(100);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString().equals(SERVICE_NAME)) {
                return true;
            }
        }
        return false;
    }
}
