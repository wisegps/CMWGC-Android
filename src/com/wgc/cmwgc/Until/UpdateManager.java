package com.wgc.cmwgc.Until;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;

import com.wgc.cmwgc.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class UpdateManager {
	private final String TAG = "UpdateManager";
	private Context mContext;
	private String mUrl;
	// 提示�?
	private String updateMsg;
	private Dialog noticeDialog;
	private Dialog downloadDialog;
	/* 下载包安装路�?*/
	private static final String savePath = Environment
			.getExternalStorageDirectory().getPath() + "/updatedemo/";
	private static final String saveFileName = savePath + "UpdateDemoRelease.apk";

	/* 进度条与通知ui刷新的handler和msg常量 */
	private ProgressBar mProgress;
	private static final int DOWN_UPDATE = 1;
	private static final int DOWN_OVER = 2;
	private int progress;
	private Thread downLoadThread;
	private boolean interceptFlag = false;
	private double version;

	
	/**
	 * handler
	 */
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				// tv_record.setText("更新�?+ (i++));
				break;
			case DOWN_OVER:
				installApk();
				break;
			}
		};
	};

	public UpdateManager(Context context, String url, String updateMsg,
			double version, Handler handler) {
		this.mContext = context;
		this.mUrl = url;
		this.updateMsg = updateMsg;
		this.version = version;
//		this.leadActivityHandler = handler;
	}


	/**
	 * checkUpdateInfo
	 */
	public void checkUpdateInfo() {
		showNoticeDialog();
	}

	@SuppressLint("NewApi")
	private void showNoticeDialog() {
		String log = "";
		try {
			JSONArray jsonArray = new JSONArray(updateMsg);
			for (int i = 0; i < jsonArray.length(); i++) {
				double logVersion = Double.valueOf(jsonArray.getJSONObject(i)
						.getString("version"));
				if (logVersion > version) {
					Log.d(TAG, jsonArray.getJSONObject(i).getString("log"));
					
					log = jsonArray.getJSONObject(i).getString("log").replaceAll("\\\\r\\\\n", "");//替换\r\n
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		AlertDialog.Builder builder = new Builder(mContext,AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle("软件版本更新");
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.down_note, null);
		TextView tv_down_log = (TextView) v.findViewById(R.id.tv_down_log);
		tv_down_log.setText(log);
		builder.setView(v);
		builder.setPositiveButton("下载", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showDownloadDialog();
			}
		});
		builder.setNegativeButton("以后再说", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		noticeDialog = builder.create();
		noticeDialog.show();
	}

	/**
	 * 下载进度提示框
	 */
	private void showDownloadDialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("软件版本更新");
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.progress_update, null);
		mProgress = (ProgressBar) v.findViewById(R.id.progress);
		TextView tv_record = (TextView) v.findViewById(R.id.tv_record);
		builder.setView(v);
		builder.setNegativeButton("取消", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				interceptFlag = true;
			}
		});
		downloadDialog = builder.create();
		downloadDialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消�? 
		downloadDialog.show();
		downloadApk();
	}

	/**
	 * 下载线程
	 */
	private Runnable mdownApkRunnable = new Runnable() {
		public void run() {
			try {
				URL url = new URL(mUrl);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();

				File file = new File(savePath);
				if (!file.exists()) {
					file.mkdir();
				}
				String apkFile = saveFileName;
				File ApkFile = new File(apkFile);
				FileOutputStream fos = new FileOutputStream(ApkFile);

				int count = 0;
				byte buf[] = new byte[1024];

				do {
					int numread = is.read(buf);
					count += numread;
					progress = (int) (((float) count / length) * 100);
					// 更新进度
					mHandler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0) {
						// 下载完成通知安装
						mHandler.sendEmptyMessage(DOWN_OVER);
						break;
					}
					fos.write(buf, 0, numread);
				} while (!interceptFlag);// 点击取消就停止下�?

				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	};

	/**
	 * 下载apk
	 * 
	 * @param url
	 */

	private void downloadApk() {
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}

	/**
	 * 安装apk
	 * 
	 * @param url
	 */
	private void installApk() {
		File apkfile = new File(saveFileName);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);

	}
}
