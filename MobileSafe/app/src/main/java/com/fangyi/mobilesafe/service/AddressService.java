package com.fangyi.mobilesafe.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.fangyi.mobilesafe.R;
import com.fangyi.mobilesafe.dao.NumberAddressQueryDao;
import com.fangyi.mobilesafe.receiver.BootCompleteReceiver;

/**
 * Created by FANGYI on 2016/6/6.
 */
public class AddressService extends Service {

    /**
     * 电话服务
     */
    private TelephonyManager tm;

    private MyPhoneStateListener listener;

    private OutCallReceiver receiver;


    /**
     * 窗口服务
     */
    private WindowManager wm;
    private TextView textView;
    private View view;
    private SharedPreferences sp;
    private WindowManager.LayoutParams params;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences("config", MODE_PRIVATE);
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        //监听来电
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new MyPhoneStateListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        //注册监听去电-广播接收者的代码注册
        receiver = new OutCallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");//监听去电的动作
        registerReceiver(receiver, filter);


    }

    /**
     * 服务里面的内部-用来监听去电
     */
    public class OutCallReceiver extends BootCompleteReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String number = getResultData();//去电号码
            String address = NumberAddressQueryDao.getAddess(number);
//            Toast.makeText(AddressService.this, address, Toast.LENGTH_SHORT).show();

            myToast(address);
        }
    }




    private class MyPhoneStateListener extends PhoneStateListener {

        /**
         * 呼叫状态
         * 来电号码
         * @param state
         * @param incomingNumber
         */
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING://来电-铃声响起
                    String address = NumberAddressQueryDao.getAddess(incomingNumber);
//                    Toast.makeText(AddressService.this, address, Toast.LENGTH_SHORT).show();
                    myToast(address);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:  //空闲状态。电话挂断
                    if (textView != null) {
                        wm.removeView(textView);
                        textView = null;
                    }


            }
        }
    }

    /**
     * 自定义吐司
     * @param address
     */
    private void myToast(String address) {
//        textView = new TextView(this);
//        textView.setTextSize(18);
//        textView.setTextColor(Color.RED);
//        textView.setText(address);

        view = View.inflate(this, R.layout.show_address, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_show_address);
        tv.setText(address);

        int which = sp.getInt("which", 0);
        //{"粉色","绿色","蓝色","紫色"};
        int ids[] = {R.drawable.daily_theme_1, R.drawable.daily_theme_2,
                R.drawable.daily_theme_3, R.drawable.daily_theme_4};

        //得到在设置中保存的位置
        int lastX = sp.getInt("lastX", 0);
        int lastY = sp.getInt("lastY", 0);


        view.setBackgroundResource(ids[which]);//根据设置中心的设置值，动态重新设置背景
        view.setOnTouchListener(new View.OnTouchListener() {
            float startX = 0;
            float startY = 0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN://手指按下屏幕
                        //1.记录手指第一次按住的坐标
                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE://手指移动屏幕
                        //2.来电的一个新坐标
                        float newX = event.getRawX();
                        float newY = event.getRawY();
                        //3.计算偏移量
                        int dX = (int) (newX - startX);
                        int dY = (int) (newY - startY);
                        //4.根据偏移量更新控件的位置
                        params.x += dX;
                        params.y += dY;

                        //屏蔽非法拖动
                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.y < 0) {
                            params.y =0;
                        }

                        if (params.x > wm.getDefaultDisplay().getWidth()) {
                            params.x = wm.getDefaultDisplay().getWidth() - view.getWidth();
                        }

                        if (params.y > wm.getDefaultDisplay().getHeight()) {
                            params.y = wm.getDefaultDisplay().getHeight() - view.getHeight();
                        }
                        wm.updateViewLayout(view, params);

                        //5.重新记录坐标
                        startX = event.getRawX();
                        startY = event.getRawY();
                        Log.e("重新记录坐标", startX+";"+startY);
                        break;

                    case MotionEvent.ACTION_UP://手指离开屏幕
                        //保存坐标
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("lastX", params.x);
                        editor.putInt("lastY", params.y);
                        editor.commit();
                        Log.e("手指离开屏幕", startX+";"+startY);
                        break;
                }

                return false;
            }
        });

        params = new WindowManager.LayoutParams();

        params.gravity = Gravity.TOP + Gravity.LEFT;//设置中心 屏幕的左上角
        params.x = lastX;
        params.y = lastY;

        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE //不可以获得焦点
//                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE //不可以触摸
                  | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON; //不能锁屏
        params.format = PixelFormat.TRANSPARENT;
//        params.type = WindowManager.LayoutParams.TYPE_TOAST;


//        <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"></uses-permission>
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
//        wm.addView(textView, params);
        wm.addView(view, params);
    }

    /**
     * 销毁服务
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        /**
         * 取消监听来电
         */
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
        listener = null;

        /**
         * 去掉注册监听去电
         */
        unregisterReceiver(receiver);
        receiver = null;

    }
}
