package com.fangyi.mobilesafe.rocket;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.fangyi.mobilesafe.R;


public class Rocket extends Activity {
    private ImageView rocketImage;
    private AnimationDrawable rocketAnimation;

    private ImageView ivBottom;
    private ImageView ivTop;
    private ImageView ivJe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rocket);
        http://www.android-doc.com/guide/topics/resources/animation-resource.html

        Log.e("将要执行         火箭发射系统", "");
        rocketImage = (ImageView) findViewById(R.id.rocket_image);
        rocketImage.setBackgroundResource(R.drawable.rocket_thrust);

        rocketAnimation = (AnimationDrawable) rocketImage.getBackground();
        rocketAnimation.start();

        ivBottom = (ImageView) findViewById(R.id.iv_bottom);
        ivTop = (ImageView) findViewById(R.id.iv_top);
        ivJe = (ImageView) findViewById(R.id.iv_je);

        //设置触摸事件
        rocketImage.setOnTouchListener(new View.OnTouchListener() {
            int startX = 0;
            int startY = 0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //1.记录手指第一次按住的坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        ivJe.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int newX = (int) event.getRawX();
                        int newY = (int) event.getRawY();
                        //3.计算偏移量
                        int dX = newX - startX;
                        int dY = newY - startY;
                        //4.根据偏移量更新控件的位置
                        rocketImage.layout(rocketImage.getLeft() + dX, rocketImage.getTop() + dY,
                                rocketImage.getRight() + dX, rocketImage.getBottom() + dY);
                        //5.重新记录坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP:
                        ivJe.setVisibility(View.INVISIBLE);
                        //火箭发射架
                        int newl = rocketImage.getLeft();
                        int newt = rocketImage.getTop();
                        int newr = rocketImage.getRight();

                        if (newl > 250 && newt > 1300 && newr <850 ) {
                            //火箭发射系统
                            sendRocket();
                            //冒烟
                            ivBottom.setVisibility(View.VISIBLE);
                            ivTop.setVisibility(View.VISIBLE);
                            AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
                            aa.setDuration(500);//设置时间
                            aa.setRepeatCount(1);//播放重复次数
                            aa.setRepeatMode(AlphaAnimation.REVERSE);//重复的类型RESTART- 每次重头开始播放动画REVERSE -取反播放动画
                            aa.setFillAfter(true);//保持动画后的状态
                            ivBottom.startAnimation(aa);
                            ivTop.startAnimation(aa);
                        }
                        break;
                }
                return true;
            }
        });

    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int y = (int) msg.obj;
            Log.e("将要执行         火箭发射系统", y + "");
            rocketImage.layout(rocketImage.getLeft(), y,
                    rocketImage.getRight(), rocketImage.getHeight() + y);

            if (y == -380) {
                finish();
            }
        }
    };

    /**
     * 火箭发射系统
     */
    protected void sendRocket() {
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i <21; i++) {
                    SystemClock.sleep(40);
                    Message msg = Message.obtain();
                    int y = 380 - i*38; //最大距离
                    msg.obj = y;
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }

}
