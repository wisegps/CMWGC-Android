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
 * �÷�����Ҫ�����ϴ���γ�Ⱥ��ж��Ƿ��а汾����
 */
public class HttpService extends Service {
	
	private AMapLocationClient mLocationClient = null;//�ߵ¶�λ
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
	 * ��ʱ����
	 */
	private Runnable mTasks = new Runnable(){
		public void run(){
			Log.i("WWWWWWWW", "30s��ʱ����..............." + Config.ACCESS_TOKEN);
			uploadLocation();
			objHandler.postDelayed(mTasks, TEN_MINUTES);
		}
	};
	
	
	

	@Override
	public void onCreate() {
		super.onCreate();		
		//����Ļ��״̬���й㲥����
		initWistorm();
		Log.d("WWWWWWWW", "����  onCreate����");
		getToken();
		TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);	
		Config.con_serial = tm.getDeviceId();	 //�ֻ�����:GSM�ֻ��� IMEI �� CDMA�ֻ��� MEID. 
		
		IntentFilter filter = new IntentFilter();  	   
	    filter.addAction(Intent.ACTION_SCREEN_ON);
	    filter.addAction(Intent.ACTION_SCREEN_OFF); 
	    filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
	    registerReceiver(receiver, filter); 
		initLocation();
	}
	
	
	/**
	 * ��ʼ��Wistorm
	 */
	private void initWistorm(){
		BaseVolley.init(this);
		deviceApi = new WDeviceApi(this);
		userApi = new WUserApi(this);
	}
	
	
	/**
	 * ������ʷ��λ����
	 */
	private void uploadLocation(){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("access_token", Config.ACCESS_TOKEN);
		params.put("serial", Config.con_serial);
		params.put("lat", latt);//γ��
		params.put("lon", lonn);//����
		params.put("rcv_time", ClsUtils.getCurrentTime());//��ǰʱ��
		params.put("gps_time", Config.gps_time);//��λʱ��
		
		Log.d("WWWWWWWW", Config.con_serial +  "��λʱ�� �� " + Config.gps_time + "��ǰʱ��: " + ClsUtils.getCurrentTime());
		
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
				Log.e("WWWWWWWW", "�ϴ�ʧ��");
			}
		});
		
	}
	/**
	 * ��ȡtoken
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
	 * �ù㲥���ڼ�����Ļ�Ŀ���
	 */	
	private final BroadcastReceiver receiver = new BroadcastReceiver(){  
		  
	    @Override  
	    public void onReceive(final Context context, final Intent intent) {
	    	
	    	ConnectivityManager connectivityManager = (ConnectivityManager)
	    			context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			if(networkInfo != null && networkInfo.isAvailable()){
				getToken();
				Log.e("WWWWWWWW", "�������ָ�");
				if(mLocationClient == null){
					isFirst = true;
					initLocation();
				}	
			}else{
				Log.e("WWWWWWWW", "û���������");
				if(mLocationClient!= null){
	    			mLocationClient.stopLocation();
	    			mLocationClient = null;
	    			objHandler.removeCallbacks(mTasks);
	    		}
			}

	    	if(intent.getAction()==Intent.ACTION_SCREEN_ON){
	    		Log.d("WWWWWWWW", "��Ļ  Intent.ACTION_SCREEN_ON");
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
	    		
	    		Log.d("WWWWWWWW", "��Ļ  Intent.ACTION_SCREEN_OFF");
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
		Log.e("WWWWWWWW", "����  onStartCommand");
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e("WWWWWWWW", "����  onDestroy");
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
	 * �ߵ¶�λ
	 */
	private void initLocation(){
		 //������λ�ص�������
		AMapLocationListener mLocationListener = new AMapLocationListener() {
			
			@SuppressLint("SimpleDateFormat") @Override
			public void onLocationChanged(AMapLocation amapLocation) {
				if (amapLocation != null) {
			        if (amapLocation.getErrorCode() == 0) {
				        //��λ�ɹ��ص���Ϣ�����������Ϣ			       
				        double lat=amapLocation.getLatitude();//��ȡγ��
				        double lon=amapLocation.getLongitude();//��ȡ����	
				        latt = String.valueOf(lat);
				        lonn = String.valueOf(lon);
				        Config.location_address = amapLocation.getAddress();//��ַ�����option������isNeedAddressΪfalse����û�д˽�������綨λ����л��е�ַ��Ϣ��GPS��λ�����ص�ַ��Ϣ��		
				        Log.e("WWWWWWWW", "��λ�� " +  latt + "--- " + lonn + "��ַ�� " + amapLocation.getAddress()); 
				        Config.gps_time = ClsUtils.getCurrentTime();//��λʱ��,���ǵ�ǰʱ��
				        if(isFirst){
				        	isFirst = false;
				        	objHandler.removeCallbacks(mTasks);
							objHandler.postDelayed(mTasks, 1000);
				        } 
			        }
			    }
			}
		};
		//��ʼ����λ
		mLocationClient = new AMapLocationClient(getApplicationContext());
		//���ö�λ�ص�����
		mLocationClient.setLocationListener(mLocationListener);	
		//����mLocationOption����
		AMapLocationClientOption mLocationOption = null;
		//��ʼ����λ����
		mLocationOption = new AMapLocationClientOption();
		//���ö�λģʽΪ�߾���ģʽ��Battery_SavingΪ�͹���ģʽ��Device_Sensors�ǽ��豸ģʽ
		mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
		//�����Ƿ񷵻ص�ַ��Ϣ��Ĭ�Ϸ��ص�ַ��Ϣ��
		mLocationOption.setNeedAddress(true);
		//�����Ƿ�ֻ��λһ��,Ĭ��Ϊfalse
		mLocationOption.setOnceLocation(false);
		//�����Ƿ�ǿ��ˢ��WIFI��Ĭ��Ϊǿ��ˢ��
		mLocationOption.setWifiActiveScan(true);
		//�����Ƿ�����ģ��λ��,Ĭ��Ϊfalse��������ģ��λ��
		mLocationOption.setMockEnable(false);
		//���ö�λ���,��λ����,Ĭ��Ϊ2000ms
		mLocationOption.setInterval(30000); //30s
		//����λ�ͻ��˶������ö�λ����
		mLocationClient.setLocationOption(mLocationOption);
		//������λ
		mLocationClient.startLocation();
	}
    
    
	/**
	 * �����ж��Ƿ��а汾����
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
					 Log.e("WWWWWWWW", "������ ��  " + mJSONObject.toString()); 
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
	 * ������
	 */
	private void checkIsNew(){
		myThread = new Thread(myRunnable);
		myThread.start();
	}	
	
}
