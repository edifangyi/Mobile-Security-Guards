package com.fangyi.mobilesafe.activity.lostFind;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.fangyi.mobilesafe.receiver.MyAdmin;

public class LockScreenActivity extends AppCompatActivity {
    /**
     * 设备策略管理员
     */
    private DevicePolicyManager dpm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        openAdmin(null);
        finish();

    }

    /**
     * 一键锁屏
     * @param v
     */
    public void lockScreen(View v) {
        ComponentName who = new ComponentName(this, MyAdmin.class);
        if (dpm.isAdminActive(who)) {
            dpm.lockNow();//锁屏
            dpm.resetPassword("1230", 0);//设置密码

//            dpm.wipeData(0);//让手机恢复成出厂设置-远程删除数据
//            dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);//清除手机sdcard的数据;

        } else {
            openAdmin(null);
        }

    }

    /**
     * 开启设备管理员权限
     */
    public void openAdmin(View v) {
        //定义意图，动作：添加设备管理员
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        //激活的组件
        ComponentName mDeviceAdminSample = new ComponentName(this, MyAdmin.class);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
        //激活的说明
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "你激活设备管理员权限，可一键锁屏...");
        startActivity(intent);
    }

    /**
     * 卸载软件
     */
    public void uninstall(View v) {
        //1.把权限干掉
        ComponentName who = new ComponentName(this, MyAdmin.class);
        dpm.removeActiveAdmin(who);

        //2.当成普通应用卸载
        Intent intent = new Intent();
        intent.setAction("android.intent.action.DELETE");
        intent.addCategory("android.intent.category.DELETE");
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }
}
