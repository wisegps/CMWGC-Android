package com.wgc.cmwgc.Until;
/**
 * 公共类
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

public class GetSystem {
	private static String TAG = "GetSystem";
	/**
	 * M5D加密
	 * @param string
	 * @return m5d
	 */
	public static String getM5DEndo(String s) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return s;
		}
		char[] charArray = s.toCharArray();
		byte[] byteArray = new byte[charArray.length];
		for (int i = 0; i < charArray.length; i++){
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16){
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}
	/**
	 * 经纬度格式转换,把服务器得到的string转成int类型
	 * @param string 116.000000
	 * @return 116000000
	 */
	public static int StringToInt(String str) {
		try {
			Double point_doub = Double.parseDouble(str);
			return (int) (point_doub * 1000000);
		} catch (NumberFormatException e)
		{
			Log.d(TAG, "经纬度格式转换异常：NumberFormatException");
			return 0;
		}
	}
	
	/**
	 * 修改时间格式,加上8小时时区
	 * @param str yyyy-mm-ddThh:mm:ssz0000
	 * @param witch 0，返回时间,1返回日期,2返回字符串
	 * @return yyyy-mm-dd hh:mm:ss ，yyyy-mm-dd ， mm-dd hh:mm
	 */
	public static String ChangeTime(String str,int witch){
		String date="";
		int aa=str.length();
		if(str.length()<1||str.equals("null")){
			
		}else{
			
		
		date = str.substring(0, str.length() - 5).replace("T", " ");
		Calendar calendar = Calendar.getInstance();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.Date begin = sdf.parse(date);			
			calendar.setTime(begin);
			calendar.add(Calendar.HOUR_OF_DAY, 8);
			date = sdf.format(calendar.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
		if(witch == 0){
			return date;
		}else if(witch == 1){
			return date.substring(0, 10);
		}else{
			return date.substring(5, 16);
		}
		
	}
	/**
	 * 日期改成时间,年检提醒等显示的是时日,上传的时候要转成日期格式
	 * @param Data xxxx-xx-xx
	 * @return xxxx-xx-xx 00:00:00
	 */
	public static String CreateTime(String Data){
		if(Data.equals("")){
			return "";
		}
		return Data + " 00:00:00";
	}
	/**
	 * 获取当前系统时间
	 * @return yyyy-mm-dd hh:mm:ss
	 */
	public static String GetNowTime() {
		Time time = new Time();
		time.setToNow();
		String year = ChangeTime(time.year);
		String month = ChangeTime(time.month + 1);
		String day = ChangeTime(time.monthDay);
		String minute = ChangeTime(time.minute);
		String hour = ChangeTime(time.hour);
		String sec = ChangeTime(time.second);
		String str = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + sec;
		return str;
	}
	/**
	 * 获取当前系统时间前一个月的时间
	 * @return yyyy-mm-dd hh:mm:ss
	 */
	public static String GetBeforeTime() {
		Time time = new Time();
		time.setToNow();
		String year = ChangeTime(time.year);
		String month = ChangeTime(time.month+1);
		if(month.contains("01")){
			year=String.valueOf(Integer.parseInt(year)-1);
			month="12";
		}else{
			month="0"+(Integer.parseInt(month)-1);
		}
		String day = ChangeTime(time.monthDay);		
		String str = year + "-" + month + "-" + day;
		return str;
	}
	
	/**
	 * 调整时间格式
	 * @param 9
	 * @return 09
	 */
	public static String ChangeTime(int i) {
		String str = null;
		if (i < 10) {
			str = "0" + i;
		} else {
			str = "" + i;
		}
		return str;
	}
	/**
	 * 地图标记旋转
	 * @param context 句柄
	 * @param direct 旋转角度
	 * @param ResourceId 图片资源
	 * @return 旋转后的图片
	 */
	public static BitmapDrawable GetDrawable(Context context,int direct,int ResourceId){
		Bitmap bitmapOrg = BitmapFactory.decodeResource(context.getResources(),ResourceId);
		// 获取这个图片的宽和高
		int width = bitmapOrg.getWidth();
		int height = bitmapOrg.getHeight();
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 缩放图片动作
		matrix.postScale(1, 1);
		// 旋转图片 动作
		matrix.postRotate(direct, (float) width / 2, (float) height / 2);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width,height, matrix, true);
		// 将上面创建的Bitmap转换成Drawable对象
		BitmapDrawable bmd = new BitmapDrawable(context.getResources(), resizedBitmap);
		return bmd;
	}
	
    /**
     * 返回离当前时间的时间差（分钟）
     * @param 计算离当前的时间差
     * @return 返回分钟
     */
    public static long GetTimeDiff(String time){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			java.util.Date begin = sdf.parse(time);
			java.util.Date end = sdf.parse(GetNowTime());
			long between = (end.getTime() - begin.getTime())/(1000*60);
			return between;
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * 离线时间显示
	 * @param 多少分钟
	 * @return 对应的时间
	 */
	public static String ShowOfflineTime(long time){
		//System.out.println("time=" + time);
		String str = null;
		if(time > 1440)
		{//小时
			long hours = time/60;
			str = hours/24 + "天" +hours%24 + "小时";
		}
		else if(time >= 60)
		{
			str = time/60 +"小时";
		}
		else
		{
			str = time + "分钟";
		}
		return str;
	}
	/**
	 * 获取版本信息，判断时候有更新
	 * @param context
	 * @param 包名称
	 * @return versionName，版本名称，如1.2
	 */
	public static String GetVersion(Context context,String packString) {
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
	 * 判断网络是否连接。
	 * @param context
	 * @return result true表示有连接，false表示无连接
	 */
	public static boolean checkNetWorkStatus(Context context){
        boolean result;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();
        if (netinfo != null && netinfo.isConnected()) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }
	/**
	 * 获取MAC地址
	 * @param context
	 * @return
	 */
	public static String getMacAddress(Context context){
    	try {
			//获取MAC地址
			WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);		 
			WifiInfo info = wifi.getConnectionInfo();
			String wifiAddress = info.getMacAddress();
			if(wifiAddress != null){
				return wifiAddress;
			}
			String Imei = ((TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE)).getDeviceId();
			if(Imei != null){
				return Imei;
			}
			String BluetoothAddress = BluetoothAdapter.getDefaultAdapter().getAddress();
			return BluetoothAddress;
		} catch (Exception e){
			Log.d(TAG, "获取MAC地址异常");
			return "";
		}
    }
	
}