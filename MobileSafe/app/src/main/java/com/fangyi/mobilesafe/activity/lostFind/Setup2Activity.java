package com.fangyi.mobilesafe.activity.lostFind;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.fangyi.mobilesafe.R;
import com.fangyi.mobilesafe.view.SettingItemView;

/**
 * Created by FANGYI on 2016/5/30.
 */
public class Setup2Activity extends BaseSetupActivity {
    private SettingItemView sivBindSIM;
    /**
     * 电话服务-读取sim信息，监听来电和挂断电话
     */
    private TelephonyManager tm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lostfind_setup2);
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        sivBindSIM = (SettingItemView) findViewById(R.id.siv_lost_find_setup2_bind_sim);
        String sim = sp.getString("sim", "");
        if (TextUtils.isEmpty(sim)) {
            //没有绑定sim卡
            sivBindSIM.setChecked(false);
        } else {
            //已经绑定sim卡
            sivBindSIM.setChecked(true);
        }
        sivBindSIM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                if (sivBindSIM.isChecked()) {
                    sivBindSIM.setChecked(false);
                    editor.putString("sim", null);
                } else {
                    sivBindSIM.setChecked(true);
                    //读取sim卡的串号
//                tm.getLine1Number();//得到sim卡的电话

                    String sim = tm.getSimSerialNumber();
                    Toast.makeText(Setup2Activity.this, sim, Toast.LENGTH_SHORT).show();
                    editor.putString("sim", sim);
                }
                editor.commit();
            }
        });
    }

    @Override
    public void showNext() {
        String sim = sp.getString("sim", "");
        if (TextUtils.isEmpty(sim)) {
            Toast.makeText(this, "未绑定sim卡", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, Setup3Activity.class);
        startActivity(intent);
        //当前页面关闭
        finish();
        overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
    }

    @Override
    public void showPre() {
        Intent intent = new Intent(this, Setup1Activity.class);
        startActivity(intent);
        //当前页面关闭
        finish();
        overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
    }

}
