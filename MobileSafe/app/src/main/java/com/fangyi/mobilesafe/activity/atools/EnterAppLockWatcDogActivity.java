package com.fangyi.mobilesafe.activity.atools;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fangyi.mobilesafe.R;

/**
 * Created by FANGYI on 2016/6/22.
 */

public class EnterAppLockWatcDogActivity extends AppCompatActivity {
    private Intent intent;
    private String packname;
    private ImageView ivApplockWatchDogIcon;
    private EditText etDialogEnterpwdPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_applockwatcdog_enterpwd);
        ivApplockWatchDogIcon = (ImageView) findViewById(R.id.iv_applockwatchdog_icon);
        etDialogEnterpwdPassword = (EditText) findViewById(R.id.et_dialog_enterpwd_password);
        intent = getIntent();
        packname = intent.getStringExtra("packname");

        PackageManager pm = getPackageManager();
        try {
            Drawable icon = pm.getApplicationInfo(packname, 0).loadIcon(pm);
//            String  name = pm.getApplicationInfo(packname, 0).loadLabel(pm).toString();
            ivApplockWatchDogIcon.setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        //回到桌面
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);

    }

    public void ok(View view) {
        //1.得到密码 判断是否为空
        String password = etDialogEnterpwdPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "密码不能为空 - 123", Toast.LENGTH_SHORT).show();
            return;
        }
        //2.判断密码是否正确，正确就关闭页面
        if ("123".equals(password)) {
            //发一个消息给看门狗放行
            Intent intent = new Intent();
            intent.setAction("com.fangyi.mobilesafe.stopprotectting");
            intent.putExtra("packname", packname);
            //发广播
            sendBroadcast(intent);
            //关闭当前页面
            finish();
        } else {
            Toast.makeText(this, "密码错误 - 123", Toast.LENGTH_SHORT).show();
        }

    }

    //Activity看不见，用这个干掉
    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
