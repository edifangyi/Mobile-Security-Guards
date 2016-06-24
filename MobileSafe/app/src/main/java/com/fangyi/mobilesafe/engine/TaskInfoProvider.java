package com.fangyi.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.fangyi.mobilesafe.R;
import com.fangyi.mobilesafe.domain.TaskInfo;
import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.jaredrummler.android.processes.models.Statm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FANGYI on 2016/6/17.
 */

public class TaskInfoProvider {

    /**
     * 得到手机所有的运行的进程信息
     *
     * @param context 上下文
     * @return
     */
    public static List<TaskInfo> getAllTaskInfos(Context context) {
        List<TaskInfo> taskInfos = new ArrayList<>();

//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningAppProcessInfo> cccc = AndroidProcesses.getRunningAppProcessInfo(context);
//        PackageManager pm = context.getPackageManager();
//        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : cccc) {
//            TaskInfo taskInfo = new TaskInfo();
//            //包名
//            String packName = runningAppProcessInfo.processName;
//            taskInfo.setPackname(packName);
//            Debug.MemoryInfo memoryInfo = am.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid})[0];//得到过多进程占用的内存
//            //得到应用在内存中占用的内存
//            long meminfosize = memoryInfo.getTotalPrivateDirty() * 1024;
//            taskInfo.setMeninfosize(meminfosize);
//
//            try {
//                //图标
//                Drawable icon = pm.getPackageInfo(packName, 0).applicationInfo.loadIcon(pm);
//                taskInfo.setIcon(icon);
//                //软件名称
//                String name = pm.getPackageInfo(packName, 0).applicationInfo.loadLabel(pm).toString();
//                taskInfo.setName(name);
//
//                int flag = pm.getPackageInfo(packName, 0).applicationInfo.flags;
//                if ((flag & ApplicationInfo.FLAG_SYSTEM) == 0) {
//                    //用户进程
//                    taskInfo.setUser(true);
//                } else {
//                    //系统进程
//                    taskInfo.setUser(false);
//                }
//
//            } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
//            }
//
//            taskInfos.add(taskInfo);
//        }


        List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();
        PackageManager pm = context.getPackageManager();

        for (AndroidAppProcess process : processes) {
            TaskInfo taskInfo = new TaskInfo();
            try {
                Statm statm = process.statm();

                //包名
                String packName = process.getPackageName();
                taskInfo.setPackname(packName);

                //得到应用在内存中占用的内存
                long totalSizeOfProcess = statm.getSize();
                long residentSetSize = statm.getResidentSetSize();
                taskInfo.setMeninfosize(residentSetSize);

                PackageInfo packageInfo = process.getPackageInfo(context, 0);

                //图标
                Drawable drawable = packageInfo.applicationInfo.loadIcon(pm);
                taskInfo.setIcon(drawable);
                //软件名称
                String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
                taskInfo.setName(appName);

                int flag = packageInfo.applicationInfo.flags;
                if ((flag & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    //用户进程
                    taskInfo.setUser(true);
                } else {
                    //系统进程
                    taskInfo.setUser(false);
                }

            } catch (Exception e) {
                e.printStackTrace();
                taskInfo.setName(process.getPackageName());
                taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_launcher));
            }

            taskInfos.add(taskInfo);
        }


        /**
         * 5.1以下夏天使用
         */
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
//        PackageManager pm = context.getPackageManager();
//        for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
//            TaskInfo taskInfo = new TaskInfo();
//
//            //包名
//            String packName = processInfo.processName;
//            taskInfo.setPackname(packName);
//            Debug.MemoryInfo memoryInfo = am.getProcessMemoryInfo(new int[]{processInfo.pid})[0];//得到过多进程占用的内存
//            //得到应用在内存中占用的内存
//            long meminfosize = memoryInfo.getTotalPrivateDirty() * 1024;
//            taskInfo.setMeninfosize(meminfosize);
//
//            try {
//                //图标
//                Drawable icon = pm.getPackageInfo(packName, 0).applicationInfo.loadIcon(pm);
//                taskInfo.setIcon(icon);
//                //软件名称
//                String name = pm.getPackageInfo(packName, 0).applicationInfo.loadLabel(pm).toString();
//                taskInfo.setName(name);
//
//                int flag = pm.getPackageInfo(packName, 0).applicationInfo.flags;
//                if ((flag & ApplicationInfo.FLAG_SYSTEM) == 0) {
//                    //用户进程
//                    taskInfo.setUser(true);
//                } else {
//                    //系统进程
//                    taskInfo.setUser(false);
//                }
//
//            } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
//                taskInfo.setName(packName);
//                taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_launcher));
//            }
//
//            taskInfos.add(taskInfo);
//        }


//
        return taskInfos;
    }

}
