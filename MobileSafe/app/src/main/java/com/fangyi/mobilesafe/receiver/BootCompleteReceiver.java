package com.fangyi.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;


/**
 * 监听开机广播
 * Created by FANGYI on 2016/6/2.
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    private SharedPreferences sp;
    private TelephonyManager tm;
    @Override
    public void onReceive(Context context, Intent intent) {
        //1.得到之前的sim卡信息
        sp = context.getSharedPreferences("config", Context.MODE_APPEND);
        if (sp.getBoolean("protectting", false)) {
            String saveSIM = sp.getString("sim", "");
            //2.得到当前手机的sim卡信息
            tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String currentSIM = tm.getSimSerialNumber();
            //3.比较sim卡信息是否一致
            if (saveSIM.equals(currentSIM)) {
                //什么也不用做
            } else {
                //4.如果不一致就发短信给安全号码
                System.out.println("sim卡变更...");
                Toast.makeText(context, "sim卡变更....", Toast.LENGTH_SHORT).show();
                SmsManager.getDefault().sendTextMessage(sp.getString("safenumber", ""), null, "sim changge from o(*￣▽￣*)o", null, null);
            }
        }

    }
}
