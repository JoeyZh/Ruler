package com.joey.ruler.library;

import com.joey.ruler.R;
import com.joey.ruler.library.RulerScrollView.ScrollType;
import com.joey.ruler.library.RulerScrollView.ScrollViewListener;

import android.content.Context;
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

	private ImageView mark;

	private Bitmap markBgBmp;
	private Drawable minDrawable;
	private Drawable maxDrawable;
	private Drawable midDrawable;

	private int bmpMaxHeight = 60;

	private float MAX_TEXT_SIZE = 15.0f;

	/**
	 * unit size of the ruler
	 */
	private int minUnitSize = 20;
	private int maxUnitCount = 24;
	private int perUnitCount = 10;
	private int currentUnit;

	private int lastX;
	private int lastY;

	/**
	 * Padding on the left,
	 */
	private final int PADDING = 10;

	private final int UNIT_ITEM_WIDTH = 2;
	private LinearLayout unitContainer;
	private LinearLayout textContainer;
	private RelativeLayout rulerContainer;
	private LinearLayout rootContainer;

	private RulerScrollView scrollerView;
	private RulerHandler rulerHandler;

	public Ruler(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		init();
	}

	public Ruler(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public Ruler(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	private void init() {
		initDrawable();
		initParentContainer();
		initUnit();
		scrollerView.setOnScrollStateChangedListener(scrollListener);
	}

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
		unitContainer.setPadding(dp2px(PADDING), 0, dp2px(PADDING), 0);
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

	private void initUnit() {

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				dp2px(minUnitSize), -2);

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
				} else if (j == 5) {
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
				dp2px(minUnitSize *perUnitCount/2), -2);
		for (int i = 0; i < maxUnitCount * 2; i++) {
			TextView textUnitView = new TextView(getContext());
			textUnitView.setTextSize(MAX_TEXT_SIZE);
			textUnitView.setLayoutParams(maxParams);
			textUnitView.setGravity(Gravity.TOP | Gravity.LEFT);
			if (i % 2 == 0)
				textUnitView.setText(String.format("%02d:00", i / 2));
			else
				textUnitView.setText(String.format("%02d:30", i / 2));
			textContainer.addView(textUnitView);
		}
	}

	/**
	 * 初始化单位的背景图
	 */
	private void initDrawable() {
		Bitmap bmp1 = Bitmap.createBitmap(dp2px(UNIT_ITEM_WIDTH),
				dp2px(bmpMaxHeight), Config.ARGB_8888);
		Bitmap bmp2 = Bitmap.createBitmap(dp2px(UNIT_ITEM_WIDTH),
				dp2px(bmpMaxHeight) * 3 / 4, Config.ARGB_8888);
		Bitmap bmp3 = Bitmap.createBitmap(dp2px(UNIT_ITEM_WIDTH),
				dp2px(bmpMaxHeight) * 2 / 3, Config.ARGB_8888);
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
				dp2px(bmpMaxHeight) + dp2px((int) MAX_TEXT_SIZE),
				Config.ARGB_8888);
		Canvas canvas4 = new Canvas(markBgBmp);
		paint.setColor(Color.RED);
		canvas4.drawLine(0, 0, 0, markBgBmp.getHeight(), paint);

	}

	public void setRulerHandler(RulerHandler rulerHandler) {
		this.rulerHandler = rulerHandler;
	}

	/**
	 *  time format is HH:MM
	 * @param formatTime
	 */
	public void scrollToTime(String formatTime)
	{
		if(formatTime == null||formatTime.isEmpty())
			return;
		String value[] = formatTime.split(":");
		if(value.length<2)
			return;
		int minVal = (getWidth()/2-dp2px(PADDING+minUnitSize/2-UNIT_ITEM_WIDTH*2))/dp2px(minUnitSize);
		Log.i(getClass().getName(),"minVal = "+minVal);
		int hour = Integer.parseInt(value[0])%24;
		int minute = Integer.parseInt(value[1])%60;
		Log.i(getClass().getName(), "hour is "+hour+",minute is "+minute);
		float val = hour *10 + (float)minute /6;
		Log.i(getClass().getName(),"val = "+val);
		if(val < minVal)
		{
			scrollerView.smoothScrollTo(0, 0);
			return;
		}
		scrollerView.smoothScrollTo((int)((val - minVal)*dp2px(minUnitSize)), 0);
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

				int newScrollX = (scrollX + getWidth() / 2 - dp2px(PADDING) - dp2px(minUnitSize) / 2);
				int hourUnitSize = (dp2px(minUnitSize) * perUnitCount);

				int hour = newScrollX / hourUnitSize;
				int val = newScrollX / dp2px(minUnitSize);
				int dep = (int) val % perUnitCount;

				int minute = (newScrollX - hour * hourUnitSize) * 6
						/ (dp2px(minUnitSize));
				Log.i(getClass().getName(), "hour = " + hour + "dep = " + dep
						+ ",minute = " + minute);

				if (rulerHandler != null) {
					rulerHandler.markScrollto(hour, minute, val);
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
