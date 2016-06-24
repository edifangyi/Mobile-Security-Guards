package com.fangyi.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.fangyi.mobilesafe.R;
import com.fangyi.mobilesafe.activity.lostFind.LockScreenActivity;
import com.fangyi.mobilesafe.service.GPSService;

/**
 * Created by FANGYI on 2016/6/3.
 */
public class SMSReceiver extends BroadcastReceiver {
    private SharedPreferences sp;

    /**
     * 设备策略管理员
     */
    private DevicePolicyManager dpm;

    @Override
    public void onReceive(Context context, Intent intent) {
        sp = context.getSharedPreferences("config", Context.MODE_APPEND);

        dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        for (Object pdu : pdus) {
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);

            //得到发送者
            String sender = sms.getOriginatingAddress();//获得发送指令的电话号码
            String safenumber = sp.getString("safenumber", "");//获得设置的安全号码
            //得到内容
            String body = sms.getMessageBody();

            //模拟器这么做，真机的话，就不能这样了
            if (sender.equals(safenumber)) {
                if ("#location#".equals(body)) {
                    //得到手机的GPS位置
                    Intent gpsSericeIntent = new Intent(context, GPSService.class);
                    context.startService(gpsSericeIntent);
                    String locationManager = sp.getString("locationManager", "");
                    if (TextUtils.isEmpty(locationManager)) {
                        SmsManager.getDefault().sendTextMessage(sender, null, "getting locationManager ... for YOU", null, null);

                    } else {
                        SmsManager.getDefault().sendTextMessage(sender, null, locationManager, null, null);
                    }

                    abortBroadcast();
                } else if ("#alarm#".equals(body)) {
                    MediaPlayer player = MediaPlayer.create(context, R.raw.jd);
                    player.setVolume(1.0f, 1.0f);
                    player.setLooping(true);//循环播放
                    player.start();
                    //播放报警音乐
                    abortBroadcast();
                } else if ("#wipedate#".equals(body)) {
                    //远程数据销毁
                    ComponentName who = new ComponentName(context, MyAdmin.class);
                    if (dpm.isAdminActive(who)) {
                        dpm.wipeData(0);//手机恢复出厂设置
                        dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);//清除手机sdcard的数据;
                    } else {
                        //定义意图，动作：添加设备管理员
                        openAdmin(context);
                    }
                    abortBroadcast();
                } else if ("#*locksereen#".equals(body)) {
                    //远程锁屏
                    ComponentName who = new ComponentName(context, MyAdmin.class);
                    if (dpm.isAdminActive(who)) {
                        dpm.lockNow();//锁屏
                        dpm.resetPassword("1230", 0);//设置密码

                    } else {
                        //定义意图，动作：添加设备管理员
                        openAdmin(context);
                    }
                    abortBroadcast();
                }
            }
        }
    }

    private void openAdmin(Context context) {
        //定义意图，动作：添加设备管理员
        Intent openAdmin = new Intent(context, LockScreenActivity.class);
        openAdmin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(openAdmin);
    }
}
