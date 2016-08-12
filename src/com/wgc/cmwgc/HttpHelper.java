package com.wgc.cmwgc;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;




import com.wgc.cmwgc.Until.TtsServiceManager;
import com.wgc.cmwgc.Until.WakeUpServiceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class HttpHelper {

	private static final String TAG = "HttpHelper";

	public static final boolean TEST_DEBUG = false;// test_debug

	private static String mImei;
	public static final String GlobalName = "Global";
	public static final int CONNECT_MAX_TIMEOUT = 10 * 1000;
	public static final int DEFALUT_VALUE = 2;
	public static final int SUCCESS_VALUE = 1;

	private static final String URL_HOST = "http://m.qqw16888.com/api";
	private static final String DOWNLOAD_PATH = "/images/";
	private static final String AUTH_CODE = "auth_code=bba2204bcd4c1f87a19ef792f1f68404";
	private static final String CHECK_PATH = "/wx_info/getWxMsgPush/";
	private static final String DEAL_PATH = "/dealer/";
	private static final String DEAL_PATH_KEY = "/GetDeviceSeverTime";
	private static final String TEST_IMEI = "459432803656447";// deal
																// 459432803656447
																// update
																// 459432803656447
	private static String mUrlString;
	private static String mUrlDealString;

	public static String VERSION_FILE = "ver.json";
	// private static String root_path =
	// Environment.getExternalStorageDirectory().getPath();
	private static String root_path = "/storage/sdcard0";
	public static String dir_path = root_path + "/" + GlobalName;
	// private static String dir_path = root_path;

	public static final int DEAL_DEFALUT_VALUE = 0;

	private static SharedPreferences mSharedPreference;
	private static SharedPreferences mSharedPreferenceDeal;

	public static final String MEDIA_PIC_ONE = "pic1_name";
	public static final String MEDIA_PIC_TWO = "pic2_name";
	public static final String MEDIA_PIC_THREE = "pic3_name";
	public static final String MEDIA_PIC_FOUR = "pic4_name";

	private static final String MEDIA_PIC_ONE_DEFAULT = "pic1.png";
	private static final String MEDIA_PIC_TWO_DEFAULT = "pic2.png";
	private static final String MEDIA_PIC_THREE_DEFAULT = "pic3.png";
	private static final String MEDIA_PIC_FOUR_DEFAULT = "pic4.png";

	public static final String MEDIA_INFO = "media_name";
	private static final String MEDIA_DEFAULT = "bootmusic.mp3";

	private static final String VERSION_INFO = "version_info";
	private static final String DEAL_INFO = "deal_info";
	public static final int VERSION_DEFAULT = 0;
	public static final String VERSION_KEY = "is_new";

	private static final String ACTION_HIDE_NAVIGATION = "com.rmt.action.HIDE_NAVIGATION";
	private static final String ACTION_SHOW_NAVI_BAR = "com.rmt.action.SHOW_NAVIGATION";

	public static void copyFile(Context context) {
		InputStream input = null;
		OutputStream output = null;
		//获取资源文件
		AssetManager mAssetManager = context.getAssets();
		try {
			String[] fileNames = mAssetManager.list("");
			for (int i = 0; i < fileNames.length; i++) {
				if (fileNames[i].equals("images"))
					continue;
				if (fileNames[i].equals("sounds"))
					continue;
				if (fileNames[i].equals("webkit"))
					continue;

				File mFile = new File(getFilePath(fileNames[i]));
				//判断是否是文件
				if (mFile.isFile())
					continue;
				//获取文件资源
				input = mAssetManager.open(fileNames[i]);
				
				output = new FileOutputStream(mFile);
				output = new BufferedOutputStream(output);
				byte[] buffer = new byte[1024];
				int value;
				while ((value = input.read(buffer)) != -1) {
					output.write(buffer, 0, value);
				}
				output.flush();
				input.close();
				output.close();
			}
			if (output != null)
				output.close();
			if (output != null)
				input.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}

	public static void sendBroadcastHideNaviBar(Context context) {
		Settings.System.putInt(context.getContentResolver(), "navi_bar_status", 0);
		Intent mHideNavigationBarIntent = new Intent(ACTION_HIDE_NAVIGATION);
		context.sendBroadcast(mHideNavigationBarIntent);
	}

	public static void sendBroadcastShowNaviBar(Context context) {
		Settings.System.putInt(context.getContentResolver(), "navi_bar_status", 1);
		Intent mShowNaviBarIntent = new Intent(ACTION_SHOW_NAVI_BAR);
		context.sendBroadcast(mShowNaviBarIntent);
	}

	public static void startOffAllMsg(Context context) {
		Intent mOffIntent = new Intent("com.rmt.action.KILL_PACKAGE");
		mOffIntent.putExtra("kill_package_name", "com.rmt.speech.helper");
		context.sendBroadcast(mOffIntent);
	}

	public static void startOnAllMsg(Context context) {
		WakeUpServiceManager.start(context);
		TtsServiceManager.start(context);
	}

	public static String getVerString(Context context) {
		if (mUrlString == null) {
			mUrlString = URL_HOST + getCheckPath() + getImei(context) + "?" + getSignCode();
		}
		return mUrlString;
	}

	public static String getDealString(Context context) {
		if (mUrlDealString == null) {
			mUrlDealString = URL_HOST + getDealPath() + getImei(context) + getDealPathKey() + "?" + getSignCode();
		}
		return mUrlDealString;
	}

	public static String getDownloadString(String content) {
		if (content == null)
			return null;
		return URL_HOST + getDlPath() + content;
	}

	public static void makeDir() {
		File mFile = new File(dir_path);
		if (!mFile.exists()) {
			mFile.mkdirs();
		}
	}

	public static String getFilePath(String name) {
		return dir_path + "/" + name;
	}

	public static void setMediaValue(Context context, String key, String value) {
		getSharedPreferences(context).edit().putString(key, value).commit();
	}

	public static String getImageOne(Context context) {
		//（key，value）
		return getSharedPreferences(context).getString(MEDIA_PIC_ONE, MEDIA_PIC_ONE_DEFAULT);
	}

	public static String getImageTwo(Context context) {
		return getSharedPreferences(context).getString(MEDIA_PIC_TWO, MEDIA_PIC_TWO_DEFAULT);
	}

	public static String getImageThree(Context context) {
		return getSharedPreferences(context).getString(MEDIA_PIC_THREE, MEDIA_PIC_THREE_DEFAULT);
	}

	public static String getImageFour(Context context) {
		return getSharedPreferences(context).getString(MEDIA_PIC_FOUR, MEDIA_PIC_FOUR_DEFAULT);
	}

	public static String getMusicName(Context context) {
		return getSharedPreferences(context).getString(MEDIA_INFO, MEDIA_DEFAULT);
	}

	public static int getVersionValue(Context context) {
		return getSharedPreferences(context).getInt(VERSION_INFO, VERSION_DEFAULT);
	}

	public static void setVersionValue(Context context, int value) {
		getSharedPreferences(context).edit().putInt(VERSION_INFO, value).commit();
	}

	public static int switchTimeFormat(String content) {
		int tIndex = content.indexOf("T");
		String key = "-";
		String dateTemp = content.substring(0, tIndex);
		while (dateTemp.contains(key)) {
			int index = dateTemp.indexOf(key);
			String startTemp = dateTemp.substring(0, index);
			String endTemp = dateTemp.substring(index + 1, dateTemp.length());
			dateTemp = startTemp + endTemp;
		}
		Log.v(TAG, "switchTimeFormat:" + dateTemp);
		int value = DEAL_DEFALUT_VALUE;
		try {
			value = Integer.valueOf(dateTemp);
		} catch (NumberFormatException e) {
			value = DEAL_DEFALUT_VALUE;
			Log.e(TAG, "switchTimeFormat fail:" + e.getMessage());
		}
		return (value + 1);
	}

	public static int getCurrentTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", new Locale("zh", "CN"));
		Log.v(TAG, "getCurrentTime:" + format);
		int value = DEAL_DEFALUT_VALUE;
		try {
			value = Integer.valueOf(format.format(new Date()));
		} catch (NumberFormatException e) {
			value = DEAL_DEFALUT_VALUE;
			Log.e(TAG, "getCurrentTime fail:" + e.getMessage());
		}
		return value;
	}

	private static SharedPreferences getSharedPreferences(Context context) {
		if (mSharedPreference == null) {
			mSharedPreference = context.getSharedPreferences(VERSION_INFO, Context.MODE_PRIVATE);
		}
		return mSharedPreference;
	}

	private static SharedPreferences getSharedPreferencesDeal(Context context) {
		if (mSharedPreferenceDeal == null) {
			mSharedPreferenceDeal = context.getSharedPreferences(DEAL_INFO, Context.MODE_PRIVATE);
		}
		return mSharedPreferenceDeal;
	}

	public static void setDealValueDeal(Context context, int value) {
		getSharedPreferencesDeal(context).edit().putInt(DEAL_INFO, value).commit();
	}

	public static int getDealValue(Context context) {
		return getSharedPreferencesDeal(context).getInt(DEAL_INFO, DEAL_DEFALUT_VALUE);
	}

	public static String getImei(Context context) {

		// mImei = TEST_IMEI;
		if (mImei != null) {
			return TEST_DEBUG ? TEST_IMEI : mImei;
		}
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		mImei = telephonyManager.getDeviceId();
		if (null == mImei) {
			return "";
		}
		return TEST_DEBUG ? TEST_IMEI : mImei;
	}

	private static String getCheckPath() {
		return CHECK_PATH;
	}

	private static String getDealPath() {
		return DEAL_PATH;
	}

	private static String getDealPathKey() {
		return DEAL_PATH_KEY;
	}

	private static String getDlPath() {
		return DOWNLOAD_PATH;
	}

	private static String getSignCode() {
		return AUTH_CODE;
	}

}
