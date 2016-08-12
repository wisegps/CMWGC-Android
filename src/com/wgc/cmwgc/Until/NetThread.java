package com.wgc.cmwgc.Until;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * ���̲߳�����  TODO httpGet���ع�
 * @author honesty
 */
public class NetThread {
	static String TAG = "NetThread";
	/**
	 * Get��ȡ����
	 * @author honesty
	 */
	public static class GetDataThread extends Thread{
		Handler handler;
		String url;
		int what;
		/**
		 * Get��ȡ����
		 * @param handler
		 * @param url
		 * @param what
		 */
		public GetDataThread(Handler handler,String url,int what){
			this.handler = handler;
			this.url = url;
			this.what =what;
		}
		@Override
		public void run() {
			super.run();
			try {
				Log.d(TAG, url);
				URL myURL = new URL(url);
				URLConnection httpsConn = (URLConnection) myURL.openConnection();
				if (httpsConn != null) {
					httpsConn.setConnectTimeout(20*1000);
					httpsConn.setReadTimeout(20*1000);
					InputStreamReader insr = new InputStreamReader(httpsConn.getInputStream(), "UTF-8");
					BufferedReader br = new BufferedReader(insr, 1024);
					String data = "";
					String line = "";
					while ((line = br.readLine()) != null) {
						data += line;
					}
					insr.close();
					Message message = new Message();
					message.what = what;
					message.obj = data;
					handler.sendMessage(message);
				}else{
					Log.d(TAG, "URLConnection");
					Message message = new Message();
					message.what = what;
					message.obj = "";
					handler.sendMessage(message);
				}
			}catch (SocketTimeoutException e) {
				e.printStackTrace();
				Message message = new Message();
				message.what = what;
				message.obj = "SocketTimeoutException";
				handler.sendMessage(message);
			}catch (SocketException e) {
				e.printStackTrace();
				Message message = new Message();
				message.what = what;
				message.obj = "SocketTimeoutException";
				handler.sendMessage(message);
			}
			catch (Exception e) {
				e.printStackTrace();
				Message message = new Message();
				message.what = what;
				message.obj = "";
				handler.sendMessage(message);
			}
		}
	}
	
	public static class putDataThread extends Thread{
		Handler handler;
		String url;
		int what;
		List<NameValuePair> parms;
		public putDataThread(Handler handler,String url,List<NameValuePair> parms,int what){
			this.handler = handler;
			this.url = url;
			this.what =what;
			this.parms = parms;
		}
		@Override
		public void run() {
			super.run();
			try {
				BasicHttpParams httpParams = new BasicHttpParams();  
			    HttpConnectionParams.setConnectionTimeout(httpParams, 10000);  
			    HttpConnectionParams.setSoTimeout(httpParams, 10000); 
				HttpClient client = new DefaultHttpClient(httpParams);
		        HttpPut httpPut = new HttpPut(url);	
		        if(parms != null){
		        	httpPut.setEntity(new UrlEncodedFormEntity(parms,HTTP.UTF_8));
		        }
		        HttpResponse response = client.execute(httpPut); 
		        if (response.getStatusLine().getStatusCode()  == 200){
		        	HttpEntity entity = response.getEntity();
					BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
					StringBuilder sb = new StringBuilder();
					String line = "";
					while ((line = reader.readLine()) != null) {
						sb.append(line);
					}
					System.out.println(sb.toString());
					Message message = new Message();
					message.what = what;
					message.obj = sb.toString();
					handler.sendMessage(message);
		        }else{
		        	Message message = new Message();
					message.what = what;
					message.obj = "";
					handler.sendMessage(message);
		        }
			} catch (Exception e) {
				e.printStackTrace();	
				Message message = new Message();
				message.what = what;
				message.obj = "";
				handler.sendMessage(message);
			}
		}
	}
	
	public static class DeleteThread extends Thread{
		Handler handler;
		String url;
		int what;		
		public DeleteThread(Handler handler,String url,int what){
			this.handler = handler;
			this.url = url;
			this.what =what;
		}
		@Override
		public void run() {
			super.run();
			try {
				HttpClient client = new DefaultHttpClient();
				HttpDelete httpDelete = new HttpDelete(url);
		        HttpResponse response = client.execute(httpDelete); 
		        if (response.getStatusLine().getStatusCode()  == 200){
		        	HttpEntity entity = response.getEntity();
					BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line);
					}
					Message message = new Message();
					message.what = what;
					message.obj = sb.toString();
					handler.sendMessage(message);
		        }else{
		        	Message message = new Message();
					message.what = what;
					message.obj = "";
					handler.sendMessage(message);
		        }
			} catch (Exception e) {
				e.printStackTrace();	
				Message message = new Message();
				message.what = what;
				message.obj = "";
				handler.sendMessage(message);
			}
		}
	}
	
	public static class postDataThread extends Thread{
		Handler handler;
		String url;
		int what;
		List<NameValuePair> params;
		public postDataThread(String url,List<NameValuePair> params){
			this.url = url;
			this.params = params;
		}
		public postDataThread(Handler handler,String url,List<NameValuePair> params,int what){
			this.handler = handler;
			this.url = url;
			this.what = what;
			this.params = params;
		}
		@Override
		public void run() {
			super.run();
			Log.d(TAG, url);
			HttpPost httpPost = new HttpPost(url);
			try {
				 httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				 HttpClient client = new DefaultHttpClient();
				 client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
				 client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
				 HttpResponse httpResponse = client.execute(httpPost);
				 if(httpResponse.getStatusLine().getStatusCode() == 200){
					 String strResult = EntityUtils.toString(httpResponse.getEntity());
					 if(handler != null){
						 Message message = new Message();
						 message.what = what;
						 message.obj = strResult;
						 handler.sendMessage(message);
					 }					 
				 }else{
					 int gg=httpResponse.getStatusLine().getStatusCode();
					 Log.d(TAG, "״̬" +httpResponse.getStatusLine().getStatusCode());
					
				 }
			} catch (Exception e) {
				if(handler != null){
					Message message = new Message();
					message.what = what;
					message.obj = "";
					handler.sendMessage(message);
				}				
				e.printStackTrace();
			}
		}
	}
	
	
	 /**
     * ��ָ�� URL ����POST����������
     * 
     * @param url
     *            ��������� URL
     * @param param
     *            ����������������Ӧ���� name1=value1&name2=value2 ����ʽ��
     * @return ������Զ����Դ����Ӧ���
     */
    public static String sendPost(String url) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // �򿪺�URL֮�������
            URLConnection conn = realUrl.openConnection();
            // ����ͨ�õ���������
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // ����POST�������������������
            conn.setDoOutput(true);
            conn.setDoInput(true);
          
            // ����BufferedReader����������ȡURL����Ӧ
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("���� POST ��������쳣��"+e);
            e.printStackTrace();
        }
        //ʹ��finally�����ر��������������
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    } 
    
}