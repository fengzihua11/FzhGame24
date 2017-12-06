package com.fzh.game.view;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import com.fzh.game.bean.CardBean;
import com.fzh.game.bean.CardBean.CardType;
import com.fzh.game.constant.Flagconstant;
import com.fzh.game.ershi.R;
import com.fzh.game.picture.AnimateDrawable;
import com.fzh.game.picture.CardDrawable;
import com.fzh.game.picture.CiclerDrawable;
import com.fzh.game.tool.UtilTool;

/**
 * 游戏视图
 */
public class Game24View extends View {

    private static final String TAG = "fzh24m";

    public static final int GAME_OVER = 0x0001;
    public static final int CLOSE_ANSWER = 0x0003;

    // 无效值
    public static final int ISNULL_BEI = -30000;
    // ���ܱ����
    public static final int CANNOT_DIV = -30001;

    // poke的高和宽
    private static int POKE_WIDTH = 200;
    private static int POKE_HEIGHT = 300;

    // 运算符的高和宽
    private static final int SIGN_WIDTH = 90;
    private static final int SIGN_HEIGHT = 90;
    // 局点半径
    private static final int CIRCLE_RADIAUS = 18;

    // 视图的整个大小
    private int mWidth = 0;
    private int mHeight = 0;

    private Paint mPaint = null;

    private int paddingLeft = 0;
    private int paddingTop = 0;

    private volatile boolean isAnmation = false;
    // 所有位置记录对像
    private CardBean[] beans = new CardBean[13];
    // 正在拖动的卡片
    private CardBean moveBean = null;
    private int srcIndex = -1;
    private int desIndex = -1;

    // poke占位图
    private CardDrawable whiteSpace;

    // 是否正在拖动
    private volatile boolean isDrag = false;

    private int[] picRes = null;
    private int[] numbers = null;

    private ArrayList<Integer> pokes = new ArrayList<Integer>();

    private int beginGameTime = 0;

    private OnRectClickListener rectListener;

    private boolean touchable = true;

    // 局点
    private CiclerDrawable[] cicer = new CiclerDrawable[13];

    // 背景图
    private Drawable backGround;

    // 记录游戏当前状态
    private PlayStatus playStatus = PlayStatus.WASH_MODE;

    public void setTouchable(boolean flag) {
        touchable = flag;
    }

    public void again() {
        flushAllRect();
        // 洗牌
        UtilTool.washPoke(pokes);
        calentGameCards(0);
        invalidate();
    }

    public int[] getPic() {
        int[] pics = new int[5];
        if (playStatus == PlayStatus.WASH_MODE) {
            for (int i = 0; i < picRes.length; i++)
                pics[i] = picRes[i];
            pics[4] = 0;
        } else {
            for (int i = 0; i < picRes.length; i++)
                pics[i] = numbers[i];
            pics[4] = 1;
        }
        return pics;
    }

    public int[] getFourNumber() {
        return numbers;
    }

    /**
     * 设置相关poke值
     *
     * @param nums
     */
    public void setPics(int nums[]) {
        playStatus = PlayStatus.QUESTION_MODE;
        flushAllRect();
        if (numbers == null)
            numbers = new int[4];
        // ��ʼ���������������Ŀ��
        for (int i = 0; i < 4; i++) {
            numbers[i] = nums[i];
            beans[i].setEmpty(false, new CardDrawable(getContext(),
                    R.drawable.answer, nums[i]), nums[i]);
            beans[i].dr.reSize(POKE_WIDTH, POKE_HEIGHT);
        }
        invalidate();
    }

