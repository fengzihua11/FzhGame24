package com.fzh.game.picture;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public class CiclerDrawable extends Drawable {

	private int mRadius = 0;
	private int mColor = Color.BLACK;
	private Paint paint = null;
	//private int mRound = 1;
	private float middleX;
	private float middleY;

	public CiclerDrawable(int color, int radius, int round, int x, int y) {
		mRadius = radius;
		mColor = color;
		//mRound = round;
		middleX = x;
		middleY = y;
		paint = new Paint();
		paint.setColor(mColor);
		paint.setAntiAlias(true);
		paint.setShadowLayer(2, 3.0f, 2.0f, Color.GRAY);
	}

	public void setColor(int color) {
		mColor = color;
		paint.setColor(mColor);
	}
	
	public void draw(Canvas canvas, int color) {
		setColor(color);
		draw(canvas);
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawCircle(middleX, middleY, mRadius, paint);
	}

	@Override
	public int getIntrinsicWidth() {
		return mRadius * 2;
	}

	@Override
	public int getIntrinsicHeight() {
		return mRadius * 2;
	}

	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	public void setAlpha(int alpha) {
	}

	public void setColorFilter(ColorFilter cf) {
	}	
}