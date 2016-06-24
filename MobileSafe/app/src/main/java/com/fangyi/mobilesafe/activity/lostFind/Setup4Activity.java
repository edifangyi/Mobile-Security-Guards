package com.fangyi.mobilesafe.activity.lostFind;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.fangyi.mobilesafe.R;

/**
 * Created by FANGYI on 2016/5/30.
 */
public class Setup4Activity extends BaseSetupActivity {

    private CheckBox cbSetup4Protectting;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        setContentView(R.layout.activity_lostfind_setup4);
        cbSetup4Protectting = (CheckBox) findViewById(R.id.cb_setup4_protectting);

        boolean protectting = sp.getBoolean("protectting", false);
        if (protectting) {
            //手机防盗已经开启
            cbSetup4Protectting.setText("当前状态：手机防盗已经开启");
        } else {
            //手机防盗已经关闭
            cbSetup4Protectting.setText("当前状态：手机防盗已经关闭");
        }
        cbSetup4Protectting.setChecked(protectting);

        cbSetup4Protectting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("protectting", isChecked);
                editor.commit();
                if (isChecked) {
                    cbSetup4Protectting.setText("当前状态：手机防盗已经开启");
                } else {
                    cbSetup4Protectting.setText("当前状态：手机防盗已经关闭");
                }
            }
        });
    }

    @Override
    public void showNext() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("configed", true);
        editor.commit();
        Intent intent = new Intent(this, LostFindActivity.class);
        startActivity(intent);
        //当前页面关闭
        finish();
        overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
    }

    @Override
    public void showPre() {
        Intent intent = new Intent(this, Setup3Activity.class);
        startActivity(intent);
        //当前页面关闭
        finish();
        overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
    }

}