    /**
     * 初始化高度和宽度
     * 相关按钮
     */
    private void initData() {

        // 计算poke大小
        POKE_WIDTH = (mWidth - getPaddingLeft() - getPaddingRight()) / 5;
        POKE_HEIGHT = (POKE_WIDTH * 3) / 2;
        whiteSpace.reSize(POKE_WIDTH, POKE_HEIGHT);

        Log.d(TAG, "initData: [" + mWidth + "," + mHeight + "], [" + POKE_WIDTH + ", " + POKE_HEIGHT + "]");

        // poke边距
        int fourLRpadding = (mWidth - POKE_WIDTH * 4) / 5;
        int calendLRpadding = (mWidth - POKE_WIDTH * 3 - SIGN_WIDTH * 2) / 6;
        int signLRpadding = (mWidth - SIGN_WIDTH * 4) / 5;
        int circleLRpadding = (mWidth - CIRCLE_RADIAUS * 2 * 13) / 14;

        // 底部开始位
        final int bottomPadding = 160;
        // 操作符高度
        final int opH = 45;

        // 初始化四张poke
        for (int i = 0; i < 4; i++) {
            beans[i] = new CardBean(whiteSpace);
            beans[i].rect = new Rect(
                    fourLRpadding * (i + 1) + POKE_WIDTH * i,
                    20,
                    (fourLRpadding + POKE_WIDTH) * (i + 1),
                    20 + POKE_HEIGHT);
            beans[i].dr.reSize(POKE_WIDTH, POKE_HEIGHT);
        }

        // 运算三个牌位
        for (int i = 0; i < 3; i++) {
            beans[i + 4] = new CardBean(whiteSpace);
            beans[i + 4].rect = new Rect(
                    calendLRpadding + POKE_WIDTH * i + (SIGN_WIDTH + calendLRpadding * 2) * i,
                    mHeight - bottomPadding - POKE_HEIGHT,
                    calendLRpadding + POKE_WIDTH * (i + 1) + (SIGN_WIDTH + calendLRpadding * 2) * i,
                    mHeight - bottomPadding);
            beans[i + 4].dr.reSize(POKE_WIDTH, POKE_HEIGHT);
        }

        // 当前运算符位
        beans[7] = new CardBean(new CardDrawable(getContext(), R.drawable.add));
        beans[7].canMove = false;
        beans[7].isEmpty = false;
        beans[7].type = CardType.PLUG;
        beans[7].mValue = 0;
        beans[7].rect = new Rect(
                calendLRpadding * 2 + POKE_WIDTH,
                mHeight - bottomPadding - ((POKE_HEIGHT + opH) / 2),
                calendLRpadding * 2 + POKE_WIDTH + SIGN_WIDTH,
                mHeight - bottomPadding- ((POKE_HEIGHT + opH) / 2) + opH);

        // 等号位
        beans[8] = new CardBean(new CardDrawable(getContext(), R.drawable.equal));
        beans[8].canMove = false;
        beans[8].isEmpty = false;
        beans[8].type = CardType.EQUAL;
        beans[8].rect = new Rect(
                calendLRpadding * 4 + POKE_WIDTH * 2 + SIGN_WIDTH,
                mHeight - bottomPadding - ((POKE_HEIGHT + opH) / 2),
                calendLRpadding * 4 + POKE_WIDTH * 2 + SIGN_WIDTH * 2,
                mHeight - bottomPadding- ((POKE_HEIGHT + opH) / 2) + opH);

        // 四个运算符
        for (int i = 0; i < 4; i++) {
            beans[i + 9] = new CardBean(new CardDrawable(getContext(),Flagconstant.signIds[i]));
            beans[i + 9].canMove = false;
            beans[i + 9].isEmpty = false;
            beans[i + 9].type = CardType.BUTTON;
            beans[i + 9].rect = new Rect(
                    SIGN_WIDTH * i + signLRpadding * (i + 1),
                    (mHeight - SIGN_HEIGHT)/2 ,
                    SIGN_WIDTH * (i + 1) + signLRpadding * (i + 1),
                    (mHeight - SIGN_HEIGHT)/2 + SIGN_HEIGHT);

            beans[i + 9].mValue = i;
        }

        // 13个局点
        for (int i = 0; i < cicer.length; i++) {
            cicer[i] = new CiclerDrawable(Color.BLACK, CIRCLE_RADIAUS,
                    i + 1,
                    circleLRpadding * (i + 1) + 12 * i + CIRCLE_RADIAUS,
                    mHeight - 80);
        }
    }

