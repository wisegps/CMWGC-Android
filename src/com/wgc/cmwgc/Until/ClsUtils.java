package com.wgc.cmwgc.Until;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

public class ClsUtils {
	
    
    
    public static void showToast(Context context,String content){
    	Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }
    
    
    public static ProgressDialog createDialog(Context context,String content){
    	ProgressDialog Dialog= new ProgressDialog(context);
		Dialog.setIndeterminate(true);
		Dialog.setMessage(content);
		Dialog.setTitle("提示");
		//这个设置true点击屏幕没反应但点击返回键有反应
		Dialog.setCancelable(true);
		Dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
		Dialog.show();
		return Dialog;
    }  
    public static Boolean isEmpty(Context context,EditText et,String content){
    	if(TextUtils.isEmpty(et.getText().toString())){
    		showToast(context, content);
    		return true;
    	}else{
    		return false;
    	}
    }
    
    /**
	 * 获取版本信息，判断时候有更新
	 * 
	 * @param context
	 * @param 包名称
	 * @return versionName，版本名称，如1.2
	 */
	public static String getVersion(Context context, String packString) {
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(packString, 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * 判断服务是否运行中
	 */
	public static boolean isWorked(Context context,String className) {
		ActivityManager myManager = (ActivityManager)context.getApplicationContext().getSystemService(
						Context.ACTIVITY_SERVICE);
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager
				.getRunningServices(40);
		
		for (int i = 0; i < runningService.size(); i++) {
			String aa = runningService.get(i).service.getClassName().toString();	
			Log.i("TAG", aa);
			if (runningService.get(i).service.getClassName().toString()
					.equals(className)) {				
				return true;
			}
		}
		return false;
	}
      
	
	/**
	 * 判断sd卡是否存在
	 * 
	 * @return
	 */
	public static boolean isSdCardExist() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}
	
	
	public static String getCurrentTime(){
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");       
		String date = sf.format(new Date());
		return date; 
	}
	
	
}
