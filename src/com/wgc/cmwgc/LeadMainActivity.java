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
 * �����ж��ǽ�����ҳ���ǽ���ʱ��ҳ���ж��ն��Ƿ���⣬û���Ҫ���
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
		
		Log.e(TAG, "�豸�ţ� " + Config.con_serial);
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
	}
	
	/**
	 * ��ʼ���豸
	 */
	private void initDevice(){
		mContext = LeadMainActivity.this;
		TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);	
		Config.con_serial = tm.getDeviceId();	 //�ֻ�����:GSM�ֻ��� IMEI �� CDMA�ֻ��� MEID. 
		
		isUpdate();	
	}
	
	
	/**
	 * ��ʼ��Wistorm
	 */
	private void initWistorm(){
		BaseVolley.init(LeadMainActivity.this);
		userApi = new WUserApi(LeadMainActivity.this);
		deviceApi = new WDeviceApi(LeadMainActivity.this);
	}
	
	/**
	 * ��ȡtoken
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
     * ���fragment,��������ҳ
     */
    private void setfragment(){
		FragmentManager manager=getFragmentManager();
		FragmentTransaction tran=manager.beginTransaction();
		LeadFragment content = LeadFragment.newInstance("");
		tran.replace(R.id.id_content, content);
		tran.commit();
	}
    
	/**
	 * ����������ʾ����ҳ������ҳ
	 */
	private void checkIsCreate(){	
		boolean isCreate = pref.getBoolean(Config.IS_CREATE, false);
		Log.e(TAG, "����豸�Ƿ��Ѿ������� " + isCreate);
		if(!isCreate){//���û�д����ʹ���
			getToken();
			
//			isCreate();			
		}else{
			if(!ClsUtils.isWorked(this,"com.wgc.cmwgc.HttpService")){//�����ڿ�������
				Log.e(TAG, "��������------------");
				Intent intent_service =new Intent(LeadMainActivity.this,HttpService.class);
				startService(intent_service);
			}
		}
	}
	
	/**
	 * ����̨�Ƿ��Ѿ�����������豸
	 */
	private void isCreate(){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("access_token", Config.ACCESS_TOKEN);
		params.put("serial", Config.con_serial);
		deviceApi.get(params, "", new OnSuccess() {
			
			@Override
			protected void onSuccess(String response) {
				// TODO Auto-generated method stub
				Log.e(TAG, "��ȡ�豸������Ϣ isCreate ��    " + response);
				try {
					JSONObject jsonObject = new JSONObject(response);
					if(jsonObject.has("status_code")){
						if("3".equals(jsonObject.getString("status_code"))){
							Log.e(TAG, "���豸û���ں�̨�����������ȴ����豸  ");
							createDevice();
						}
					}else if(response.contains("device_id")){
						Log.e(TAG, "��������-isCreate-----------");
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
	 * �����豸
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
						Toast.makeText(mContext, "�豸�󶨳ɹ���", Toast.LENGTH_LONG).show();
						if(!ClsUtils.isWorked(LeadMainActivity.this,"com.wgc.cmwgc.HttpService")){//�����ڿ�������
							Log.e(TAG, "��������------------");
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
				Log.e(TAG, "���صĸ�����Ϣ------------" + msg.obj.toString());
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
	 * �Ƿ��ȡ������Ϣ,�����sd�������
	 */
 	private void isUpdate() {
		if (ClsUtils.isSdCardExist()) {
			new NetThread.GetDataThread(hanlder, Config.UPDATA_APK_URL, UPDATA_APK).start();
		}
	}
 	
	/**
	 * ��ȡ���½ӿ���Ϣ
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
				// ����
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
	 * �ù㲥���ڼ�����Ļ�Ŀ���
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
