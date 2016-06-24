package com.fangyi.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.fangyi.mobilesafe.activity.atools.EnterAppLockWatcDogActivity;
import com.fangyi.mobilesafe.dao.AppLockDao;

import java.util.List;

/**
 * Created by FANGYI on 2016/6/22.
 */

public class WatchDogSerivce extends Service {
    private boolean flag = false;
    private InnerReceiver receiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private ActivityManager am;
    private AppLockDao dao;
    private Intent intent;
    private String stopprotecttingPackname;
    private ScreenReceiver screenReceiver;
    private List<String> packnames;//所有加锁的应用的包名
    private MyContentObserver myContentObserver;

    @Override
    public void onCreate() {
        super.onCreate();
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        dao = new AppLockDao(this);
        packnames = dao.queryAll();
        intent = new Intent(this, EnterAppLockWatcDogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//在服务中启动一个Activity

        //监听停止保护对象
        receiver = new InnerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.fangyi.mobilesafe.stopprotectting");
        registerReceiver(receiver, filter);

        new Thread() {
            @Override
            public void run() {
                //看门狗巡逻 - 最近打开的应用的 - 包名
                flag = true;
                while (flag) {
                    //am.getRunningTasks(1)得到任务栈列表，只装一个；.get(0)把这个唯一的栈取出来
                    ActivityManager.RunningTaskInfo taskInfo = am.getRunningTasks(1).get(0);
                    //栈里面有多个Activity - 只装一个
                    String packname = taskInfo.topActivity.getPackageName();
//                    if (dao.query(packname)) {//修改成在内存中查找，速度提高10呗
                    if (packnames.contains(packname)) {
                        if (packname.equals(stopprotecttingPackname)) {
                            //什么也不做
                        } else {
                            //弹出页面
                            intent.putExtra("packname", packname);
                            startActivity(intent);
                        }
                    }

                    //监听锁屏事件 - 熄屏 给 stopprotecttingPackname = null;
                    screenReceiver = new ScreenReceiver();
                    IntentFilter filter = new IntentFilter();
                    //设置监听锁屏
                    filter.addAction(Intent.ACTION_SCREEN_OFF);//ACTION_SCREEN_ON 屏幕开启
                    registerReceiver(screenReceiver, filter);

                    SystemClock.sleep(5);
                }
            }
        }.start();
        Uri uri = Uri.parse("content://com.fangyi.mobilesafe.dbchange");
        myContentObserver = new MyContentObserver(new Handler());
        getContentResolver().registerContentObserver(uri, true, myContentObserver);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;//让看门狗灵魂必须休息

        //取消注册看门狗
        unregisterReceiver(receiver);
        unregisterReceiver(screenReceiver);
        receiver = null;
        screenReceiver = null;

        //取消注册内容观察者
        getContentResolver().unregisterContentObserver(myContentObserver);
        myContentObserver = null;

    }


    private class InnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            stopprotecttingPackname = intent.getStringExtra("packname");
        }
    }

    private class ScreenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            stopprotecttingPackname = null;
        }
    }

    /**
     * 内容观察者
     */
    private class  MyContentObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            System.out.println("数据发生变化");
            packnames = dao.queryAll();
        }
    }
}
