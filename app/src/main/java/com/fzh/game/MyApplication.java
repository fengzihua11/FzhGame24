package com.fzh.game;

import android.app.Application;
import android.util.Log;
import com.fzh.game.ershi.BuildConfig;
import com.fzh.game.staitic.umeng.UmengAgent;
//import com.squareup.leakcanary.RefWatcher;
//import android.content.Context;

/**
 *
 */
public class MyApplication extends Application {

    private static final String TAG = "fzh24";

    private static MyApplication mContext;

//    private RefWatcher refWatcher;
//    public static RefWatcher getRefWatcher(Context context) {
//        MyApplication application = (MyApplication) context.getApplicationContext();
//        return application.refWatcher;
//    }

    public static MyApplication getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        /*if (!Constants.MONKEY_TESTING) {
            refWatcher = LeakCanary.install(this);
        }*/

        // 打印日志信息
        Log.i(TAG, "[" + BuildConfig.DEBUG + ", " + BuildConfig.VERSION_NAME + ", " + BuildConfig.VERSION_CODE + "]");
        // 初始化友盟
        UmengAgent.init();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.i(TAG, "on low memory, clear memory.");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.i(TAG, "on trim memory, level is " + level);
    }
}