    /**
     * 重置相关值
     */
    private void resetGameCards() {
        if (playStatus == PlayStatus.WASH_MODE) {
            if (picRes == null) {
                numbers = null;
                if (rectListener != null) {
                    rectListener.onRectClick(GAME_OVER);
                }
                return;
            }
            for (int i = 0; i < 4; i++) {
                beans[i].setEmpty(false, new CardDrawable(getContext(),
                        Flagconstant.picIds[picRes[i]]), picRes[i] / 4 + 1);
                beans[i].dr.reSize(POKE_WIDTH, POKE_HEIGHT);
            }
        } else {
            if (numbers == null) {
                picRes = null;
                if (rectListener != null) {
                    rectListener.onRectClick(GAME_OVER);
                }
                return;
            }
            for (int i = 0; i < 4; i++) {
                beans[i].setEmpty(false, new CardDrawable(getContext(),
                        R.drawable.answer, numbers[i]), numbers[i]);
                beans[i].dr.reSize(POKE_WIDTH, POKE_HEIGHT);
            }

        }

    }

    /**
     * 获取指定位置开始的poker
     * @param begin
     */
    private void calentGameCards(int begin) {
        playStatus = PlayStatus.WASH_MODE;
        beginGameTime = begin;
        picRes = UtilTool.getResIdforCards(pokes, begin, 4);
        if (picRes == null) {
            numbers = null;
            if (rectListener != null) {
                rectListener.onRectClick(GAME_OVER);
            }
            return;
        }
        // ��� ��ͼƬ��ֵ
        numbers = new int[picRes.length];
        for (int i = 0; i < picRes.length; i++)
            numbers[i] = picRes[i] / 4 + 1;

        for (int i = 0; i < 4; i++) {
            beans[i].setEmpty(false, new CardDrawable(getContext(),
                    Flagconstant.picIds[picRes[i]]), picRes[i] / 4 + 1);
            beans[i].dr.reSize(POKE_WIDTH, POKE_HEIGHT);
        }
    }

    public Game24View(Context context) {
        this(context, null);
    }

    public Game24View(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 背景图
        backGround = getContext().getResources()
                .getDrawable(R.drawable.desk_bg);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        for (int i = 0; i < Flagconstant.picIds.length; i++) {
            pokes.add(i);
        }
        // 洗牌
        UtilTool.washPoke(pokes);
        whiteSpace = new CardDrawable(getContext(), R.drawable.black_card_0);
    }

    public void setOnRectClickListener(OnRectClickListener listener) {
        rectListener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getWidth();
        mHeight = getHeight();
        backGround.setBounds(0, 0, mWidth, mHeight);
        initData();
        calentGameCards(0);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (isAnmation && mDrawable != null && an != null && an.hasEnded()) {
            isAnmation = false;
            endAnimation(AnimationStatus.MOVE_DES);
        }
        drawCards(canvas);
        drawMoveCard(canvas);
        drawAnimation(canvas);
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawBackground(canvas);
        drawCicler(canvas);
    }

    public void drawAnimation(Canvas canvas) {
        if (isAnmation && mDrawable != null && an != null) {
            mDrawable.draw(canvas);
            invalidate();
        }
    }

    private void drawBackground(Canvas canvas) {
        if (backGround != null)
            backGround.draw(canvas);
    }

    private void drawCards(Canvas canvas) {
        for (int i = 0; i < beans.length; i++) {
            if (beans[i].dr != null && i != 17)
                beans[i].drawCard(canvas, mPaint);
        }
    }

    /**
     * 画局点
     * @param canvas
     */
    private void drawCicler(Canvas canvas) {
        for (int i = 0; i < cicer.length; i++) {
            if (i * 4 <= beginGameTime) {
                cicer[i].draw(canvas, Color.BLUE);
            } else {
                cicer[i].draw(canvas, Color.BLACK);
            }
        }
    }

    /**
     * 绘制移动的card
     * @param canvas
     */
    private void drawMoveCard(Canvas canvas) {
        if (moveBean == null)
            return;
        if (moveBean.dr != null)
            moveBean.drawCard(canvas, mPaint);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (isAnmation || !touchable)
            return super.onTouchEvent(event);
        int pointX = (int) event.getX();
        int pointY = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                hindDownRect(pointX, pointY);
                if (srcIndex > CardBean.NO_ID && srcIndex < 7) {
                    onStartDrag(pointX, pointY);
                } else if (clickDownSignRect(pointX, pointY)) {
                    calendResult();
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (isDrag) {
                    onDraging(pointX, pointY);
                }
                break;

            default:
                if (srcIndex > CardBean.NO_ID && srcIndex < beans.length) {
                    onStopDrag(pointX, pointY);
                }
                invalidate();
                break;
        }
        return true;
    }

