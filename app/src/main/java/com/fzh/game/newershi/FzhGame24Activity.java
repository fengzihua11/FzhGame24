package com.fzh.game.newershi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.fzh.game.staitic.umeng.UmengAgent;
import com.fzh.game.tool.UtilTool;
import com.fzh.game.view.Game24View;
import com.fzh.game.view.Game24View.OnRectClickListener;
import java.util.HashMap;
import java.util.Map;

/**
 * 游戏主页面
 *
 * @author fengzihua
 * @since 2017.11.07
 */
public class FzhGame24Activity extends AppCompatActivity implements OnRectClickListener {

    private static final String TAG = "game24";
    // 游戏视图
    private Game24View gameView;
    // 答案视图
    //private Game24AnswerView answerView;
    // 设备相关信息
    private DisplayMetrics mDisplay;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. 读取屏幕
        getDisplay();
        setContentView(R.layout.play_screen);

        // 1. 顶部actionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 2. 初始化视图
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UmengAgent.onResume(this);

        reportEvent("open_game");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 1, getResources().getString(
                R.string.game_description_label));

        // 2. 求助
        menu.add(0, 2, 1, getResources().getString(R.string.test_other_people));
        // 3. 出题
        //menu.add(0, 3, 1, getResources().getString(R.string.make_question_demo));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                openGameDescription();
                break;
            case 2:
                getHelpBySms();
                break;
            case 3:
                makeQuestionDailog();
                break;
        }
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_UP) {
            /*if (answerView.isShown()) {
                answerView.setVisibility(View.GONE);
                gameView.setTouchable(true);
                return true;
            } else {
                showCloseAppDailog();
                return true;
            }*/
            showCloseAppDailog();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmengAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 读取当前设置的显示信息
     */
    private void getDisplay() {
        Resources res = getResources();
        mDisplay = res.getDisplayMetrics();
        Log.d(TAG, "--" + mDisplay.toString());
    }

    /**
     * 构造相关视图
     */
    private void initView() {
        gameView = (Game24View) findViewById(R.id.gameView);
        gameView.setOnRectClickListener(this);
        //answerView = (Game24AnswerView) findViewById(R.id.answerView);
        //answerView.setOnRectClickListener(this);
    }

    /**
     * 弹出对话框
     *
     * @deprecated
     */
    private void openAdTip() {
        final AlertDialog dialog = new AlertDialog.Builder(this).setIcon(
                R.mipmap.ic_launcher).setTitle(R.string.ad_miss_icon).setMessage(
                R.string.ad_show_content).setPositiveButton(
                R.string.ad_noshow_miss_icon,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //pushInt(0);
                        dialog.dismiss();
                    }
                }).setNegativeButton(R.string.ad_show_miss_icon, null).create();
        dialog.show();
    }

    /**
     * 显示退出对话框
     */
    private void showCloseAppDailog() {
        AlertDialog dialog = new AlertDialog.Builder(this).setIcon(
                R.mipmap.ic_launcher).setTitle(R.string.login_out_title_tip)
                .setPositiveButton(R.string.login_out_sure,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                finish();
                            }
                        }).setNegativeButton(R.string.login_out_cancel, null)
                .create();
        dialog.show();
    }

    /**
     * 再来一局
     */
    private void showGameAgaimDailog() {
        AlertDialog dialog = new AlertDialog.Builder(this).setIcon(
                R.mipmap.ic_launcher).setTitle(R.string.game_over_title_tip)
                .setPositiveButton(R.string.game_over_yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                finish();
                            }
                        }).setNegativeButton(R.string.game_over_again,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                gameView.again();
                            }
                        }).create();
        dialog.show();
    }

    /**
     * 游戏介绍
     */
    private void openGameDescription() {
        Intent intent = new Intent(this, GameDescriptionActivity.class);
        startActivity(intent);
    }

    /**
     * 发送信息获取帮助
     */
    private void getHelpBySms() {
        int ids[] = gameView.getFourNumber();
        if (ids == null) {
            Toast.makeText(this,
                    UtilTool.getString(this, R.string.counting_tip_washing),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        String content = UtilTool.getString(this, R.string.msg_tip_title);
        for (int i = 0; i < ids.length; i++) {
            content += ids[i] + ", ";
        }
        content += UtilTool.getString(this, R.string.msg_tip_tip);
        Uri smsToUri = Uri.parse("smsto:");
        Intent mIntent = new Intent(Intent.ACTION_SENDTO, smsToUri);

        mIntent.putExtra("sms_body", content);
        startActivity(mIntent);
    }

    /**
     * 出题
     */
    private void makeQuestionDailog() {
        View group = ((LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.questions, null);
        final EditText number1 = (EditText) group.findViewById(R.id.number1);
        final EditText number2 = (EditText) group.findViewById(R.id.number2);
        final EditText number3 = (EditText) group.findViewById(R.id.number3);
        final EditText number4 = (EditText) group.findViewById(R.id.number4);
        final AlertDialog dialog = new AlertDialog.Builder(this).setIcon(
                R.mipmap.ic_launcher).setTitle(R.string.make_question_demo).setView(
                group).setNeutralButton(R.string.make_question_ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sendNumber(number1.getText().toString(), number2
                                .getText().toString(), number3.getText()
                                .toString(), number4.getText().toString());
                    }
                }).setNegativeButton(R.string.make_question_cancel, null)
                .create();
        dialog.show();
    }

    private void sendNumber(String num1, String num2, String num3, String num4) {
        int[] numbers = new int[4];
        numbers[0] = getNumber(num1);
        if (numbers[0] == -1) {
            showMsg(R.string.number_wrong_tip);
            return;
        }
        numbers[1] = getNumber(num2);
        if (numbers[1] == -1) {
            showMsg(R.string.number_wrong_tip);
            return;
        }
        numbers[2] = getNumber(num3);
        if (numbers[2] == -1) {
            showMsg(R.string.number_wrong_tip);
            return;
        }
        numbers[3] = getNumber(num4);
        if (numbers[3] == -1) {
            showMsg(R.string.number_wrong_tip);
            return;
        }
        gameView.setPics(numbers);
    }

    public int getNumber(String num) {
        int value = -1;
        try {
            value = Integer.parseInt(num);
        } catch (Exception e) {
        }
        return value;
    }

    /**
     * 显示toast
     * @param resId
     */
    private void showMsg(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }


    /**
     * 上报事件
     *
     * @param eventId
     */
    private void reportEvent(String eventId) {
        Map<String, String> map = new HashMap();
        UmengAgent.onEvent(eventId, map);
    }

    /**
     * 上一题
     */
    public void onPre(View view) {
        if(gameView != null) {
            gameView.pre();
        }
    }

    /**
     * 重置
     */
    public void onReset(View view) {
        if(gameView != null) {
            gameView.reset();
        }
    }

    /**
     * 退出游戏
     */
    public void onExit(View view) {
        showCloseAppDailog();
        reportEvent("exit_game_click");

    }

    /**
     * 显示答案
     *
     * @param view
     */
    public void onAnswer(View view) {
        //answerView.setVisibility(View.VISIBLE);
        //answerView.setPicIds(gameView.getPic());
        Intent intent = new Intent(this, AnswerUI.class);
        int[] numbers = gameView.getPic();
        if(numbers == null || numbers.length < 5) {
            Toast.makeText(this, "请先确认已发牌!", Toast.LENGTH_SHORT).show();
            return;
        }
        intent.putExtra("numbers", numbers);
        startActivity(intent);

        //gameView.setTouchable(false);
        reportEvent("show_answer_click");
    }

    /**
     * 下一题
     *
     * @param view
     */
    public void onNext(View view) {
        if(gameView != null) {
            gameView.next();
        }
    }

    /**
     * 区域点击响应
     * @param flag
     */
    @Override public void onRectClick(int flag) {
        switch (flag) {
            case Game24View.GAME_OVER:
                // 当前局结束
                showGameAgaimDailog();
                break;
        }
    }
}