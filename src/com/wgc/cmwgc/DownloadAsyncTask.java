package com.wgc.cmwgc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpStatus;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadAsyncTask extends AsyncTask<String, Integer, Integer> {

	private static final String TAG = "DownloadAsyncTask";

	private HttpURLConnection mHttpURLConnection;

	private static final int FLAG_DEFAULT = 1;
	private static final int FLAG_SUCCESS = 2;
	private static final int FLAG_FAIL = 3;
	private int flag = FLAG_DEFAULT;
	
	private Context mContext;
	private String mFileName;
	private int mVersion;
	private String mKey;

	public DownloadAsyncTask(Context context, String key, String fileName, int version){
		mContext = context.getApplicationContext();
		mKey = key;
		mFileName = fileName;
		mVersion = version;
	}
	

	private HttpURLConnection getHttpURLConnection(String url) {

		URL mURL = null;
		try {
			mURL = new URL(HttpHelper.getDownloadString(url));
			mHttpURLConnection = (HttpURLConnection) mURL.openConnection();
			mHttpURLConnection
					.setConnectTimeout(HttpHelper.CONNECT_MAX_TIMEOUT);
			mHttpURLConnection.setReadTimeout(HttpHelper.CONNECT_MAX_TIMEOUT);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			Log.e(TAG,
					"getHttpURLConnection MalformedURLException:"
							+ e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "getHttpURLConnection IOException:" + e.getMessage());
		}
		return mHttpURLConnection;
	}

	private void writeBuffer(InputStream input, String filename) {
		File mFile = new File(HttpHelper.getFilePath(filename));
		try {
			if (!mFile.isFile()) {
				Log.e(TAG, "delete");
//				mFile.delete();
				mFile.createNewFile();
			}
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		OutputStream output = null;
		try {
			output = new FileOutputStream(mFile);
			output = new BufferedOutputStream(output);
			byte[] data = new byte[1024];
			int value;
			while ((value = input.read(data)) != -1) {
				output.write(data, 0, value);
			}
			output.flush();
			output.close();
			flag = FLAG_SUCCESS;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			flag = FLAG_FAIL;
			Log.e(TAG, "writeBuffer FileNotFoundException:" + e.getMessage());
		} catch (IOException e) {
			flag = FLAG_FAIL;
			e.printStackTrace();
			Log.e(TAG, "writeBuffer IOException:" + e.getMessage());
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					flag = FLAG_FAIL;
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected Integer doInBackground(String... arg0) {
		if (HttpHelper.TEST_DEBUG){
			Log.e(TAG, HttpHelper.getDownloadString(mFileName));
		}
			
		HttpURLConnection httpURLConnection = getHttpURLConnection(mFileName);
		InputStream input = null;
		
		try {
			httpURLConnection.connect();
			if (httpURLConnection.getResponseCode() == HttpStatus.SC_OK) {
				input = httpURLConnection.getInputStream();
				input = new BufferedInputStream(input);
				writeBuffer(input, mFileName);
			}
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "doInBackground IOException:" + e.getMessage());
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return flag;
	}

	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		if (result == FLAG_SUCCESS) {
			// do something you want to do
			HttpHelper.setMediaValue(mContext, mKey, mFileName);
			HttpHelper.setVersionValue(mContext, mVersion);
		} else {
			HttpHelper.setVersionValue(mContext, HttpHelper.VERSION_DEFAULT);
		}
	}
}