    /**
     * 清空所有状态
     */
    private void flushAllRect() {
        for (int i = 0; i < 4; i++) {
            beans[i].mValue = CardBean.NO_ID;
            beans[i].canMove = true;
            beans[i].isEmpty = true;
            beans[i].dr = whiteSpace;
            beans[i].dr.reSize(POKE_WIDTH, POKE_HEIGHT);
        }
        for (int i = 0; i < 3; i++) {
            beans[i + 4].mValue = CardBean.NO_ID;
            beans[i + 4].canMove = true;
            beans[i + 4].isEmpty = true;
            beans[i + 4].dr = whiteSpace;
            beans[i + 4].dr.reSize(POKE_WIDTH, POKE_HEIGHT);
        }

        beans[7].dr = new CardDrawable(getContext(), R.drawable.add);
        beans[7].canMove = false;
        beans[7].type = CardType.PLUG;
        beans[7].isEmpty = false;
        beans[7].mValue = 0;
    }

    /**
     * 点击运算符
     *
     * @param pointX
     * @param pointY
     * @return
     */
    private boolean clickDownSignRect(int pointX, int pointY) {
        // 4个运算符
        for (int i = 9; i < 13; i++) {
            if (beans[i].isHint(pointX, pointY) && !beans[i].isEmpty) {
                beans[7].setEmpty(false, beans[i].dr, beans[i].mValue);
                return true;
            }
        }

        // 中间计算符
        if (beans[7].isHint(pointX, pointY) && !beans[7].isEmpty) {
            int newValue = (beans[7].mValue + 1) % 4;
            beans[7].setEmpty(false, beans[9 + newValue].dr, newValue);
            return true;
        }

        return false;
    }

    /**
     * �����ƶ��ĵ������ڵ�λ��, ֻ�ڰ���ʱ��ʼ����
     *
     * @param pointX
     * @param pointY
     * @return
     */
    private int hindDownRect(int pointX, int pointY) {
        for (int i = 0; i < 7; i++) {
            if (beans[i].isHint(pointX, pointY) && !beans[i].isEmpty
                    && beans[i].canMove) {
                srcIndex = i;
                desIndex = srcIndex;
                break;
            }
        }
        return srcIndex;
    }

    /**
     * ��ʼ�϶�
     *
     * @param pointX
     * @param pointY
     */
    private void onStartDrag(int pointX, int pointY) {
        isDrag = true;
        paddingLeft = pointX - beans[srcIndex].rect.left;
        paddingTop = pointY - beans[srcIndex].rect.top;
        calendStartDragBean(pointX, pointY);
        // 重绘
        invalidate();

    }

    /**
     * �����ƶ��Ķ��񣬲���ʼ��.
     *
     * @param pointX
     * @param pointY
     */
    private void calendStartDragBean(int pointX, int pointY) {
        if (moveBean == null)
            moveBean = new CardBean();
        int left = pointX - paddingLeft;
        int top = pointY - paddingTop;
        int right = left + POKE_WIDTH;
        int bottom = top + POKE_HEIGHT;
        if (moveBean.rect == null)
            moveBean.rect = new Rect(left, top, right, bottom);
        else
            moveBean.rect.set(left, top, right, bottom);
        if (moveBean.dr == null && srcIndex < beans.length
                && srcIndex > CardBean.NO_ID) {
            moveBean
                    .setEmpty(false, beans[srcIndex].dr, beans[srcIndex].mValue);
            beans[srcIndex].setEmpty(true, whiteSpace, CardBean.NO_ID);
        }
    }

    /**
     * 移动
     *
     * @param pointX
     * @param pointY
     */
    private void onDraging(int pointX, int pointY) {
        // 计算移动的card
        calendDragingMoveBean(pointX, pointY);
        // 计算是否hint到某个card位置
        calendDragingDesRect(pointX, pointY);
        invalidate();
    }

    /**
     * 计算移动的card
     *
     * @param pointX
     * @param pointY
     */
    private void calendDragingMoveBean(int pointX, int pointY) {
        if (moveBean == null || moveBean.dr == null)
            return;
        int left = pointX - paddingLeft;
        int top = pointY - paddingTop;
        int right = left + POKE_WIDTH;
        int bottom = top + POKE_HEIGHT;
        if (moveBean.rect == null)
            moveBean.rect = new Rect(left, top, right, bottom);
        else
            moveBean.rect.set(left, top, right, bottom);
    }

