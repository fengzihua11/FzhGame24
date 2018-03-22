package com.fzh.game.newershi;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import com.fzh.game.staitic.umeng.UmengAgent;
import com.fzh.game.view.Game24AnswerView;
import java.util.HashMap;
import java.util.Map;

/**
 * 答案页面
 *
 * @author: fengzihua
 * @Time: 2018/3/22 上午10:37
 */

public class AnswerUI extends AppCompatActivity {

    private static final String TAG = "answer";
    // 设备相关信息
    private DisplayMetrics mDisplay;
    // 答案视图
    private Game24AnswerView answerView;
    // 传入数字
    private int[] mNumbers;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_screen);
        // 1. 读取屏幕
        getDisplay();
        // 2. 顶部actionBar
        initActionBar();
        // 3. 初始化视图
        initView();
        // 4. 初始化数字
        initDatas();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UmengAgent.onResume(this);
        reportEvent("open_game");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
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
    }

    private void initActionBar() {
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 构造相关视图
     */
    private void initView() {
        answerView = (Game24AnswerView) findViewById(R.id.answerView);
    }

    private void initDatas() {
        mNumbers = getIntent().getIntArrayExtra("numbers");
        answerView.setPicIds(mNumbers);
        StringBuilder sb = new StringBuilder();
        for(int number : mNumbers) {
            sb.append(number);
            sb.append("--");
        }
        Log.d(TAG, "numbers: " + sb.toString());
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
}