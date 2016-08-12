package receiver;

import java.util.Timer;
import java.util.TimerTask;

import com.wgc.cmwgc.LeadMainActivity;
import com.wgc.cmwgc.Until.Config;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootUpReceiver extends BroadcastReceiver{

//	private SharedPreferences pref;	
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		/*pref = context.getSharedPreferences(Config.SP_NAME, Context.MODE_PRIVATE);
		boolean isCreate = pref.getBoolean(Config.IS_CREATE, false);
		if(isCreate){
			if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){ 
				turnMainActivity(context);
		    } 
		} */
		
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){ 
			turnMainActivity(context);
	    } 
	}
	
	
	
	/**
	 * @param context
	 */
	private void turnMainActivity(final Context context){
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Intent newIntent = new Intent(context, LeadMainActivity.class); 
			    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //注意，必须添加这个标记，否则启动会失败 
			    context.startActivity(newIntent); 
			}
		};
		timer.schedule(task, 12000);
		

	}

}
