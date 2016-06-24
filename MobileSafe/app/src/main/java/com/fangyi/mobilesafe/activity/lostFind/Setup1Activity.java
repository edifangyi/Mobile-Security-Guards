package com.fangyi.mobilesafe.activity.lostFind;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fangyi.mobilesafe.R;

/**
 * Created by FANGYI on 2016/5/30.
 */
public class Setup1Activity extends BaseSetupActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lostfind_setup1);
    }

    public void showNext() {
        Log.e("dsdd", "！！！！！！！！！！！！！！！！！！！！！！！！");
        Intent intent = new Intent(this, Setup2Activity.class);
        startActivity(intent);
        //当前页面关闭
        finish();
        //Activity切换动画，在startActivity(intent);或finish();后面写
        overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
    }

    @Override
    public void showPre() {

    }
}
