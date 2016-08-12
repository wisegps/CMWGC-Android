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
 * 立即体验后跳转的引导页
 */
public class LeadFragment extends Fragment implements OnPageChangeListener ,OnClickListener{
	    // 定义ViewPager对象
		private ViewPager viewPager;
		// 定义ViewPager适配器
		private ViewPagerAdapter vpAdapter;
		// 定义一个ArrayList来存放View
		private ArrayList<View> views;
		// 引导图片资源
		private static final int[] pics = { R.drawable.lead1,R.drawable.lead2,R.drawable.lead3,R.drawable.lead4};
		// 底部小点的图片
		Button btn;
		TextView tv_back;
		private ImageView[] points;
		// 记录当前选中位置
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
	 * 初始化组件
	 */
	private void initView(View view) {
		// 实例化ArrayList对象
		views = new ArrayList<View>();
		// 实例化ViewPager
		viewPager = (ViewPager)view.findViewById(R.id.viewpager);
		// 实例化ViewPager适配器
		vpAdapter = new ViewPagerAdapter(views);				
	}
	
	
	/**
	 * 初始化数据
	 */
	private void initData(LayoutInflater inflater,View view) {
		// 定义一个布局并设置参数
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);

		// 初始化引导图片列表
		for (int i = 0; i < pics.length; i++) {
			ImageView iv = new ImageView(context);
			iv.setLayoutParams(mParams);
			//防止图片不能填满屏幕
			iv.setScaleType(ScaleType.FIT_XY);
			//加载图片资源
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
		// 设置数据
		viewPager.setAdapter(vpAdapter);
		// 设置监听
		viewPager.setOnPageChangeListener(this);

		// 初始化底部小点
		initPoint(view);
	}

	
	/**
	 * 初始化底部小点
	 */
	private void initPoint(View view) {
		linearLayout = (LinearLayout)view.findViewById(R.id.ll);
		points = new ImageView[views.size()];
		// 循环取得小点图片
		for (int i = 0; i < views.size(); i++) {
			// 得到一个LinearLayout下面的每一个子元素
			points[i] = (ImageView) linearLayout.getChildAt(i);
			// 默认都设为灰色
			points[i].setEnabled(true);
			// 给每个小点设置监听
			points[i].setOnClickListener(this);
			// 设置位置tag，方便取出与当前位置对应
			points[i].setTag(i);
		}
		// 设置当面默认的位置
		currentIndex = 0;
		// 设置为白色，即选中状态
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
		// 设置底部小点选中状态
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
	 * 设置当前页面的位置
	 */
	private void setCurView(int position) {
		if (position < 0 || position >= views.size()) {
			return;
		}
		viewPager.setCurrentItem(position);	
	}
	
	
	/**
	 * 设置当前的小点的位置
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
	 * 生成二维码
	 */
	private void setData(){
		try {	
			 String contentString = Config.con_serial;  
			 if (!contentString.equals("")) {  
			     Bitmap qrCodeBitmap = EncodingHandler.createQRCode(contentString, 350);  
			     erweima.setImageBitmap(qrCodeBitmap); 
			 }
//			 //app下载的二维码	
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
