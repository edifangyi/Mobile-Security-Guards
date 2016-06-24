package com.fangyi.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * 校验某个服务是否运行中
 * Created by FANGYI on 2016/6/6.
 */
public class ServiceStatusUtils {

    /**
     * 校验某个服务是否运行中
     *
     * @param context
     * @param serviceName 要校验服务的全类名
     * @return 如果运行就返回true，否则返回false
     */

    public static boolean inRunningService(Context context, String serviceName) {
        //ActivityManager
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningServiceInfo> serviceInfos = am.getRunningServices(100);//参数的意思是最近的100个服务

        for (ActivityManager.RunningServiceInfo service : serviceInfos) {
            //得到全类名称
            String name = service.service.getClassName();
            if (serviceName.equals(name)) {
                return true;
            }
        }

        return false;
    }
}
