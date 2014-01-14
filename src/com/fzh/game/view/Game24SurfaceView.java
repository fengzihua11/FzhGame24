package com.fzh.game.view;

import com.fzh.game.ershi.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Game24SurfaceView extends SurfaceView implements SurfaceHolder.Callback {

	private static final boolean ISLOG = false;
	private static final String TAG = "Game24SurfaceView";

	private int mWidth = 0;
	private int mHeight = 0;
	private SurfaceHolder holder;

	private Paint mPaint = null;

	private Rect[] rectQuestions = new Rect[4];
	private Rect[] rectAnswers = new Rect[3];

	public Game24SurfaceView(Context context) {
		this(context, null);
	}

	public Game24SurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);

		if (ISLOG)
			Log.d(TAG, "Game24View()");
		mPaint = new Paint();
		mPaint.setAntiAlias(true);

		holder = getHolder();// 获取holder
		holder.addCallback(this);
		// setFocusable(true);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (ISLOG)
			Log.d(TAG, "onSizeChanged()");
		super.onSizeChanged(w, h, oldw, oldh);
		mWidth = getWidth();
		mHeight = getHeight();
		makeRects();
		if (ISLOG)
			Log.d(TAG, "onSizeChanged()-- mWidth = " + mWidth + "-- mHeight = "
					+ mHeight);
	}

	protected void onAttachedToWindow() {
		if (ISLOG)
			Log.d(TAG, "onAttachedToWindow()");
		super.onAttachedToWindow();

	}

	protected void onDetachedFromWindow() {
		if (ISLOG)
			Log.d(TAG, "onDetachedFromWindow()");
		super.onDetachedFromWindow();
	}

	private void makeRects() {
		for (int i = 0; i < rectQuestions.length; i++) {
			rectQuestions[i] = new Rect(10 * (i + 1) + 100 * i, 20, (10 + 100)
					* (i + 1), 170);
		}

		for (int i = 0; i < rectAnswers.length; i++) {
			rectAnswers[i] = new Rect(10 * (i + 1) + 100 * i, mHeight - 170,
					(10 + 100) * (i + 1), mHeight - 20);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		if (ISLOG)
			Log.d(TAG, "surfaceChanged()");

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (ISLOG)
			Log.d(TAG, "surfaceCreated()");
		new Thread(new MyThread()).start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (ISLOG)
			Log.d(TAG, "surfaceDestroyed()");
	}

	// 内部类的内部类
	class MyThread implements Runnable {
		public void run() {
			if (ISLOG)
				Log.d(TAG, "run()");
			Canvas canvas = null;
			while(true) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				canvas = holder.lockCanvas(null);// 获取画布
				// canvas.drawRect(new RectF(40, 60, 80, 80), mPaint);
				drawBackground(canvas);
				drawCardsBackground(canvas);
				drawCards(canvas);
				drawAnswers(canvas);
				drawMoveCard(canvas);
				holder.unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
			}
		}
	}

	private void drawBackground(Canvas canvas) {
		if (ISLOG)
			Log.d(TAG, "drawBackground()");
		Bitmap bitmap = ((BitmapDrawable) getContext().getResources()
				.getDrawable(R.drawable.desk_bg)).getBitmap();
		canvas.drawBitmap(bitmap, 0, 0, mPaint);
	}

	private int[] setBitmapIds() {
		ids[0] = R.drawable.k_1;
		ids[1] = R.drawable.k_2;
		ids[2] = R.drawable.k_3;
		ids[3] = R.drawable.k_4;
		return ids;
	}
	
	private void drawCardsBackground(Canvas canvas) {
		if (ISLOG)
			Log.i(TAG, "drawCards");
		setBitmapIds();
		Bitmap bitmap = null;
		for (int i = 0; i < ids.length && i != dragIndex; i++) {
			bitmap = ((BitmapDrawable) getContext().getResources().getDrawable(R.drawable.black_card_0)).getBitmap();
			canvas.drawBitmap(bitmap, null, rectQuestions[i], mPaint);
		}
	}

	private void drawCards(Canvas canvas) {
		if (ISLOG)
			Log.i(TAG, "drawCards");
		setBitmapIds();
		Bitmap bitmap = null;
		for (int i = 0; i < ids.length && i != dragIndex; i++) {
			bitmap = ((BitmapDrawable) getContext().getResources().getDrawable(
					ids[i])).getBitmap();
			canvas.drawBitmap(bitmap, null, rectQuestions[i], mPaint);
		}
	}

	private void drawAnswers(Canvas canvas) {
		if (ISLOG)
			Log.i(TAG, "drawAnswers");
		Bitmap bitmap = null;
		for (int i = 0; i < rectAnswers.length; i++) {
			bitmap = ((BitmapDrawable) getContext().getResources().getDrawable(
					R.drawable.black_card_0)).getBitmap();
			canvas.drawBitmap(bitmap, null, rectAnswers[i], mPaint);
		}
	}

	private void drawMoveCard(Canvas canvas) {		
		if(dragIndex < 0)
			return;
		Log.i(TAG, "drawMoveCard --- dragIndex = " + dragIndex);
		Bitmap bitmap = ((BitmapDrawable) getContext().getResources()
				.getDrawable(ids[dragIndex])).getBitmap();
		canvas.drawBitmap(bitmap, null, moveRect, mPaint);
	}

	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			dragIndex = countPoint(event);
			if(dragIndex >= 0) {
				paddingLeft = ((int) event.getX()) - rectQuestions[dragIndex].left;
				paddingTop = ((int) event.getY()) - rectQuestions[dragIndex].top;
			}			
			break;

		case MotionEvent.ACTION_MOVE:
			if(dragIndex >= 0) {
				calendMoveRect(event);
			}
			break;

		default:
			dragIndex = -1;
			break;
		}		
		return super.onTouchEvent(event);
	}
	
	private void calendMoveRect(MotionEvent event) {
		int left = (int) event.getX() - paddingLeft;
		int top = (int) event.getY() - paddingTop;
		int right = left + 100;
		int bottom = top + 150;
		moveRect.set(left, top, right, bottom);
	}

	private int countPoint(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();

		for (int i = 0; i < rectQuestions.length; i++) {
			if (rectQuestions[i].contains(x, y))
				return i;
		}
		return -1;
	}

	private Rect moveRect = new Rect();

	private int ids[] = new int[4];

	private int paddingLeft = 0;
	private int paddingTop = 0;
	private int dragIndex = -1;
}