package com.wgc.cmwgc.Fragment;

import java.util.ArrayList;

import com.google.zxing.WriterException;
import com.wgc.cmwgc.R;
import com.wgc.cmwgc.Adapter.ViewPagerAdapter;
import com.wgc.cmwgc.Until.Config;
import com.wgc.cmwgc.Until.EncodingHandler;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

/*
 * �����������ת������ҳ
 */
public class LeadFragment extends Fragment implements OnPageChangeListener ,OnClickListener{
	    // ����ViewPager����
		private ViewPager viewPager;
		// ����ViewPager������
		private ViewPagerAdapter vpAdapter;
		// ����һ��ArrayList�����View
		private ArrayList<View> views;
		// ����ͼƬ��Դ
		private static final int[] pics = { R.drawable.lead1,R.drawable.lead2,R.drawable.lead3,R.drawable.lead4};
		// �ײ�С���ͼƬ
		Button btn;
		TextView tv_back;
		private ImageView[] points;
		// ��¼��ǰѡ��λ��
		private int currentIndex;
		Context context;
		LinearLayout linearLayout;
		ImageView erweima,img_er;
		
		
		
		
//		public LeadFragment(Activity context){
//			this.context=context;
//		}
		
		public static LeadFragment newInstance(String key){
			LeadFragment fragment = new LeadFragment();
			Bundle bundle = new Bundle();
			bundle.putString("key", key);
			fragment.setArguments(bundle);
			return fragment;
		}
		
		
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View view =inflater.inflate(R.layout.leader_fragment1, container, false);
		context=getActivity();
		initView(view);		
		initData(inflater,view);
		setData();				
		return  view;
	}
	
	/**
	 * ��ʼ�����
	 */
	private void initView(View view) {
		// ʵ����ArrayList����
		views = new ArrayList<View>();
		// ʵ����ViewPager
		viewPager = (ViewPager)view.findViewById(R.id.viewpager);
		// ʵ����ViewPager������
		vpAdapter = new ViewPagerAdapter(views);				
	}
	
	
	/**
	 * ��ʼ������
	 */
	private void initData(LayoutInflater inflater,View view) {
		// ����һ�����ֲ����ò���
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);

		// ��ʼ������ͼƬ�б�
		for (int i = 0; i < pics.length; i++) {
			ImageView iv = new ImageView(context);
			iv.setLayoutParams(mParams);
			//��ֹͼƬ����������Ļ
			iv.setScaleType(ScaleType.FIT_XY);
			//����ͼƬ��Դ
			iv.setImageResource(pics[i]);
			views.add(iv);
		}		
       View v=inflater.inflate(R.layout.viewpage1, null);
       tv_back=(TextView)v.findViewById(R.id.tv_back);
       tv_back.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			getActivity().finish();			
		}
	});
       erweima=(ImageView)v.findViewById(R.id.erweima); 
       img_er=(ImageView)v.findViewById(R.id.img_er); 
		views.add(v);
		// ��������
		viewPager.setAdapter(vpAdapter);
		// ���ü���
		viewPager.setOnPageChangeListener(this);

		// ��ʼ���ײ�С��
		initPoint(view);
	}

	
	/**
	 * ��ʼ���ײ�С��
	 */
	private void initPoint(View view) {
		linearLayout = (LinearLayout)view.findViewById(R.id.ll);
		points = new ImageView[views.size()];
		// ѭ��ȡ��С��ͼƬ
		for (int i = 0; i < views.size(); i++) {
			// �õ�һ��LinearLayout�����ÿһ����Ԫ��
			points[i] = (ImageView) linearLayout.getChildAt(i);
			// Ĭ�϶���Ϊ��ɫ
			points[i].setEnabled(true);
			// ��ÿ��С�����ü���
			points[i].setOnClickListener(this);
			// ����λ��tag������ȡ���뵱ǰλ�ö�Ӧ
			points[i].setTag(i);
		}
		// ���õ���Ĭ�ϵ�λ��
		currentIndex = 0;
		// ����Ϊ��ɫ����ѡ��״̬
		points[currentIndex].setEnabled(false);
	}

	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub		
		// ���õײ�С��ѡ��״̬
				setCurDot(arg0);
				
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int position = (Integer) v.getTag();
		setCurView(position);
		setCurDot(position);
		
	}
	
	
	/**
	 * ���õ�ǰҳ���λ��
	 */
	private void setCurView(int position) {
		if (position < 0 || position >= views.size()) {
			return;
		}
		viewPager.setCurrentItem(position);	
	}
	
	
	/**
	 * ���õ�ǰ��С���λ��
	 */
	private void setCurDot(int positon) {
		if (positon < 0 || positon > views.size() - 1 || currentIndex == positon) {
			return;
		}
		points[positon].setEnabled(false);
		points[currentIndex].setEnabled(true);
        
		currentIndex = positon;
	}
	
	
	/**
	 * ���ɶ�ά��
	 */
	private void setData(){
		try {	
			 String contentString = Config.con_serial;  
			 if (!contentString.equals("")) {  
			     Bitmap qrCodeBitmap = EncodingHandler.createQRCode(contentString, 350);  
			     erweima.setImageBitmap(qrCodeBitmap); 
			 }
//			 //app���صĶ�ά��	
//			 String app_down = "http://wo365.net/";  
//			 if (!app_down.equals("")) {  
//			     Bitmap qrCodeBitmap = EncodingHandler.createQRCode(app_down, 350);  
//			     img_er.setImageBitmap(qrCodeBitmap);
//			 }
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
