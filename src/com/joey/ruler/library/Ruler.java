package com.joey.ruler.library;

import com.joey.ruler.R;
import com.joey.ruler.library.RulerScrollView.ScrollType;
import com.joey.ruler.library.RulerScrollView.ScrollViewListener;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

/**
 * Define a ruler, it can be scrolled horizontal,and marks the current label
 * 
 * @author Joey <lma_ma@163.com>
 * 
 */
public class Ruler extends FrameLayout {

	/**
	 * 标记，采用红线标示，标记当前刻度
	 */
	private ImageView mark;

	private Bitmap markBgBmp;
	/**
	 * 绘制的几个刻度，有三种刻度，最大刻度，中等刻度，和最小刻度
	 */
	private Drawable minDrawable;
	private Drawable maxDrawable;
	private Drawable midDrawable;

	private float bmpMaxHeight = 60.0f;

	private float maxTextSize = 15.0f;

	/**
	 * unit size of the ruler
	 */
	private float minUnitSize = 20.0f;
	/**
	 * 最大单位的个数
	 */
	private int maxUnitCount = 24;
	/**
	 * 最大单位包含的每个单位数
	 */
	private int perUnitCount = 10;

	private int maxUnitColor;
	private int midUnitColor;
	private int minUnitColor;

	/**
	 * Padding on the left,
	 */
	private float padding = 10.0f;
	/**
	 * 刻度的宽度
	 */
	private final int UNIT_ITEM_WIDTH = 2;
	/**
	 * 刻度容器
	 */
	private LinearLayout unitContainer;
	/**
	 * 单位文字容器
	 */
	private LinearLayout textContainer;
	private RelativeLayout rulerContainer;
	/**
	 * 整个刻度尺
	 */
	private LinearLayout rootContainer;
	/**
	 * 横向滑动的scrollerView
	 */
	private RulerScrollView scrollerView;

	private RulerHandler rulerHandler;
	/**
	 * 标记刻度尺的类型，一种是一般的刻度尺， 另一种为时间刻度尺
	 */
	public final static int MODE_RULER = 0;
	public final static int MODE_TIMELINE = 1;

	private int mode;

	public Ruler(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Ruler,
				defStyleAttr, 0);
		minUnitSize = a.getDimension(R.styleable.Ruler_min_unit_size, 20.0f);
		maxUnitCount = a.getInteger(R.styleable.Ruler_max_unit_count, 24);
		perUnitCount = a.getInteger(R.styleable.Ruler_per_unit_count, 10);
		bmpMaxHeight = a.getDimension(R.styleable.Ruler_unit_bmp_height, 60.0f);
		mode = a.getInt(R.styleable.Ruler_ruler_mode, MODE_TIMELINE);
		padding = minUnitSize / 2;

		a.recycle();

