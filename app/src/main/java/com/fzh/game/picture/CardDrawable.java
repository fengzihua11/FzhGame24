package com.fzh.game.picture;

import com.fzh.game.bean.CardBean;
import com.fzh.game.view.Game24View;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Paint.Align;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * 卡片drawable
 */
public class CardDrawable extends Drawable {

    private static final String TAG = "ershi";

    private Bitmap mBitmap;
    private int mWidth;
    private int mHeight;
    private int mValue = CardBean.NO_ID;
    private Paint tPaint = null;
    private Rect mBaseRect = null;

    public CardDrawable(Context context, int resId) {
        this(context, resId, CardBean.NO_ID);
    }

    public CardDrawable(Context context, int resId, int value) {
        if (value != CardBean.NO_ID) {
            mValue = value;
            initPaint();
        }
        mBitmap = ((BitmapDrawable) context.getResources().getDrawable(resId)).getBitmap();
        if (mBitmap != null) {
            mWidth = mBitmap.getWidth();
            mHeight = mBitmap.getHeight();
        } else {
            mWidth = mHeight = 0;
        }
        Log.d(TAG, "CardDrawable-->mWidth: " + mWidth + ", mHeight: " + mHeight + ", value: " + value);
    }

    public void reSize(int w, int h) {
        mWidth = w;
        mHeight = h;
        if(mBaseRect == null) {
            mBaseRect = new Rect(0, 0, mWidth, mHeight);
        } else {
            mBaseRect.set(0,0,mWidth,mHeight);
        }
    }

    private void initPaint() {
        if(tPaint == null)
            tPaint = new Paint();
        tPaint.setTextSize(54);
        tPaint.setColor(Color.RED);
        tPaint.setAntiAlias(true);
        tPaint.setShadowLayer(2, 3.0f, 2.0f, Color.GRAY);
        tPaint.setTextAlign(Align.CENTER);
    }

    @Override
    public void draw(Canvas canvas) {
        draw(canvas, null, null);
    }

    public void draw(Canvas canvas, Rect rect, Paint paint) {
        if (rect != null) {
            canvas.drawBitmap(mBitmap, null, rect, paint);
        } else if(mBaseRect != null){
            canvas.drawBitmap(mBitmap, null, mBaseRect, paint);
        } else {
            canvas.drawBitmap(mBitmap, 0, 0, paint);
        }
        int left = 0;
        int top = 0;
        if(rect != null) {
            left = rect.left;
            top = rect.top;
        }
        if(mValue == Game24View.ISNULL_BEI) {
            canvas.drawText("Ill", left + mWidth / 2,
                    top + (mHeight + 30) / 2, tPaint);
        } else if(mValue == Game24View.CANNOT_DIV) {
            canvas.drawText("No", left + mWidth / 2,
                    top + (mHeight + 30) / 2, tPaint);
        } else if (mValue != CardBean.NO_ID) {
            canvas.drawText(String.valueOf(mValue),
                    left + mWidth / 2,
                    top + (mHeight + 30) / 2,
                    tPaint);
        }
    }

    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter cf) {
    }

    public int getIntrinsicWidth() {
        return mWidth;
    }

    public int getIntrinsicHeight() {
        return mHeight;
    }

    public int getMinimumWidth() {
        return mWidth;
    }

    public int getMinimumHeight() {
        return mHeight;
    }

}