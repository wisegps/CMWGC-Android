package com.wgc.cmwgc;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import service.HttpService;

import com.android.volley.VolleyError;
import com.wgc.cmwgc.Fragment.LeadFragment;
import com.wgc.cmwgc.Until.ClsUtils;
import com.wgc.cmwgc.Until.Config;
import com.wgc.cmwgc.Until.NetThread;
import com.wgc.cmwgc.Until.UpdateManager;

import com.wicare.wistorm.api.WDeviceApi;
import com.wicare.wistorm.api.WUserApi;
import com.wicare.wistorm.http.BaseVolley;
import com.wicare.wistorm.http.OnFailure;
import com.wicare.wistorm.http.OnSuccess;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
/*
 * 用于判断是进引导页还是进临时主页，判断终端是否入库，没入库要入库
 */
import android.widget.Toast;

public class LeadMainActivity extends FragmentActivity {
	
	static final String TAG = "TEST_CMWGC";
	private Context mContext;
	private final int UPDATA_APK = 0;
	private SharedPreferences pref;
	private SharedPreferences.Editor editor;	
	private WUserApi userApi;
	private WDeviceApi deviceApi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);	
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.test_main);
		
		IntentFilter filter = new IntentFilter();  	
	    filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
	    registerReceiver(receiver, filter);

		setfragment();
		initDevice();
		pref = getSharedPreferences(Config.SP_NAME, Context.MODE_PRIVATE);
		editor = pref.edit();
		initWistorm();
		
		checkIsCreate();
		
		Log.e(TAG, "设备号： " + Config.con_serial);
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
	}
	
	/**
	 * 初始化设备
	 */
	private void initDevice(){
		mContext = LeadMainActivity.this;
		TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);	
		Config.con_serial = tm.getDeviceId();	 //手机串号:GSM手机的 IMEI 和 CDMA手机的 MEID. 
		
		isUpdate();	
	}
	
	
	/**
	 * 初始化Wistorm
	 */
	private void initWistorm(){
		BaseVolley.init(LeadMainActivity.this);
		userApi = new WUserApi(LeadMainActivity.this);
		deviceApi = new WDeviceApi(LeadMainActivity.this);
	}
	
	/**
	 * 获取token
	 */
	private void getToken(){
		userApi.getToken(Config.USER_NAME, Config.USER_PASS,"2", new OnSuccess() {
			
			@Override
			protected void onSuccess(String response) {
				// TODO Auto-generated method stub
				Log.e(TAG, response);
				try {
					JSONObject jsonObject = new JSONObject(response);
					if("0".equals(jsonObject.getString("status_code"))){
						Config.ACCESS_TOKEN = jsonObject.getString("access_token");
						Config.USER_ID = jsonObject.getString("user_id");
//						checkIsCreate();
						isCreate();	
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
     * 添加fragment,加载引导页
     */
    private void setfragment(){
		FragmentManager manager=getFragmentManager();
		FragmentTransaction tran=manager.beginTransaction();
		LeadFragment content = LeadFragment.newInstance("");
		tran.replace(R.id.id_content, content);
		tran.commit();
	}
    
	/**
	 * 根据数据显示引导页或者主页
	 */
	private void checkIsCreate(){	
		boolean isCreate = pref.getBoolean(Config.IS_CREATE, false);
		Log.e(TAG, "检查设备是否已经创建： " + isCreate);
		if(!isCreate){//如果没有创建就创建
			getToken();
			
//			isCreate();			
		}else{
			if(!ClsUtils.isWorked(this,"com.wgc.cmwgc.HttpService")){//服务不在开启服务
				Log.e(TAG, "开启服务------------");
				Intent intent_service =new Intent(LeadMainActivity.this,HttpService.class);
				startService(intent_service);
			}
		}
	}
	
	/**
	 * 检查后台是否已经创建了这个设备
	 */
	private void isCreate(){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("access_token", Config.ACCESS_TOKEN);
		params.put("serial", Config.con_serial);
		deviceApi.get(params, "", new OnSuccess() {
			
			@Override
			protected void onSuccess(String response) {
				// TODO Auto-generated method stub
				Log.e(TAG, "获取设备返回信息 isCreate ：    " + response);
				try {
					JSONObject jsonObject = new JSONObject(response);
					if(jsonObject.has("status_code")){
						if("3".equals(jsonObject.getString("status_code"))){
							Log.e(TAG, "该设备没有在后台创建，请你先创建设备  ");
							createDevice();
						}
					}else if(response.contains("device_id")){
						Log.e(TAG, "开启服务-isCreate-----------");
						editor.putBoolean(Config.IS_CREATE, true);
						editor.commit();
						Intent intent_service =new Intent(LeadMainActivity.this,HttpService.class);
						startService(intent_service);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}	
			}
		}, new OnFailure() {
			
			@Override
			protected void onFailure(VolleyError error) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	
	
	/**
	 * 创建设备
	 */
	private void createDevice(){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("access_token", Config.ACCESS_TOKEN);
		params.put("serial", Config.con_serial);
		params.put("dealer_id", Config.USER_ID);
		deviceApi.create(params, "", new OnSuccess() {
			
			@Override
			protected void onSuccess(String response) {
				// TODO Auto-generated method stub
				Log.e("TEST_WISTORM", response);
				try {
					JSONObject jsonObject = new JSONObject(response);
					if("0".equals(jsonObject.getString("status_code"))){
						editor.putBoolean(Config.IS_CREATE, true);
						editor.commit();
						Toast.makeText(mContext, "设备绑定成功！", Toast.LENGTH_LONG).show();
						if(!ClsUtils.isWorked(LeadMainActivity.this,"com.wgc.cmwgc.HttpService")){//服务不在开启服务
							Log.e(TAG, "开启服务------------");
							Intent intent_service =new Intent(LeadMainActivity.this,HttpService.class);
							startService(intent_service);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}	
			}
		}, new OnFailure() {
			
			@Override
			protected void onFailure(VolleyError error) {}
		});
	}
	
	
	/**
	 * handler
	 */
	@SuppressLint("HandlerLeak") 
	Handler hanlder=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				Log.e(TAG, "返回的更新信息------------" + msg.obj.toString());
				if (TextUtils.isEmpty(msg.obj.toString())) {				
					return;
				}
				if (msg.obj.toString().equals("SocketTimeoutException")) {				
					return;
				}
				UpdateData(msg);
				break;
			}
		};
	};
	
	/**
	 * 是否读取更新信息,如果有sd卡则更新
	 */
 	private void isUpdate() {
		if (ClsUtils.isSdCardExist()) {
			new NetThread.GetDataThread(hanlder, Config.UPDATA_APK_URL, UPDATA_APK).start();
		}
	}
 	
	/**
	 * 读取更新接口信息
	 * 
	 * @param msg
	 */
	private void UpdateData(Message msg) {
		try {
			JSONObject jsonObject = new JSONObject(msg.obj.toString());
			double latestVersion = jsonObject.getDouble("version");
			double nativeVersion = Double.valueOf(ClsUtils.getVersion(
					getApplicationContext(), getPackageName()));
			if (latestVersion > nativeVersion) {
				// 更新
				String downloadUrl = jsonObject.getString("app_path");
				String logs = jsonObject.getString("logs");
				UpdateManager mUpdateManager = new UpdateManager(
						mContext, downloadUrl, logs, nativeVersion,hanlder);
				mUpdateManager.checkUpdateInfo();
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
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
				checkIsCreate();
			}
	    }

	};  
	
	
	
	
	
}