		init();
	}

	public Ruler(Context context, AttributeSet attrs) {
		super(context, attrs, R.attr.ruler_style);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Ruler,
				R.attr.ruler_style, 0);
		minUnitSize = a.getDimension(R.styleable.Ruler_min_unit_size, 20.0f);
		maxUnitCount = a.getInteger(R.styleable.Ruler_max_unit_count, 24);
		perUnitCount = a.getInteger(R.styleable.Ruler_per_unit_count, 10);
		bmpMaxHeight = a.getDimension(R.styleable.Ruler_unit_bmp_height, 60.0f);
		mode = a.getInt(R.styleable.Ruler_ruler_mode, MODE_TIMELINE);
		padding = minUnitSize / 2;

		a.recycle();
		Log.i("Ruler",
				String.format(
						"minUnitSize %02f,maxUnitCount %d,perUnitCount %d,bmpMaxHeight %02f,mode %d",
						minUnitSize, maxUnitCount, perUnitCount, bmpMaxHeight,
						mode));
		init();
	}

	public Ruler(Context context) {
		super(context, null);
		init();

	}

	private void init() {
		Log.i("Ruler", "ruler init");
		initDrawable();
		initParentContainer();
		initUnit();
		scrollerView.setOnScrollStateChangedListener(scrollListener);
	}

	/**
	 * 初始化刻度尺的第一级容器
	 */
	private void initParentContainer() {
		scrollerView = new RulerScrollView(getContext());
		scrollerView.setVerticalScrollBarEnabled(false);
		scrollerView.setHorizontalScrollBarEnabled(false);
		FrameLayout.LayoutParams scrollerParams = new FrameLayout.LayoutParams(
				-1, -2);
		scrollerParams.gravity = Gravity.CENTER_VERTICAL;
		scrollerView.setLayoutParams(scrollerParams);
		addView(scrollerView);

		rootContainer = new LinearLayout(getContext());
		rootContainer.setLayoutParams(new HorizontalScrollView.LayoutParams(-1,
				-2));
		scrollerView.addView(rootContainer);

		rulerContainer = new RelativeLayout(getContext());
		rulerContainer.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
		rootContainer.addView(rulerContainer);
		//
		unitContainer = new LinearLayout(getContext());
		RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
				-1, -2);
		params1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		unitContainer.setLayoutParams(params1);
		unitContainer.setOrientation(LinearLayout.HORIZONTAL);
		unitContainer.setId(R.id.unit_container_id);
		unitContainer.setPadding(dp2px((int) padding), 0, dp2px((int) padding),
				0);
		rulerContainer.addView(unitContainer);

		textContainer = new LinearLayout(getContext());
		RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
				-1, -2);
		params2.addRule(RelativeLayout.BELOW, R.id.unit_container_id);
		textContainer.setLayoutParams(params2);
		textContainer.setOrientation(LinearLayout.HORIZONTAL);
		rulerContainer.addView(textContainer);

		mark = new ImageView(getContext());
		FrameLayout.LayoutParams params3 = new FrameLayout.LayoutParams(-2, -2);
		params3.gravity = Gravity.CENTER;
		params3.leftMargin = -markBgBmp.getWidth() / 2;
		mark.setLayoutParams(params3);
		mark.setImageBitmap(markBgBmp);
		addView(mark);

	}

	/**
	 * 初始化刻度与刻度标记部分
	 */
	private void initUnit() {

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				dp2px((int) minUnitSize), -2);

		for (int i = 0; i < maxUnitCount; i++) {
			for (int j = 0; j < perUnitCount; j++) {
				TextView minUnitView = new TextView(getContext());
				minUnitView.setLayoutParams(params);
				minUnitView.setTextSize(.1f);
				minUnitView.setGravity(Gravity.BOTTOM
						| Gravity.CENTER_HORIZONTAL);
				if (j == 0) {
					minUnitView.setCompoundDrawables(null, maxDrawable, null,
							null);
				} else if (j == perUnitCount / 2) {
					minUnitView.setCompoundDrawables(null, midDrawable, null,
							null);
				} else {
					minUnitView.setCompoundDrawables(null, minDrawable, null,
							null);
				}
				minUnitView.setText("");
				unitContainer.addView(minUnitView);
			}
		}
		TextView maxUnitView = new TextView(getContext());
		maxUnitView.setTextSize(.1f);
		maxUnitView.setLayoutParams(params);
		maxUnitView.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		maxUnitView.setCompoundDrawables(null, maxDrawable, null, null);
		unitContainer.addView(maxUnitView);

		LinearLayout.LayoutParams maxParams = new LinearLayout.LayoutParams(
				dp2px((int) minUnitSize * perUnitCount / 2), -2);
		for (int i = 0; i < maxUnitCount * 2; i++) {
			TextView textUnitView = new TextView(getContext());
			textUnitView.setTextSize(maxTextSize);
			textUnitView.setLayoutParams(maxParams);
			textUnitView.setGravity(Gravity.TOP | Gravity.LEFT);
			switch (mode) {
			case MODE_RULER:
				if (i % 2 == 0)
					textUnitView.setText(String.format("    %d  ", i / 2));
				break;
			case MODE_TIMELINE:
				if (i % 2 == 0)
					textUnitView.setText(String.format("%02d:00", i / 2));
				else
					textUnitView.setText(String.format("%02d:30", i / 2));

			}

			textContainer.addView(textUnitView);
		}
	}

	/**
	 * 初始化单位的背景图
	 */
	private void initDrawable() {
		Bitmap bmp1 = Bitmap.createBitmap(dp2px(UNIT_ITEM_WIDTH),
				dp2px((int) bmpMaxHeight), Config.ARGB_8888);
		Bitmap bmp2 = Bitmap.createBitmap(dp2px(UNIT_ITEM_WIDTH),
				dp2px((int) bmpMaxHeight) * 3 / 4, Config.ARGB_8888);
		Bitmap bmp3 = Bitmap.createBitmap(dp2px(UNIT_ITEM_WIDTH),
				dp2px((int) bmpMaxHeight) * 2 / 3, Config.ARGB_8888);
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(10);
		paint.setStyle(Paint.Style.STROKE);

		Canvas canvas1 = new Canvas(bmp1);
		canvas1.drawLine(0, 10, 0, bmp1.getHeight(), paint);

		Canvas canvas2 = new Canvas(bmp2);
		canvas2.drawLine(0, 10, 0, bmp2.getHeight(), paint);

		Canvas canvas3 = new Canvas(bmp3);
		paint.setAlpha(80);
		canvas3.drawLine(0, 10, 0, bmp3.getHeight(), paint);

		minDrawable = new BitmapDrawable(bmp3);
		minDrawable.setBounds(0, 0, minDrawable.getMinimumWidth(),
				minDrawable.getMinimumHeight());
		maxDrawable = new BitmapDrawable(bmp1);
		maxDrawable.setBounds(0, 0, maxDrawable.getMinimumWidth(),
				maxDrawable.getMinimumHeight());
		midDrawable = new BitmapDrawable(bmp2);
		midDrawable.setBounds(0, 0, midDrawable.getMinimumWidth(),
				midDrawable.getMinimumHeight());
		markBgBmp = Bitmap.createBitmap(2 * dp2px(UNIT_ITEM_WIDTH),
				dp2px((int) bmpMaxHeight) + dp2px((int) maxTextSize),
				Config.ARGB_8888);
		Canvas canvas4 = new Canvas(markBgBmp);
		paint.setColor(Color.RED);
		canvas4.drawLine(0, 0, 0, markBgBmp.getHeight(), paint);

	}

	public void setRulerHandler(RulerHandler rulerHandler) {
		this.rulerHandler = rulerHandler;
	}

	/**
	 * time format is HH:MM
	 * 跳转到时间刻度尺的部分，只有在时间轴模式下条件下才能使用
	 * @param formatTime
	 */
	public void scrollToTime(String formatTime) {
		if (mode == MODE_RULER)
			return;
		if (formatTime == null || formatTime.isEmpty())
			return;
		String value[] = formatTime.split(":");
		if (value.length < 2)
			return;
		int minVal = (getWidth() / 2 - dp2px((int) padding + (int) minUnitSize
				/ 2 - UNIT_ITEM_WIDTH * 2))
				/ dp2px((int) minUnitSize);
		Log.i(getClass().getName(), "minVal = " + minVal);
		int hour = Integer.parseInt(value[0]) % 24;
		int minute = Integer.parseInt(value[1]) % 60;
		Log.i(getClass().getName(), "hour is " + hour + ",minute is " + minute);
		float val = hour * 10 + (float) minute / 6;
		Log.i(getClass().getName(), "val = " + val);
		if (val < minVal) {
			scrollerView.smoothScrollTo(0, 0);
			return;
		}
		scrollerView.smoothScrollTo(
				(int) ((val - minVal) * dp2px((int) minUnitSize)), 0);
	}
	/**
	 * 跳转到刻度尺的某个位置
	 * @param max 最大刻度
	 * @param min 最小刻度
	 * @param val 最小刻度的浮点部分
	 */
	public void scrollTo(int max, int min, float val) {
		int minVal = (getWidth() / 2 - dp2px((int) padding + (int) minUnitSize
				/ 2 - UNIT_ITEM_WIDTH * 2))
				/ dp2px((int) minUnitSize);
		Log.i(getClass().getName(), "minVal = " + minVal);

		int total = max * 10 + min;
		if (total < minVal) {
			scrollerView.smoothScrollTo(0, 0);
			return;
		}
		scrollerView.smoothScrollTo(
				(int) ((total - minVal + val) * dp2px((int) minUnitSize)), 0);
	}

	ScrollViewListener scrollListener = new ScrollViewListener() {

		@Override
		public void onScrollChanged(ScrollType scrollType) {
			// TODO Auto-generated method stub
			switch (scrollType) {
			case IDLE:
			case TOUCH_SCROLL:
			case FLING:
				int scrollX = scrollerView.getScrollX();

				int newScrollX = (scrollX + getWidth() / 2
						- dp2px((int) padding) - dp2px((int) minUnitSize) / 2);
				int bigUnitSize = (dp2px((int) minUnitSize) * perUnitCount);
				int smallUnitSize = dp2px((int) minUnitSize);
				int max = newScrollX / bigUnitSize;
				int min = newScrollX / smallUnitSize % perUnitCount;
				float val = (float) (newScrollX - (max * bigUnitSize) - (min * smallUnitSize))
						/ (float) smallUnitSize;

				Log.i(getClass().getName(), "max = " + max + ",min = " + min
						+ ",val = " + val);
				if (rulerHandler != null) {
					rulerHandler.markScrollto(max, min, val);
				}
				break;
			}
		}

	};

	public int dp2px(int dp) {
		float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	public int px2dp(int px) {
		float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}

}
