package com.joey.ruler.library;

import com.joey.ruler.R;

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
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

/**
 * Define a ruler, it can be scrolled horizontal,and marks the current label
 * 
 * @author Joey
 * 
 */
public class Ruler extends LinearLayout {

	private ImageView mark;
	
	private Bitmap markBgBmp;
	private Drawable minDrawable;
	private Drawable maxDrawable;
	private Drawable midDrawable;

	private int bmpMaxHeight = 80;
	/**
	 * max unit length of the ruler
	 */
	private int maxUnitSize = 100;
	private float MAX_TEXT_SIZE = 15.0f;

	/**
	 * min unit length of the ruler
	 */
	private int minUnitSize = 20;

	private Scroller scroller;

	private int maxUnitCount = 24;
	private int perUnitCount = 10;
	private int currentUnit;

	private int lastX;
	private int lastY;

	private final int PADDING = 10;

	private static final float TAN = 2.0f;
	
	private final int UNIT_ITEM_WIDTH = 2;
	private LinearLayout unitContainer;
	private LinearLayout textContainer;
	private RelativeLayout rootContainer;
	
	private HorizontalScrollView scrollerView;

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
		setOrientation(HORIZONTAL);	
		scroller = new Scroller(getContext());
		initDrawable();
		initParentContainer();
		initUnit();
//		mark = new ImageView(getContext());
//		RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(-2, -2);
//		params3.leftMargin = getWidth()/2;
//		mark.setLayoutParams(params3);
//		mark.setImageBitmap(markBgBmp);
//		rootContainer.addView(mark);
	}

	private void initParentContainer()
	{
		scrollerView = new HorizontalScrollView(getContext());
		
		rootContainer = new RelativeLayout(getContext());
		rootContainer.setLayoutParams(new LinearLayout.LayoutParams(-1,-2));
		addView(rootContainer);	
//		
		unitContainer = new LinearLayout(getContext());
		RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(-1,-2);
		params1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		unitContainer.setLayoutParams(params1);
		unitContainer.setOrientation(HORIZONTAL);
		unitContainer.setId(R.id.unit_container_id);
		unitContainer.setPadding(dp2px(PADDING), 0, dp2px(PADDING), 0);
		rootContainer.addView(unitContainer);
		
		textContainer = new LinearLayout(getContext());
		RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(-1,-2);
		params2.addRule(RelativeLayout.BELOW,R.id.unit_container_id);
		textContainer.setLayoutParams(params2);
		textContainer.setOrientation(HORIZONTAL);
		rootContainer.addView(textContainer);
		
	}
	private void initUnit() {
			
		LinearLayout.LayoutParams params = new LayoutParams(
				dp2px(minUnitSize), -2);
		
		for (int i = 0; i < maxUnitCount; i++) {
			for(int j = 0;j<perUnitCount;j++)
			{
				TextView minUnitView = new TextView(getContext());
				minUnitView.setLayoutParams(params);
				minUnitView.setTextSize(.1f);
				minUnitView.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
				if(j == 0)
				{
					minUnitView.setCompoundDrawables(null, maxDrawable, null,null);
				}
				else if(j == 5)
				{
					minUnitView.setCompoundDrawables(null,midDrawable, null,null);
				}
				else
				{
					minUnitView.setCompoundDrawables(null, minDrawable,null, null );
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
		
		LinearLayout.LayoutParams maxParams = new LayoutParams(
				dp2px(maxUnitSize), -2);	
		for(int i = 0;i<maxUnitCount *2;i++)
		{
			TextView textUnitView = new TextView(getContext());
			textUnitView.setTextSize(MAX_TEXT_SIZE);
			textUnitView.setLayoutParams(maxParams);
			textUnitView.setGravity(Gravity.TOP| Gravity.LEFT);
			if(i %2 == 0)
				textUnitView.setText(String.format("%02d:00", i/2));
			else
				textUnitView.setText(String.format("%02d:30", i/2));
			textContainer.addView(textUnitView);
		}
	}

	private void initDrawable() {
		Bitmap bmp1 = Bitmap.createBitmap(dp2px(UNIT_ITEM_WIDTH), dp2px(bmpMaxHeight),
				Config.ARGB_8888);
		Bitmap bmp2 = Bitmap.createBitmap(dp2px(UNIT_ITEM_WIDTH),
				dp2px(bmpMaxHeight) * 3 / 4, Config.ARGB_8888);
		Bitmap bmp3 = Bitmap.createBitmap(dp2px(UNIT_ITEM_WIDTH),
				dp2px(bmpMaxHeight) * 2 / 3, Config.ARGB_8888);
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(10);
		paint.setStyle(Paint.Style.STROKE);
		
		Canvas canvas1 = new Canvas(bmp1);
		canvas1.drawLine(0, 10, 0, bmp1.getHeight() , paint);

		Canvas canvas2 = new Canvas(bmp2);
		canvas2.drawLine(0, 10, 0, bmp2.getHeight() , paint);
		
		Canvas canvas3 = new Canvas(bmp3);
		paint.setAlpha(80);
		canvas3.drawLine(0, 10, 0, bmp3.getHeight() , paint);

		minDrawable = new BitmapDrawable(bmp3);
		minDrawable.setBounds(0, 0, minDrawable.getMinimumWidth(),
				minDrawable.getMinimumHeight());
		maxDrawable = new BitmapDrawable(bmp1);
		maxDrawable.setBounds(0, 0, maxDrawable.getMinimumWidth(),
				maxDrawable.getMinimumHeight());
		midDrawable = new BitmapDrawable(bmp2);
		midDrawable.setBounds(0, 0, midDrawable.getMinimumWidth(),
				midDrawable.getMinimumHeight());
		markBgBmp = Bitmap.createBitmap(2*dp2px(UNIT_ITEM_WIDTH), dp2px(bmpMaxHeight)+dp2px((int)MAX_TEXT_SIZE),
				Config.ARGB_8888);
		Canvas canvas4 = new Canvas(markBgBmp);
		paint.setColor(Color.RED);
		canvas4.drawLine(0, 10, 0, markBgBmp.getHeight() , paint);

	}

	public void onRequireTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		int scrollX = getScrollX();
		Log.i(getClass().getName(), "scrollX :" + scrollX);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			if (!scroller.isFinished()) {
				scroller.abortAnimation();
			}
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			int deltaX = x - lastX;
			int deltaY = y - lastY;
			if (Math.abs(deltaX) < Math.abs(deltaY) * TAN) {
				break;
			}

			int newScrollX = scrollX - deltaX;
			if (newScrollX < -dp2px(PADDING)
					|| newScrollX > (maxUnitCount + 1) * dp2px(maxUnitSize)
							+ -getWidth())
				return;
			this.scrollTo(newScrollX, 0);

			// }
			break;
		}
		case MotionEvent.ACTION_UP: {

			break;
		}
		default:
			break;
		}

		lastX = x;
		lastY = y;
	}

	View.OnTouchListener listener = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			onRequireTouchEvent(event);
			return true;
		}
	};

	private void smoothScrollTo(int destX, int destY) {
		// 缓慢滚动到指定位置
		int scrollX = getScrollX();
		int delta = destX - scrollX;
		scroller.startScroll(scrollX, 0, delta, 0, Math.abs(delta) * 3);
		invalidate();
	}

	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			scrollTo(scroller.getCurrX(), scroller.getCurrY());
			postInvalidate();
		}
	}

	public void reset() {
		scroller.startScroll(getScrollX(), 0, -getScrollX(), 0, 0);
		invalidate();
	}

	public int dp2px(int dp) {
		float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	public int px2dp(int px) {
		float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}

}
