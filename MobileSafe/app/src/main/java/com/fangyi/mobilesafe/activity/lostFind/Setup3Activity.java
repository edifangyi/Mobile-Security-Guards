package com.fangyi.mobilesafe.activity.lostFind;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fangyi.mobilesafe.R;

/**
 * Created by FANGYI on 2016/5/30.
 */
public class Setup3Activity extends BaseSetupActivity {

    private EditText etActivitySetup3Number;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lostfind_setup3);
        etActivitySetup3Number = (EditText) findViewById(R.id.et_activity_setup3_number);
        etActivitySetup3Number.setText(sp.getString("safenumber", ""));
    }

    @Override
    public void showNext() {
        //校验是否设置安全号码
        String number = etActivitySetup3Number.getText().toString().trim();
        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "安全号码还没有设置", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("safenumber", number);
        editor.commit();


        Intent intent = new Intent(this, Setup4Activity.class);
        startActivity(intent);
        //当前页面关闭
        finish();
        overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
    }

    @Override
    public void showPre() {

        Intent intent = new Intent(this, Setup2Activity.class);
        startActivity(intent);
        //当前页面关闭
        finish();
        overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
    }

    /**
     * 进入联系人列表
     * @param v
     */
    public void selectContact(View v) {
        Intent intent = new Intent(this, SelectContactActivity.class);
        startActivityForResult(intent, 0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }

        String number = data.getStringExtra("number").replace("-", "");
        etActivitySetup3Number.setText(number);
    }
}
