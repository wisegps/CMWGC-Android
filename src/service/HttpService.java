package service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.android.volley.VolleyError;
import com.wgc.cmwgc.HttpHelper;
import com.wgc.cmwgc.LeadMainActivity;
import com.wgc.cmwgc.Until.ClsUtils;
import com.wgc.cmwgc.Until.Config;
import com.wicare.wistorm.api.WDeviceApi;
import com.wicare.wistorm.api.WUserApi;
import com.wicare.wistorm.http.BaseVolley;
import com.wicare.wistorm.http.OnFailure;
import com.wicare.wistorm.http.OnSuccess;
import com.wicare.wistorm.toolkit.WiStormApi;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
/**
 * 该服务主要用于上传经纬度和判断是否有版本更新
 */
public class HttpService extends Service {
	
	private AMapLocationClient mLocationClient = null;//高德定位
	private int TEN_MINUTES = 1000 * 30 ;// 30s
    private Boolean isFirst = true;
    private String latt;
    private String lonn;
    private Thread myThread;
    private WDeviceApi deviceApi;
    private WUserApi userApi;
	private static final String TAG = "HttpService";
	
	private Handler objHandler = new Handler();
	
	
	/**
	 * 定时任务
	 */
	private Runnable mTasks = new Runnable(){
		public void run(){
			Log.i("WWWWWWWW", "30s定时任务..............." + Config.ACCESS_TOKEN);
			uploadLocation();
			objHandler.postDelayed(mTasks, TEN_MINUTES);
		}
	};
	
	
	

	@Override
	public void onCreate() {
		super.onCreate();		
		//对屏幕的状态进行广播设置
		initWistorm();
		Log.d("WWWWWWWW", "服务：  onCreate（）");
		getToken();
		TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);	
		Config.con_serial = tm.getDeviceId();	 //手机串号:GSM手机的 IMEI 和 CDMA手机的 MEID. 
		
