/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wgc.Zxing.view;

import java.util.Collection;
import java.util.HashSet;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.wgc.Zxing.camera.CameraManager;
import com.wgc.cmwgc.R;



/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 * 
 */
public final class ViewfinderView extends View {
	private static final String TAG = "log";
	/**
	 * 鍒锋柊鐣岄潰鐨勬椂锟�	 */
	private static final long ANIMATION_DELAY = 10L;
	private static final int OPAQUE = 0xFF;

	/**
	 * 鍥涗釜缁胯壊杈硅瀵瑰簲鐨勯暱锟�	 */
	private int ScreenRate;
	
	/**
	 * 鍥涗釜缁胯壊杈硅瀵瑰簲鐨勫锟�	 */
	private static final int CORNER_WIDTH = 10;
	/**
	 * 鎵弿妗嗕腑鐨勪腑闂寸嚎鐨勫锟�	 */
	private static final int MIDDLE_LINE_WIDTH = 6;
	
	/**
	 * 鎵弿妗嗕腑鐨勪腑闂寸嚎鐨勪笌鎵弿妗嗗乏鍙崇殑闂撮殭
	 */
	private static final int MIDDLE_LINE_PADDING = 5;
	
	/**
	 * 涓棿閭ｆ潯绾挎瘡娆″埛鏂扮Щ鍔ㄧ殑璺濈
	 */
	private static final int SPEEN_DISTANCE = 5;
	
	/**
	 * 鎵嬫満鐨勫睆骞曞瘑锟�	 */
	private static float density;
	/**
	 * 瀛椾綋澶у皬
	 */
	private static final int TEXT_SIZE = 16;
	/**
	 * 瀛椾綋璺濈鎵弿妗嗕笅闈㈢殑璺濈
	 */
	private static final int TEXT_PADDING_TOP = 30;
	
	/**
	 * 鐢荤瑪瀵硅薄鐨勫紩锟�	 */
	private Paint paint;
	
	/**
	 * 涓棿婊戝姩绾跨殑锟�锟斤拷绔綅锟�	 */
	private int slideTop;
	
	/**
	 * 涓棿婊戝姩绾跨殑锟�锟斤拷绔綅锟�	 */
	private int slideBottom;
	
	/**
	 * 灏嗘壂鎻忕殑浜岀淮鐮佹媿涓嬫潵锛岃繖閲屾病鏈夎繖涓姛鑳斤紝鏆傛椂涓嶏拷?锟�	 */
	private Bitmap resultBitmap;
	private final int maskColor;
	private final int resultColor;
	
	private final int resultPointColor;
	private Collection<ResultPoint> possibleResultPoints;
	private Collection<ResultPoint> lastPossibleResultPoints;

	boolean isFirst;
	
	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		density = context.getResources().getDisplayMetrics().density;
		//灏嗗儚绱犺浆鎹㈡垚dp
		ScreenRate = (int)(20 * density);

		paint = new Paint();
		Resources resources = getResources();
		maskColor = resources.getColor(R.color.viewfinder_mask);
		resultColor = resources.getColor(R.color.result_view);

