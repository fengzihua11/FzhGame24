package com.fzh.game.staitic.umeng;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import com.fzh.game.MyApplication;
import com.fzh.game.newershi.BuildConfig;
import com.umeng.analytics.MobclickAgent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * 友盟数据统计代理类
 *
 * 1. 友盟相关log的tag是MobclickAgent
 *
 * Created by fengzihua on 17-9-12.
 */

public final class UmengAgent {

    private static final String TAG = "UmengAgent";

    // 当app是release版本，同时手机是user版本时，启用GA
    private static final boolean DEBUG = BuildConfig.DEBUG;

    /**
     * 初始化相关设置
     */
    public static final void init() {
        Log.d(TAG, "init*****************DEBUG: " + DEBUG + ", type: " + Build.TYPE);
        if (DEBUG) {
            // 设置为debug模式, 同时自动设置UncaughtExceptionHandler
            MobclickAgent.setDebugMode(true);
            // 日志不进行加密
            MobclickAgent.enableEncrypt(false);

            String intelID = getDeviceInfo(MyApplication.getContext());
            Log.d(TAG, "intelID****************:" + intelID);
        } else {
            // release版本, 同时自动设置UncaughtExceptionHandler
            MobclickAgent.setDebugMode(false);
            // 加密码日志
            MobclickAgent.enableEncrypt(true);
        }
        // 禁止默认的页面统计方式，这样将不会再自动统计Activity
        MobclickAgent.openActivityDurationTrack(false);
        // 场景类型设置
        // EScenarioType. E_UM_NORMAL  普通统计场景类型
        // EScenarioType. E_UM_GAME     游戏场景类型
        // EScenarioType. E_UM_ANALYTICS_OEM  统计盒子场景类型
        // EScenarioType. E_UM_GAME_OEM       游戏盒子场景类型
        MobclickAgent.setScenarioType(MyApplication.getContext(), MobclickAgent.EScenarioType.E_UM_NORMAL);
    }

    /**
     * 如果开发者调用Process.kill或者System.exit之类的方法杀死进程，
     * 请务必在此之前调用此方法，用来保存统计数据
     * @param context
     */
    public static void onKillProcess(Context context) {
        if(DEBUG) {
            Log.d(TAG, context.getClass().getSimpleName() + "*****************onKillProcess");
        }
        MobclickAgent.onKillProcess(context);
    }

    /**
     * session统计开始, 用于统计应用时长
     *
     * @param context
     */
    public static void onResume(Context context) {
        if(DEBUG) {
            Log.d(TAG, context.getClass().getSimpleName() + "=============>onResume");
        }
        MobclickAgent.onResume(context);
    }

    /**
     * session统计结束
     *
     * @param context
     */
    public static void onPause(Context context) {
        if(DEBUG) {
            Log.d(TAG, context.getClass().getSimpleName() + "<=============onPause");
        }
        MobclickAgent.onPause(context);
    }

    /**
     * 页面统计开始，用于统计页面的跳转
     * @param pageName
     */
    public static void onPageStart(String pageName) {
        if(DEBUG) {
            Log.d(TAG, pageName + "--------->onPageStart");
        }
        MobclickAgent.onPageStart(pageName);
    }

    public static void onPageEnd(String pageName) {
        if(DEBUG) {
            Log.d(TAG, pageName + "<---------onPageEnd");
        }
        MobclickAgent.onPageEnd(pageName);
    }

    /**
     * 上报计数事件
     *
     * @param eventId
     * @param map
     */
    public static void onEvent(String eventId, Map<String, String> map) {
        if(DEBUG) {
            Log.d(TAG, "onEvent(计数): " + cocatStr(eventId, map));
        }
        MobclickAgent.onEvent(MyApplication.getContext(), eventId, map);
    }

    /**
     * 上报计算事件
     * 如：播放音乐和视频等
     * @param eventId
     * @param map
     * @param du
     */
    private static void onEventValue(String eventId, Map<String, String> map, int du) {
        MobclickAgent.onEventValue(MyApplication.getContext(), eventId, map, du);
    }

    /**
     * 从agnes的event对像转换上报
     *
     * @param event
     */
    /*public static void reportEvent(Event event) {
        // agnes中的wigdetId对应友盟的eventId
        String widgetId = replaceUmengStr(event.getWidgetId());
        // eventId是字符串，如"expose", 相当于友盟的eventType
        String eventId = event.getId();
        Map<String, String> props = event.getProps();
        Map<String, String> map = new HashMap();
        map.put("eventType", eventId);
        if (props != null && props.size() > 0) {
            for (Map.Entry<String, String> entry : props.entrySet()) {
                // 属性的key是字符串，value有可能带“.”
                map.put(entry.getKey(), entry.getValue());
            }
        }
        // 上报计数事件
        onEvent(widgetId, map);
    }*/

    /**
     * 友盟的事件中不能有"."
     * 把.替换成_
     */
    public static String replaceUmengStr(String str) {
        if (str != null && str.contains(".")) {
            return str.replaceAll("\\.", "_");
        }
        return str;
    }

    private static String cocatStr(String eventId, Map<String, String> map) {
        StringBuffer sb = new StringBuffer();
        sb.append(eventId);
        sb.append(": ");
        Set<String> set = map.keySet();
        for (String key : set) {
            sb.append("[");
            sb.append(key);
            sb.append(":");
            sb.append(map.get(key));
            sb.append("],");
        }
        return sb.toString();
    }

    /**
     * 集成测试获取对应的设备信息
     *
     * http://mobile.umeng.com/test_devices
     *
     * @param context
     * @return
     */
    private final static String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String device_id = null;
            if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                device_id = tm.getDeviceId();
            }
            String mac = null;
            FileReader fstream = null;
            try {
                fstream = new FileReader("/sys/class/net/wlan0/address");
            } catch (FileNotFoundException e) {
                fstream = new FileReader("/sys/class/net/eth0/address");
            }
            BufferedReader in = null;
            if (fstream != null) {
                try {
                    in = new BufferedReader(fstream, 1024);
                    mac = in.readLine();
                } catch (IOException e) {
                } finally {
                    if (fstream != null) {
                        try {
                            fstream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            json.put("mac", mac);
            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }
            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
            }
            json.put("device_id", device_id);
            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class<?> clazz = Class.forName("android.content.Context");
                Method method = clazz.getMethod("checkSelfPermission", String.class);
                int rest = (Integer) method.invoke(context, permission);
                if (rest == PackageManager.PERMISSION_GRANTED) {
                    result = true;
                } else {
                    result = false;
                }
            } catch (Exception e) {
                result = false;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }
}