		IntentFilter filter = new IntentFilter();  	   
	    filter.addAction(Intent.ACTION_SCREEN_ON);
	    filter.addAction(Intent.ACTION_SCREEN_OFF); 
	    filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
	    registerReceiver(receiver, filter); 
		initLocation();
	}
	
	
	/**
	 * 初始化Wistorm
	 */
	private void initWistorm(){
		BaseVolley.init(this);
		deviceApi = new WDeviceApi(this);
		userApi = new WUserApi(this);
	}
	
	
	/**
	 * 创建历史定位数据
	 */
	private void uploadLocation(){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("access_token", Config.ACCESS_TOKEN);
		params.put("serial", Config.con_serial);
		params.put("lat", latt);//纬度
		params.put("lon", lonn);//经度
		params.put("rcv_time", ClsUtils.getCurrentTime());//当前时间
		params.put("gps_time", Config.gps_time);//定位时间
		
		Log.d("WWWWWWWW", Config.con_serial +  "定位时间 ： " + Config.gps_time + "当前时间: " + ClsUtils.getCurrentTime());
		
		deviceApi.gpsCreate(params, "", new OnSuccess() {
			
			@Override
			protected void onSuccess(String response) {
				// TODO Auto-generated method stub
				Log.e("WWWWWWWW", response);
//				{"status_code":0,"id":1}
			}
		}, new OnFailure() {
			
			@Override
			protected void onFailure(VolleyError error) {
				// TODO Auto-generated method stub
				Log.e("WWWWWWWW", "上传失败");
			}
		});
		
	}
	/**
	 * 获取token
	 */
	private void getToken(){
		userApi.getToken(Config.USER_NAME, Config.USER_PASS,"2", new OnSuccess() {
			
			@Override
			protected void onSuccess(String response) {
				// TODO Auto-generated method stub
				Log.d(TAG, response);
				try {
					JSONObject jsonObject = new JSONObject(response);
					if("0".equals(jsonObject.getString("status_code"))){
						Config.ACCESS_TOKEN = jsonObject.getString("access_token");
						Config.USER_ID = jsonObject.getString("user_id");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}	
			}
		} , new OnFailure() {
			
			@Override
			protected void onFailure(VolleyError error) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	/**
	 * 该广播用于监听屏幕的开关
	 */	
	private final BroadcastReceiver receiver = new BroadcastReceiver(){  
		  
	    @Override  
	    public void onReceive(final Context context, final Intent intent) {
	    	
	    	ConnectivityManager connectivityManager = (ConnectivityManager)
	    			context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			if(networkInfo != null && networkInfo.isAvailable()){
				getToken();
				Log.e("WWWWWWWW", "网络服务恢复");
				if(mLocationClient == null){
					isFirst = true;
					initLocation();
				}	
			}else{
				Log.e("WWWWWWWW", "没有网络服务");
				if(mLocationClient!= null){
	    			mLocationClient.stopLocation();
	    			mLocationClient = null;
	    			objHandler.removeCallbacks(mTasks);
	    		}
			}

	    	if(intent.getAction()==Intent.ACTION_SCREEN_ON){
	    		Log.d("WWWWWWWW", "屏幕  Intent.ACTION_SCREEN_ON");
	    		checkIsNew();
//		    	new Handler().postDelayed(new Runnable() {
//					
//					@Override
//					public void run() {	
//						checkIsNew();
//						if(mLocationClient == null){
//							isFirst = true;
//							initLocation();
//						}				
//					}
//				}, 2000);
	    	}else if(intent.getAction()==Intent.ACTION_SCREEN_OFF){
	    		
	    		Log.d("WWWWWWWW", "屏幕  Intent.ACTION_SCREEN_OFF");
//	    		if(mLocationClient!= null){
//	    			mLocationClient.stopLocation();
//	    			mLocationClient = null;
//	    			objHandler.removeCallbacks(mTasks);
//	    		}
	    	}	
	    }  
	};  

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e("WWWWWWWW", "服务：  onStartCommand");
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e("WWWWWWWW", "服务：  onDestroy");
		objHandler.removeCallbacks(mTasks);
		unregisterReceiver(receiver);
		if(mLocationClient!=null){
			mLocationClient.stopLocation();
		}
		if(myThread != null){
			 myThread.interrupt();
		}
		Intent service_again =new Intent(getApplicationContext(),HttpService.class);
		startService(service_again);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	/**
	 * 高德定位
	 */
	private void initLocation(){
		 //声明定位回调监听器
		AMapLocationListener mLocationListener = new AMapLocationListener() {
			
			@SuppressLint("SimpleDateFormat") @Override
			public void onLocationChanged(AMapLocation amapLocation) {
				if (amapLocation != null) {
			        if (amapLocation.getErrorCode() == 0) {
				        //定位成功回调信息，设置相关消息			       
				        double lat=amapLocation.getLatitude();//获取纬度
				        double lon=amapLocation.getLongitude();//获取经度	
				        latt = String.valueOf(lat);
				        lonn = String.valueOf(lon);
				        Config.location_address = amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。		
				        Log.e("WWWWWWWW", "定位： " +  latt + "--- " + lonn + "地址： " + amapLocation.getAddress()); 
				        Config.gps_time = ClsUtils.getCurrentTime();//定位时间,就是当前时间
				        if(isFirst){
				        	isFirst = false;
				        	objHandler.removeCallbacks(mTasks);
							objHandler.postDelayed(mTasks, 1000);
				        } 
			        }
			    }
			}
		};
		//初始化定位
		mLocationClient = new AMapLocationClient(getApplicationContext());
		//设置定位回调监听
		mLocationClient.setLocationListener(mLocationListener);	
		//声明mLocationOption对象
		AMapLocationClientOption mLocationOption = null;
		//初始化定位参数
		mLocationOption = new AMapLocationClientOption();
		//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
		mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
		//设置是否返回地址信息（默认返回地址信息）
		mLocationOption.setNeedAddress(true);
		//设置是否只定位一次,默认为false
		mLocationOption.setOnceLocation(false);
		//设置是否强制刷新WIFI，默认为强制刷新
		mLocationOption.setWifiActiveScan(true);
		//设置是否允许模拟位置,默认为false，不允许模拟位置
		mLocationOption.setMockEnable(false);
		//设置定位间隔,单位毫秒,默认为2000ms
		mLocationOption.setInterval(30000); //30s
		//给定位客户端对象设置定位参数
		mLocationClient.setLocationOption(mLocationOption);
		//启动定位
		mLocationClient.startLocation();
	}
    
    
	/**
	 * 用于判断是否有版本更新
	 */
	private Runnable myRunnable = new Runnable() {
		
		@Override
		public void run() {			
			HttpURLConnection httpURLConnection = getHttpURLConnection(Config.UPDATA_APK_URL);
			InputStream input = null;
			try {
				httpURLConnection.connect();				
				if(httpURLConnection.getResponseCode() == HttpStatus.SC_OK){
					//read data
					input = httpURLConnection.getInputStream();
					input = new BufferedInputStream(input);
					byte[] data = new byte[1024];
					while(input.read(data) != -1);
					input.close();					
					JSONObject mJSONObject = new JSONObject(new String(data));
					 Log.e("WWWWWWWW", "检查更新 ：  " + mJSONObject.toString()); 
					double latestVersion = mJSONObject.getDouble("version");
					double nativeVersion = Double.valueOf(ClsUtils.getVersion(getApplicationContext(), getPackageName()));
					if (latestVersion > nativeVersion) {
						startIntent();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "myRunnable IOException:"+e.getMessage());
			} catch (JSONException e) {
				e.printStackTrace();
				Log.e(TAG, "myRunnable JSONException:"+e.getMessage());
			} finally {
				if(input != null){
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	};
	
	
	/**
	 * @param url
	 * @return
	 */
	private HttpURLConnection getHttpURLConnection(String url) {
		URL mURL = null;
		HttpURLConnection mHttpURLConnection = null;
		try {
			mURL = new URL(url);
			mHttpURLConnection = (HttpURLConnection) mURL.openConnection();
			mHttpURLConnection.setConnectTimeout(HttpHelper.CONNECT_MAX_TIMEOUT);
			mHttpURLConnection.setReadTimeout(HttpHelper.CONNECT_MAX_TIMEOUT);
		} catch (MalformedURLException e) {
			Log.e(TAG,"getHttpURLConnection MalformedURLException:"+ e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, "getHttpURLConnection IOException:" + e.getMessage());
		}
		return mHttpURLConnection;
	}
    
	/**
	 * 
	 */
	private void startIntent(){
		Intent mIntent = new Intent();
		mIntent.setClass(getApplicationContext(), LeadMainActivity.class);
		mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(mIntent);
	}
	
	
	/**
	 * 检查更新
	 */
	private void checkIsNew(){
		myThread = new Thread(myRunnable);
		myThread.start();
	}	
	
}
