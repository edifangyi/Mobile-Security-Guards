package com.fangyi.mobilesafe.activity.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.fangyi.mobilesafe.R;
import com.fangyi.mobilesafe.service.AddressService;
import com.fangyi.mobilesafe.service.SmsSafeService;
import com.fangyi.mobilesafe.utils.ServiceStatusUtils;
import com.fangyi.mobilesafe.view.SettingClickView;
import com.fangyi.mobilesafe.view.SettingItemView;

/**
 * Created by FANGYI on 2016/5/29.
 */
public class SettingActivity extends AppCompatActivity {

    private SharedPreferences sp;
    //设置自动更新
    private SettingItemView sivUpdate;
    //设置号码归属地显示
    private SettingItemView sivShowAddress;
    private Intent addressIntent;

    //设置归属地显示窗背景
    private SettingClickView scvChangeBg;

    //设置归属地显示框位置
    private SettingClickView scvChangePosition;

    //设置黑名单拦截
    private SettingItemView sivBlacknumber;
    private Intent blacknumberIntent;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        setContentView(R.layout.activity_setting);

        //设置自动更新
        sivUpdate = (SettingItemView) findViewById(R.id.siv_update);
        boolean update = sp.getBoolean("update", true);

        sivUpdate.setChecked(update);

        sivUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                //得到他是否被勾选
                if (sivUpdate.isChecked()) {
                    //变为非勾选
                    sivUpdate.setChecked(false);
//                    sivUpdate.setDescription("当前状态为自动升级已经关闭");
                    editor.putBoolean("update", false);
                } else {
                    //变为勾选
                    sivUpdate.setChecked(true);
//                    sivUpdate.setDescription("当前状态为自动升级已经开启");
                    editor.putBoolean("update", true);
                }
                editor.commit();
            }
        });

        //设置号码归属地显示
        sivShowAddress = (SettingItemView) findViewById(R.id.siv_showaddress);
        addressIntent = new Intent(this, AddressService.class);
        boolean addressService = ServiceStatusUtils.inRunningService(this, "com.fangyi.mobilesafe.service.AddressService");

//        if (addressService) {
//            sivShowAddress.setChecked(true);
//        } else {
//            sivShowAddress.setChecked(false);
//        }
        sivShowAddress.setChecked(addressService);

        sivShowAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sivShowAddress.isChecked()) {
                    //非勾选
                    sivShowAddress.setChecked(false);
                    //关闭服务
                    stopService(addressIntent);
                } else {
                    //勾选
                    sivShowAddress.setChecked(true);
                    //开启服务
                    startService(addressIntent);
                }
            }
        });


        //设置归属地显示框的风格
        scvChangeBg = (SettingClickView) findViewById(R.id.scv_changebg);
        final String items[] = {"粉色", "绿色", "蓝色", "紫色"};
        int which = sp.getInt("which", 0);
        scvChangeBg.setDescription(items[which]);
        scvChangeBg.setTitle("归属地提示框风格");
        scvChangeBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tt = sp.getInt("which", 0);
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("归属地提示框风格");
                builder.setSingleChoiceItems(items, tt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //1.保存
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("which", which);
                        editor.commit();
                        //2.设置
                        scvChangeBg.setDescription(items[which]);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("cancel", null);
                builder.show();
            }
        });


        //设置归属地显示框位置
        scvChangePosition = (SettingClickView) findViewById(R.id.scv_changeposition);
        scvChangePosition.setTitle("归属地提示框位置");
        scvChangePosition.setDescription("设置归属地提示框显示位置");
        scvChangePosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到拖动Activity界面
                Intent intent = new Intent(SettingActivity.this, DragViewActivity.class);
                startActivity(intent);
            }
        });

        //设置黑名单拦截
        sivBlacknumber = (SettingItemView) findViewById(R.id.siv_blacknumber);
        blacknumberIntent = new Intent(this, SmsSafeService.class);
        boolean blacknumberService = ServiceStatusUtils.inRunningService(this, "com.fangyi.mobilesafe.service.SmsSafeService");

        sivBlacknumber.setChecked(blacknumberService);

        sivBlacknumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sivBlacknumber.isChecked()) {
                    //非勾选
                    sivBlacknumber.setChecked(false);
                    //关闭服务
                    stopService(blacknumberIntent);
                } else {
                    //勾选
                    sivBlacknumber.setChecked(true);
                    //开启服务
                    startService(blacknumberIntent);
                }
            }
        });
    }

    //获得焦点
    @Override
    protected void onResume() {
        super.onResume();
        boolean addressService = ServiceStatusUtils.inRunningService(this, "com.fangyi.mobilesafe.service.AddressService");
        sivShowAddress.setChecked(addressService);

        boolean blacknumberService = ServiceStatusUtils.inRunningService(this, "com.fangyi.mobilesafe.service.SmsSafeService");
        sivBlacknumber.setChecked(blacknumberService);
    }
}
