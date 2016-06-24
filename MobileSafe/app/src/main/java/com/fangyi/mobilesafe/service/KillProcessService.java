package com.fangyi.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.socks.library.KLog;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by FANGYI on 2016/6/19.
 */

public class KillProcessService extends Service {
    private Timer mTimer;//定时器
    private TimerTask mTask;

    private ScreenReceiver receiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //定时器
        mTimer = new Timer();
        mTask = new TimerTask() {
            @Override
            public void run() {

            }
        };

        mTimer.schedule(mTask, 2000, 4000);//2秒钟后开始，4秒钟轮循一次

        //监听锁屏事件
        receiver = new ScreenReceiver();
        IntentFilter filter = new IntentFilter();
        //设置监听锁屏
        filter.addAction(Intent.ACTION_SCREEN_OFF);//ACTION_SCREEN_ON 屏幕开启
        registerReceiver(receiver, filter);
    }

    private class ScreenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //杀死后台进程
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

            KLog.e("11111111111111111111111111111111111");
//            List<TaskInfo> taskInfos = TaskInfoProvider.getAllTaskInfos(context);
//
//            int i = 0;
//            List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();
//            for (AndroidAppProcess process : processes) {
//                am.killBackgroundProcesses(process.name);
//                i++;
//                KLog.e("进程  ========   " + i);
//            }
//
//            PackageManager pm = context.getPackageManager();

            //严重bug ----------- 莫名其妙的 在com.fangyi.mobilesafe.activity.taskmanager.TaskManagerSettingActivity 空指针
            int i = 0;
            for (ActivityManager.RunningAppProcessInfo processInfo : am.getRunningAppProcesses()) {
                am.killBackgroundProcesses(processInfo.processName);
                i++;
                KLog.e("进程  ========   " + i);
            }

//            String p = getPackageName();
//            KLog.e("------------------------" + p);
//
//            for (TaskInfo taskInfo : taskInfos) {
//                if (getPackageName().equals(taskInfo.getPackname())){
//                    continue;
//                }
//                am.killBackgroundProcesses(taskInfo.getPackname());
//            }




        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        KLog.e("bbbbbbbbbbbbbbbbbbbbb");
        //取消监听锁屏
        unregisterReceiver(receiver);

        receiver = null;
        mTimer.cancel();
        mTask.cancel();
        mTimer = null;
        mTask =null;

    }
}
