package com.fangyi.mobilesafe.activity.lostFind;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * 公共类，基类，父类
 * Created by FANGYI on 2016/6/2.
 */
public abstract class BaseSetupActivity extends AppCompatActivity {
    /**
     * 手势识别器
     */
    private GestureDetector detector;
    protected SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        //实例化手势识别器
        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //屏蔽划得慢：速度单位：像素每秒
                if (Math.abs(velocityX) < 50) {
                    return true;
                }


                //屏蔽 Y轴方向斜划
                if (Math.abs(e2.getY() - e1.getY()) > 100) {
                    return true;
                }

                if (e2.getX() - e1.getX() > 100) {
                    //显示上一个页面
                    showPre();
                    return true;
                }
                if (e1.getX() - e2.getX() > 100) {
                    //显示下一个页面
                    showNext();
                    return true;
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }


        });
    }

    /**
     * 使用手势识别器
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    /**
     * 显示下一个页面的抽象方法
     */
    public abstract void showNext();

    /**
     * 显示上一个页面的抽象方法
     */
    public abstract void showPre();

    /**
     * 下一步按钮的点击事件
     *
     * @param v
     */
    public void next(View v) {
        showNext();
    }

    /**
     * 上一步按钮的点击事件
     *
     * @param v
     */
    public void pre(View v) {
        showPre();
    }
}