		resultPointColor = resources.getColor(R.color.possible_result_points);
		possibleResultPoints = new HashSet<ResultPoint>(5);
	}

	@Override
	public void onDraw(Canvas canvas) {
		//涓棿鐨勬壂鎻忔锛屼綘瑕佷慨鏀规壂鎻忔鐨勫ぇ灏忥紝鍘籆ameraManager閲岄潰淇敼
		Rect frame = CameraManager.get().getFramingRect();
		if (frame == null) {
			return;
		}
		
		//鍒濆鍖栦腑闂寸嚎婊戝姩鐨勬渶涓婅竟鍜屾渶涓嬭竟
		if(!isFirst){
			isFirst = true;
			slideTop = frame.top;
			slideBottom = frame.bottom;
		}
		
		//鑾峰彇灞忓箷鐨勫鍜岄珮
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		paint.setColor(resultBitmap != null ? resultColor : maskColor);
		
		//鐢诲嚭鎵弿妗嗗闈㈢殑闃村奖閮ㄥ垎锛屽叡鍥涗釜閮ㄥ垎锛屾壂鎻忔鐨勪笂闈㈠埌灞忓箷涓婇潰锛屾壂鎻忔鐨勪笅闈㈠埌灞忓箷涓嬮潰
		//鎵弿妗嗙殑宸﹁竟闈㈠埌灞忓箷宸﹁竟锛屾壂鎻忔鐨勫彸杈瑰埌灞忓箷鍙宠竟
		canvas.drawRect(0, 0, width, frame.top, paint);
		canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
		canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1,
				paint);
		canvas.drawRect(0, frame.bottom + 1, width, height, paint);
		
		

		if (resultBitmap != null) {
			// Draw the opaque result bitmap over the scanning rectangle
			paint.setAlpha(OPAQUE);
			canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
		} else {

			//鐢绘壂鎻忔杈逛笂鐨勮锛岋拷?锟�涓儴锟�			
			paint.setColor(Color.GREEN);
			canvas.drawRect(frame.left, frame.top, frame.left + ScreenRate,
					frame.top + CORNER_WIDTH, paint);
			canvas.drawRect(frame.left, frame.top, frame.left + CORNER_WIDTH, frame.top
					+ ScreenRate, paint);
			canvas.drawRect(frame.right - ScreenRate, frame.top, frame.right,
					frame.top + CORNER_WIDTH, paint);
			canvas.drawRect(frame.right - CORNER_WIDTH, frame.top, frame.right, frame.top
					+ ScreenRate, paint);
			canvas.drawRect(frame.left, frame.bottom - CORNER_WIDTH, frame.left
					+ ScreenRate, frame.bottom, paint);
			canvas.drawRect(frame.left, frame.bottom - ScreenRate,
					frame.left + CORNER_WIDTH, frame.bottom, paint);
			canvas.drawRect(frame.right - ScreenRate, frame.bottom - CORNER_WIDTH,
					frame.right, frame.bottom, paint);
			canvas.drawRect(frame.right - CORNER_WIDTH, frame.bottom - ScreenRate,
					frame.right, frame.bottom, paint);

			
			//缁樺埗涓棿鐨勭嚎,姣忔鍒锋柊鐣岄潰锛屼腑闂寸殑绾垮線涓嬬Щ鍔⊿PEEN_DISTANCE
			slideTop += SPEEN_DISTANCE;
			if(slideTop >= frame.bottom){
				slideTop = frame.top;
			}
			canvas.drawRect(frame.left + MIDDLE_LINE_PADDING, slideTop - MIDDLE_LINE_WIDTH/2, frame.right - MIDDLE_LINE_PADDING,slideTop + MIDDLE_LINE_WIDTH/2, paint);
			
			
			//鐢绘壂鎻忔涓嬮潰鐨勫瓧
			paint.setColor(Color.WHITE);
			paint.setTextSize(TEXT_SIZE * density);
			paint.setAlpha(0x40);
			paint.setTypeface(Typeface.create("System", Typeface.BOLD));
			canvas.drawText(getResources().getString(R.string.scan_text), frame.left, (float) (frame.bottom + (float)TEXT_PADDING_TOP *density), paint);
			Collection<ResultPoint> currentPossible = possibleResultPoints;
			Collection<ResultPoint> currentLast = lastPossibleResultPoints;
			if (currentPossible.isEmpty()) {
				lastPossibleResultPoints = null;
			} else {
				possibleResultPoints = new HashSet<ResultPoint>(5);
				lastPossibleResultPoints = currentPossible;
				paint.setAlpha(OPAQUE);
				paint.setColor(resultPointColor);
				for (ResultPoint point : currentPossible) {
					canvas.drawCircle(frame.left + point.getX(), frame.top
							+ point.getY(), 6.0f, paint);
				}
			}
			if (currentLast != null) {
				paint.setAlpha(OPAQUE / 2);
				paint.setColor(resultPointColor);
				for (ResultPoint point : currentLast) {
					canvas.drawCircle(frame.left + point.getX(), frame.top
							+ point.getY(), 3.0f, paint);
				}
			}

			
						postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top,
					frame.right, frame.bottom);
			
		}
	}

	public void drawViewfinder() {
		resultBitmap = null;
		invalidate();
	}

	/**
	 * Draw a bitmap with the result points highlighted instead of the live
	 * scanning display.
	 * 
	 * @param barcode
	 *            An image of the decoded barcode.
	 */
	public void drawResultBitmap(Bitmap barcode) {
		resultBitmap = barcode;
		invalidate();
	}

	public void addPossibleResultPoint(ResultPoint point) {
		possibleResultPoints.add(point);
	}

}
