package com.fangyi.mobilesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.fangyi.mobilesafe.R;
import com.fangyi.mobilesafe.receiver.MyAppWidget;
import com.fangyi.mobilesafe.utils.SystemInfoUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by FANGYI on 2016/6/20.
 */

public class UpdateAppWidgetService extends Service {
    private AppWidgetManager awm;
    private Timer timer;
    private TimerTask task;
    private UpdateAppWidgetService.ScreenReceiver receiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        //监听锁屏事件
        receiver = new ScreenReceiver();
        IntentFilter filter = new IntentFilter();
        //设置监听锁屏
        filter.addAction(Intent.ACTION_SCREEN_OFF);//ACTION_SCREEN_ON 屏幕开启
        filter.addAction(Intent.ACTION_SCREEN_ON);//屏幕亮了
        registerReceiver(receiver, filter);


        startUpdata();
    }

    /**
     * 定时器 + 远程更新view
     */
    private void startUpdata() {
        awm = AppWidgetManager.getInstance(this);


        timer = new Timer();//定时器
        task = new TimerTask() {
            @Override
            public void run() {
//                System.out.println("开始耗电");

                //激活的组件 - 通讯
                ComponentName componentName = new ComponentName(UpdateAppWidgetService.this, MyAppWidget.class);
                //序列化 更新远程view的布局
                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.my_appwidget);

                //剩余，可用内存
                long availRam = SystemInfoUtils.getAvailRam(UpdateAppWidgetService.this);
                //总内存
                long totalRam = SystemInfoUtils.getTotalRam(UpdateAppWidgetService.this);

                //使用内存 总内存-剩余内存
                long usedRam = totalRam - availRam;
//
//                KLog.e("剩余     ==== " + availRam);
//                KLog.e("总内存   ==== " + totalRam);
//                KLog.e("使用内存 ==== " + usedRam);

                //使用内存
                remoteViews.setTextViewText(R.id.tv_appwidget_used, "RAM Used: " + Formatter.formatFileSize(UpdateAppWidgetService.this, usedRam));
                //剩余内存
                remoteViews.setTextViewText(R.id.tv_appwidget_free, "Free: " + Formatter.formatFileSize(UpdateAppWidgetService.this, availRam));

                //设置进度条，最大值为totalRam, 当前值为usedRam，最后一个参数为true时显示条纹
                remoteViews.setProgressBar(R.id.pb_appwidget, (int) totalRam, (int) usedRam, false);

                //动作 - 广播发出来
                Intent intent = new Intent();
                intent.setAction("com.fangyi.mobilesafe.killprocess");
                //延期意图
                PendingIntent pendingIntent = PendingIntent.getBroadcast(UpdateAppWidgetService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);//通过发送一条广播，程序自己接收广播动作执行清理动作
                //FLAG_UPDATE_CURRENT - 练习响应多次，执行最后一次
                //设置点击事件
                remoteViews.setOnClickPendingIntent(R.id.widget_ala, pendingIntent);

                awm.updateAppWidget(componentName, remoteViews);
            }
        };
        timer.schedule(task, 0, 4000);
    }


    private class ScreenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == Intent.ACTION_SCREEN_OFF) {
                if (timer != null && task != null) {
                    timer.cancel();
                    task.cancel();
                    timer = null;
                    task = null;
                }
            } else if (intent.getAction() == Intent.ACTION_SCREEN_ON) {
                if (timer == null && task == null) {
                    startUpdata();
                }
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消监听锁屏
        unregisterReceiver(receiver);

        //取消定时器工作
        if (timer != null && task != null) {
            timer.cancel();
            task.cancel();
            timer = null;
            task = null;
        }

    }

}
