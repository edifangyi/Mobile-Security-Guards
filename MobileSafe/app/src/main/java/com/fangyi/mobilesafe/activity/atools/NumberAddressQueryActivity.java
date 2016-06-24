package com.fangyi.mobilesafe.activity.atools;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fangyi.mobilesafe.R;
import com.fangyi.mobilesafe.dao.NumberAddressQueryDao;

/**
 * Created by FANGYI on 2016/6/5.
 */
public class NumberAddressQueryActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools_numberaddressquery);
        etActivityNumberAddressQueryNumber = (EditText) findViewById(R.id.et_activity_number_address_query_number);
        tvActivityNumberAddressQueryNumberResult = (TextView) findViewById(R.id.tv_activity_number_address_query_number_result);

        etActivityNumberAddressQueryNumber.addTextChangedListener(new TextWatcher() {

            //当文本改变的时候被回调
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if ( s != null && s.length() >= 3) {
                    String address = NumberAddressQueryDao.getAddess(s.toString());
                    tvActivityNumberAddressQueryNumberResult.setText(address);
                }
            }
            //当文本改变前被回调
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            //当文本改变后被回调
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //震动服务
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

    }

    private EditText etActivityNumberAddressQueryNumber;
    private TextView tvActivityNumberAddressQueryNumberResult;
    //震动服务
    private Vibrator vibrator;

    //查询电话号码的归属地
    public void query(View v) {
        //1.得到电话号码
        String number = etActivityNumberAddressQueryNumber.getText().toString().trim();
        //2.判断是否为空
        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "电话号码不能为空", Toast.LENGTH_SHORT).show();

            //抖动效果
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);

            //插入器
//            shake.setInterpolator(new Interpolator() {
//                @Override
//                public float getInterpolation(float input) {
//                    //方程式 = y
//                    return 0;
//                }
//            });

            etActivityNumberAddressQueryNumber.startAnimation(shake);

            //震动效果
            vibrator.vibrate(1000);//震动一秒
            long[] pattern = {500, 500, 1000, 1000, 2000, 2000};//震动停止震动停止
            //-1 不重复
            //0 重复
            //2 重复55,11,22  11,22  22
            vibrator.vibrate(pattern, -1);




        } else {

            //3.真正的查询
            Log.e(" 电话号码 ",number);
            String address = NumberAddressQueryDao.getAddess(number);
            tvActivityNumberAddressQueryNumberResult.setText(address);
            Log.e(" 电话号码 ",number);
        }
    }

}
