package com.fzh.game.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import com.fzh.game.bean.CardBean;
import com.fzh.game.bean.CardBean.CardType;
import com.fzh.game.constant.Flagconstant;
import com.fzh.game.newershi.R;
import com.fzh.game.picture.CardDrawable;
import com.fzh.game.tool.NNumCalculateToM;
import com.fzh.game.tool.UtilTool;

/**
 * 游戏视图
 *
 * @author fengzihua
 * @since 2017.11.07
 */
public class Game24AnswerView extends View {

    private static final String TAG = "answerUI";

    private static  int POKE_WIDTH = 200;
    private static  int POKE_HEIGHT = 300;
    private static  int SIGN_WIDTH = 90;
    private static  int SIGN_HEIGHT = 90;

    private CardBean[] beans = new CardBean[15];
    private NNumCalculateToM tool;

    private int mWidth = 0;
    private int mHeight = 0;

    private Paint mPaint = null;

    // 传入的图片id或是数字
    private int[] mIds = new int[5];
    private String[] answerStr = null;
    private Paint tPaint = null;

    private CardDrawable whiteSpace;

    private String tip = "";

    private Drawable backGround;

    private boolean willShow = false;

    public Game24AnswerView(Context context) {
        this(context, null);
    }

    public Game24AnswerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        tip = UtilTool.getString(context, R.string.counting_tip_cal);
        backGround = getContext().getResources().getDrawable(
                R.drawable.answer_bg);
        initPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(),
                widthMeasureSpec), 960);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getWidth();
        mHeight = getHeight();
        backGround.setBounds(0, 0, mWidth, mHeight);
        // 初始化图片大小
        initData();
        fillPics();
        Log.d(TAG, "onSizeChanged: [" + mWidth + "," + mHeight + "], [" + POKE_WIDTH + ", " + POKE_HEIGHT + "]");
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawCards(canvas);
        drawTip(canvas);
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawBackground(canvas);
    }

    /**
     * 绘制背景图
     * @param canvas
     */
    private void drawBackground(Canvas canvas) {
        if (backGround != null)
            backGround.draw(canvas);
    }

    /**
     * 绘制卡片
     * @param canvas
     */
    private void drawCards(Canvas canvas) {
        for (int i = 0; i < beans.length; i++) {
            if (beans[i].dr != null)
                beans[i].drawCard(canvas, mPaint);
        }
    }

    /**
     * 绘制提示语
     * @param canvas
     */
    private void drawTip(Canvas canvas) {
        if (tip == null || tip.equals(""))
            return;
        canvas.drawText(tip, mWidth / 2, (mHeight + 30) / 2, tPaint);
    }

    private void initPaint() {
        if (mPaint == null)
            mPaint = new Paint();
        mPaint.setAntiAlias(true);
        if (tPaint == null)
            tPaint = new Paint();
        tPaint.setTextSize(48);
        tPaint.setColor(Color.RED);
        tPaint.setAntiAlias(true);
        tPaint.setShadowLayer(2, 3.0f, 2.0f, Color.GRAY);
        tPaint.setTextAlign(Align.CENTER);
    }

    /**
     * 根据相应数据，计算答案
     */
    private class CountRunnalbe implements Runnable {

        public void run() {
            if (tool == null)
                tool = new NNumCalculateToM();
            int[] numbers = new int[4];
            if (mIds[4] == 0) {
                // 传入的是图片的id
                for (int i = 0; i < numbers.length; i++) {
                    numbers[i] = mIds[i] / 4 + 1;
                }
            } else {
                // 传入的是数字
                for (int i = 0; i < numbers.length; i++) {
                    numbers[i] = mIds[i];
                }
            }

            // 打印出图片转换的数据
            StringBuilder sb = new StringBuilder();
            for(int number : numbers) {
                sb.append(number);
                sb.append("--");
            }
            Log.d(TAG, "对应数字: " + sb.toString());

            // 获取答案
            answerStr = tool.getAnswerString(numbers);
            freshView(mIds);
            postInvalidate();
        }
    }

    public void freshView(int ids[]) {
        // 1. 无答案
        if (answerStr == null || answerStr.length < 3) {
            tip = UtilTool.getString(getContext(), R.string.counting_tip_no);
            Log.i(TAG, "无答案...");
            return;
        }

        // 2. 打印答案
        for(String number : answerStr) {
            Log.d(TAG, "答案: " + number);
        }

        // 2. 有答案，需要进行字符串转换
        tip = "";
        String[] everyValue = UtilTool.getEveryStr(answerStr);

        StringBuilder sb = new StringBuilder();
        for(String number : everyValue) {
            sb.append(number);
            sb.append(", ");
        }
        Log.d(TAG, "转换后: " + sb.toString() + "[" + (everyValue == null ? "null" : everyValue.length) + "]");

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++) {
                switch (j) {
                    case 1:
                        // 运算符
                        getFlagpic(everyValue[1 + i * 5], 1 + i * 5);
                        break;

                    case 3:
                        // 等于告符
                        getFlagpic(everyValue[3 + i * 5], 3 + i * 5);
                        break;

                    case 4:
                        // 两个数字计算结果卡片
                        getMakeNewpic(everyValue[4 + i * 5], 4 + i * 5);
                        break;

                    default:
                        // 传入的数字或卡片
                        getPicFromFourCard(everyValue[j + i * 5], j + i * 5, ids);
                        break;
                }
            }
        }
    }

    public void getFlagpic(String flag, int index) {
        if (flag == null)
            return;
        boolean isEmpty = false;
        int value = -1;
        int resId = -1;
        Log.d(TAG, "getFlagpic: " + flag + "[" + index + "]");
        if (flag.equals("+")) {
            value = 0;
            resId = R.drawable.add;
        } else if (flag.equals("-")) {
            value = 1;
            resId = R.drawable.sub;
        } else if (flag.equals("*")) {
            value = 2;
            resId = R.drawable.multiply;
        } else if (flag.equals("/")) {
            value = 3;
            resId = R.drawable.division;
        } else if (flag.equals("=")) {
            isEmpty = true;
            resId = R.drawable.equal;
        }
        beans[index].setEmpty(isEmpty, new CardDrawable(getContext(), resId),
                value);
    }

    /**
     * 生成新的card
     *
     * @param flag
     * @param index
     */
    public void getMakeNewpic(String flag, int index) {
        int value = UtilTool.getIntByStr(flag, 0);
        Log.d(TAG, "getMakeNewpic: " + flag + "[" + index + "]");
        beans[index].setEmpty(false, new CardDrawable(getContext(),
                R.drawable.answer, value), value);
        beans[index].dr.reSize(POKE_WIDTH, POKE_HEIGHT);
    }
    /**
     * 获取图片
     *
     * @param valueStr
     * @param index
     * @param ids 对应卡片的id或者数字
     */
    public void getPicFromFourCard(String valueStr, int index, int[] ids) {
        int value = UtilTool.getIntByStr(valueStr, 0);
        Log.d(TAG, "getPicFromFourCard: " + valueStr + "[" + index + "]" + ", ids: " + ids.length);
        if (value > 13) {
            // 没有比13大的poker
            beans[index].setEmpty(false, new CardDrawable(getContext(),
                    R.drawable.answer, value), value);
            beans[index].dr.reSize(POKE_WIDTH, POKE_HEIGHT);
        } else {
            for (int i = 0; i < ids.length; i++) {
                if (ids[i] > -1 && value == ids[i] / 4 + 1) {
                    Log.d(TAG, "卡片: beans[ " + index + "] = " + beans[index]);
                    beans[index].setEmpty(false, new CardDrawable(getContext(),
                            Flagconstant.picIds[ids[i]]), value);
                    ids[i] = -1;
                    return;
                } else if (i == ids.length - 1) {
                    Log.d(TAG, "数字: beans[ " + index + "] = " + beans[index]);
                    beans[index].setEmpty(false, new CardDrawable(getContext(),
                            R.drawable.answer, value), value);
                    beans[index].dr.reSize(POKE_WIDTH, POKE_HEIGHT);
                }
            }
        }
    }

    /**
     * 设置图片ids
     * @param ids
     */
    public void setPicIds(final int[] ids) {
        for (int i = 0; i < mIds.length && i < ids.length; i++) {
            mIds[i] = ids[i];
        }
        fillPics();
    }

    private void fillPics() {
        if(willShow) {
            new Thread(new CountRunnalbe()).start();
        }
        willShow = true;
    }

    /**
     * 初始答案
     */
    private void initData() {
        whiteSpace = new CardDrawable(getContext(), R.drawable.black_card_0);

        POKE_WIDTH = (mWidth - getPaddingLeft() - getPaddingRight()) / 5;
        POKE_HEIGHT = (POKE_WIDTH * 3) / 2;
        whiteSpace.reSize(POKE_WIDTH, POKE_HEIGHT);

        // 竖直方向的padding
        int heightPadding = (mHeight - (POKE_HEIGHT * 3)) / 4;
        int singleheightPadding = (POKE_HEIGHT - SIGN_HEIGHT) / 2;

        int calendLRpadding = (mWidth - POKE_WIDTH * 3 - SIGN_WIDTH * 2) / 6;

        Log.d(TAG, "initData, width: " + POKE_WIDTH + ", height: " + POKE_HEIGHT + ", padding: " + calendLRpadding);
        // 第一排答案
        for (int i = 0; i < 5; i++) {
            switch (i) {
                case 1:
                    beans[i] = new CardBean(whiteSpace);
                    beans[i].type = CardType.PLUG;
                    beans[i].canMove = false;
                    beans[i].rect = new Rect(
                            calendLRpadding + POKE_WIDTH + calendLRpadding,
                            heightPadding + singleheightPadding,
                            calendLRpadding + POKE_WIDTH + calendLRpadding + SIGN_WIDTH,
                            heightPadding + singleheightPadding + SIGN_HEIGHT);
                    break;

                case 3:
                    beans[i] = new CardBean(whiteSpace);
                    beans[i].type = CardType.EQUAL;
                    beans[i].canMove = false;
                    beans[i].rect = new Rect(
                            calendLRpadding * 4 + POKE_WIDTH * 2 + SIGN_WIDTH,
                            heightPadding + singleheightPadding,
                            calendLRpadding * 4 + POKE_WIDTH * 2 + SIGN_WIDTH * 2,
                            heightPadding + singleheightPadding + SIGN_HEIGHT);
                    break;

                default:
                    beans[i] = new CardBean(whiteSpace);
                    beans[i].canMove = false;
                    beans[i].rect = new Rect(
                            calendLRpadding + POKE_WIDTH * (i / 2) + (SIGN_WIDTH + calendLRpadding * 2) * (i / 2),
                            heightPadding,
                            calendLRpadding + POKE_WIDTH * ((i / 2) + 1) + (SIGN_WIDTH + calendLRpadding * 2) * (i / 2),
                            heightPadding + POKE_HEIGHT);
                    break;
            }
        }

        // 第二排答案
        for (int i = 0; i < 5; i++) {
            switch (i) {
                case 1:
                    beans[i + 5] = new CardBean(whiteSpace);
                    beans[i + 5].type = CardType.PLUG;
                    beans[i + 5].rect = new Rect(
                            calendLRpadding + POKE_WIDTH + calendLRpadding,
                            heightPadding + POKE_HEIGHT + heightPadding + singleheightPadding,
                            calendLRpadding + POKE_WIDTH + calendLRpadding + SIGN_WIDTH,
                            heightPadding + POKE_HEIGHT + heightPadding + singleheightPadding + SIGN_HEIGHT);
                    break;
                case 3:
                    beans[i + 5] = new CardBean(whiteSpace);
                    beans[i + 5].type = CardType.EQUAL;
                    beans[i + 5].rect = new Rect(
                            calendLRpadding * 4 + POKE_WIDTH * 2 + SIGN_WIDTH,
                            heightPadding + POKE_HEIGHT + heightPadding + singleheightPadding,
                            calendLRpadding * 4 + POKE_WIDTH * 2 + SIGN_WIDTH * 2,
                            heightPadding + POKE_HEIGHT + heightPadding + singleheightPadding + SIGN_HEIGHT);
                    break;
                default:
                    beans[i + 5] = new CardBean(whiteSpace);
                    beans[i + 5].canMove = false;
                    beans[i + 5].rect = new Rect(
                            calendLRpadding + POKE_WIDTH * (i / 2) + (SIGN_WIDTH + calendLRpadding * 2) * (i / 2),
                            heightPadding + (heightPadding + POKE_HEIGHT),
                            calendLRpadding + POKE_WIDTH * ((i / 2) + 1) + (SIGN_WIDTH + calendLRpadding * 2) * (i / 2),
                            (heightPadding + POKE_HEIGHT) * 2);
                    break;
            }
        }

        // 第三排答案
        for (int i = 0; i < 5; i++) {
            switch (i) {
                case 1:
                    beans[i + 10] = new CardBean(whiteSpace);
                    beans[i + 10].type = CardType.PLUG;
                    beans[i + 10].rect = new Rect(
                            calendLRpadding + POKE_WIDTH + calendLRpadding,
                            heightPadding + POKE_HEIGHT + heightPadding + POKE_HEIGHT + heightPadding + singleheightPadding,
                            calendLRpadding + POKE_WIDTH + calendLRpadding + SIGN_WIDTH,
                            heightPadding + POKE_HEIGHT + heightPadding + POKE_HEIGHT + heightPadding + singleheightPadding + SIGN_HEIGHT);
                    break;
                case 3:
                    beans[i + 10] = new CardBean(whiteSpace);
                    beans[i + 10].type = CardType.EQUAL;
                    beans[i + 10].rect = new Rect(
                            calendLRpadding * 4 + POKE_WIDTH * 2 + SIGN_WIDTH,
                            heightPadding + POKE_HEIGHT + heightPadding + POKE_HEIGHT + heightPadding + singleheightPadding,
                            calendLRpadding * 4 + POKE_WIDTH * 2 + SIGN_WIDTH * 2,
                            heightPadding + POKE_HEIGHT + heightPadding + POKE_HEIGHT + heightPadding + singleheightPadding + SIGN_HEIGHT);
                    break;
                default:
                    beans[i + 10] = new CardBean(whiteSpace);
                    beans[i + 10].canMove = false;
                    beans[i + 10].rect = new Rect(
                            calendLRpadding + POKE_WIDTH * (i / 2) + (SIGN_WIDTH + calendLRpadding * 2) * (i / 2),
                            heightPadding + (heightPadding + POKE_HEIGHT) * 2,
                            calendLRpadding + POKE_WIDTH * ((i / 2) + 1) + (SIGN_WIDTH + calendLRpadding * 2) * (i / 2),
                            (heightPadding + POKE_HEIGHT) * 3);
                    break;
            }
        }
    }

    /**
     * 动画显示答案
     *
     *
     * @deprecated
     * @param visibility
     */
    public void setVisibility(int visibility) {
        if (getVisibility() == visibility)
            return;
        TranslateAnimation tan = null;
        if (visibility == View.VISIBLE) {
            tip = UtilTool.getString(getContext(), R.string.counting_tip_cal);
            tan = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f);
            tan.setDuration(450);
            // tan.setInterpolator(new AccelerateInterpolator());
            tan.setInterpolator(new DecelerateInterpolator());
            setAnimation(tan);
        } else {
            tan = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, -1.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f);
            tan.setDuration(450);
            tan.setInterpolator(new AccelerateInterpolator());
            tan.setAnimationListener(myListener);
            setAnimation(tan);
        }
        super.setVisibility(visibility);
    }

    public AnimationListener myListener = new AnimationListener() {
        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationRepeat(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            new Thread(new FlushRunnable()).start();
        }
    };

    public void flushView() {
        for (int i = 0; i < 15; i++) {
            beans[i].setEmpty(false, whiteSpace, CardBean.NO_ID);
        }
    }

    private class FlushRunnable implements Runnable {
        public void run() {
            flushView();
        }
    }
}