package com.fangyi.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.fangyi.mobilesafe.domain.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FANGYI on 2016/6/15.
 */

public class AppInfoProvider {


    /**
     * 得到手机里面所有的应用信息
     * @param context
     * @return
     */
    public static List<AppInfo> getAllAppInfos(Context context) {
        List<AppInfo> mAppInfos = new ArrayList<>();
        //包管理器
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : packageInfos) {
            AppInfo appInfo = new AppInfo();
            String packName = packageInfo.packageName;
            appInfo.setPackName(packName);
            Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
            appInfo.setIcon(icon);
            String name = packageInfo.applicationInfo.loadLabel(pm).toString();
            appInfo.setName(name);

            //应用程序的标识，可以是下面任意组合 - 应用程序交答题卡
            int flage = packageInfo.applicationInfo.flags;
            if ((flage & ApplicationInfo.FLAG_SYSTEM) == 0) {
                //用户程序
                appInfo.setUser(true);
            } else {
                //系统程序
                appInfo.setUser(false);
            }

            if ((flage & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
                //内部存储
                appInfo.setRom(true);
            } else {
                //外部存储
                appInfo.setRom(false);
            }

            mAppInfos.add(appInfo);
        }

        return mAppInfos;
    }
}
