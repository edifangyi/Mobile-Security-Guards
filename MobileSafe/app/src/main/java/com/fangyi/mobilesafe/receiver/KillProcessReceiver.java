package com.fangyi.mobilesafe.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.socks.library.KLog;

/**
 * Created by FANGYI on 2016/6/21.
 */

public class KillProcessReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //杀死后台进程
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int i = 0;
        for (ActivityManager.RunningAppProcessInfo processInfo : am.getRunningAppProcesses()) {
            am.killBackgroundProcesses(processInfo.processName);
            i++;
            KLog.e("进程  ========   " + i);
        }
    }
}
