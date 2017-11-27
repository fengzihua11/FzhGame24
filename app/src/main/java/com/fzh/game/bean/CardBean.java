package com.fzh.game.bean;

import com.fzh.game.picture.CardDrawable;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class CardBean {

    public static final int NO_ID = -1;

    public boolean canMove = true;
    public boolean isEmpty = true;

    public Rect rect = null;
    public int mValue = NO_ID;
    public CardDrawable dr = null;

    public CardType type = CardType.NOMAL;

    public CardBean() {
        this(null, true);
    }

    public CardBean(CardDrawable defaultDr) {
        this(defaultDr, true);
    }

    public CardBean(CardDrawable defaultDr, boolean empty) {
        dr = defaultDr;
        isEmpty = empty;
    }

    public void setEmpty(boolean empty, CardDrawable newDr, int value) {
        isEmpty = empty;
        dr = newDr;
        mValue = value;
    }

    /**
     * �Ƿ����ڵ�(pointX, pointY)
     *
     * @param pointX
     * @param pointY
     * @return
     */
    public boolean isHint(int pointX, int pointY) {
        if (rect == null)
            return false;
        if (rect.contains(pointX, pointY))
            return true;
        return false;
    }

    public void clear() {
        rect = null;
        mValue = NO_ID;
        dr = null;
    }

    public void drawCard(Canvas canvas, Paint paint) {
        if (dr != null)
            dr.draw(canvas, rect, paint);
    }

    public enum CardType {
        NOMAL, RESULT, PLUG, EQUAL, BUTTON, COUNT, BACKGROUND
    }
}