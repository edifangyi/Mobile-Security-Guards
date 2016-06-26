package com.fangyi.mobilesafe.utils;

import android.content.Context;

/**
 * Created by FANGYI on 2016/6/26.
 */

public class DensityUtil {
    /**
     * 根据手机的分辨率从 dip 的单位转换为 px（像素）
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    /**
     * 根据手机的分辨率从 px（像素） 的单位转换为 dip
     */
    public static int ox2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