    /**
     * 计算是否hint到某个card位置
     *
     * @param pointX
     * @param pointY
     * @return
     */
    private int calendDragingDesRect(int pointX, int pointY) {
        for (int i = 0; i < 6; i++) {
            if (beans[i].isHint(pointX, pointY) && beans[i].isEmpty
                    && beans[i].canMove) {
                desIndex = i;
                break;
            }
        }
        return desIndex;
    }

    /**
     * 拖动结束
     *
     * @param pointX
     * @param pointY
     */
    private void onStopDrag(int pointX, int pointY) {
        startAnimation();
        Log.d(TAG, "endDrag--> px: " + pointX + ", py: " + pointY + ", move: " + moveBean);
        if (moveBean != null) {
            moveBean.dr = null;
        }
    }

    private void calendResult() {
        if (srcIndex == 6 && desIndex != 6) {
            beans[4].setEmpty(true, whiteSpace, CardBean.NO_ID);
            beans[5].setEmpty(true, whiteSpace, CardBean.NO_ID);
        } else if (!beans[4].isEmpty && !beans[5].isEmpty) {
            int value = getresult(beans[4].mValue, beans[5].mValue);
            beans[6].setEmpty(false, new CardDrawable(getContext(),
                    R.drawable.answer, value), value);
            beans[6].dr.reSize(POKE_WIDTH, POKE_HEIGHT);
            beans[6].canMove = value >= 0;
        } else {
            beans[6].setEmpty(false, whiteSpace, CardBean.NO_ID);
        }
    }

    private int getresult(int first, int end) {
        switch (beans[7].mValue) {
            case 0:
                return first + end;

            case 1:
                return first - end;

            case 2:
                return first * end;

            case 3:
                if (end == 0)
                    return ISNULL_BEI;
                else if (first == 0)
                    return 0;
                else if (first % end == 0)
                    return first / end;
                else
                    return CANNOT_DIV;
        }
        return CardBean.NO_ID;
    }

    /**
     * 点击了指定区域回调
     */
    public interface OnRectClickListener {
        void onRectClick(int flag);
    }

    // //////////
    private Animation an = null;
    private AnimateDrawable mDrawable;

    /**
     * 开始移动动画
     */
    private void startAnimation() {
        isAnmation = true;
        an = new TranslateAnimation(moveBean.rect.left,
                beans[desIndex].rect.left, moveBean.rect.top,
                beans[desIndex].rect.top);
        an.setDuration(180);
        an.setRepeatCount(0);
        an.initialize(10, 10, 10, 10);

        mDrawable = new AnimateDrawable(moveBean.dr, an);
        an.startNow();
    }

    /**
     * 结束动画
     * @param anStatus
     */
    private void endAnimation(AnimationStatus anStatus) {
        switch (anStatus) {
            case MOVE_DES:
                if (moveBean != null) {
                    if (desIndex > CardBean.NO_ID && desIndex < beans.length) {
                        beans[desIndex].setEmpty(false, ((CardDrawable) mDrawable
                                .getProxy()), moveBean.mValue);
                    }
                    moveBean.clear();
                }
                calendResult();
                srcIndex = CardBean.NO_ID;
                desIndex = CardBean.NO_ID;
                isDrag = false;
                break;
        }
    }

    private enum AnimationStatus {
        MOVE_DES, FA_POKE, SHOU_POKE
    }

    private enum PlayStatus {
        WASH_MODE, QUESTION_MODE
    }

    /**
     * 上一盘
     */
    public void pre() {
        if (beginGameTime - 4 >= 0) {
            flushAllRect();
            calentGameCards(beginGameTime - 4);
            // 重绘
            invalidate();
        }
    }

    /**
     * 重置
     */
    public void reset() {
        flushAllRect();
        resetGameCards();
        // 重绘
        invalidate();
    }

    /**
     * 下一盘
     */
    public void next() {
        flushAllRect();
        calentGameCards(beginGameTime + 4);
        // 重绘
        invalidate();
    }
}