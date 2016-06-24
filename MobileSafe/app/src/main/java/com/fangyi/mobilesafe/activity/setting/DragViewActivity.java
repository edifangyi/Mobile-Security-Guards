package com.fangyi.mobilesafe.activity.setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fangyi.mobilesafe.R;

import java.lang.reflect.Field;

/**
 * Created by FANGYI on 2016/6/9.
 */
public class DragViewActivity extends AppCompatActivity {
    private LinearLayout llDragView;
    private SharedPreferences sp;
    private WindowManager wm;
    private int mWidth;
    private int mHeight;

    private TextView tvDragViewTop, tvDragViewBottom;
    //第一次点击的时间
    private long firstClickTime;
    private long[] mHits = new long[2];//在这里改多击


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_view);

        sp = getSharedPreferences("config", MODE_PRIVATE);

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWidth = wm.getDefaultDisplay().getWidth();
        mHeight = wm.getDefaultDisplay().getHeight();

        llDragView = (LinearLayout) findViewById(R.id.ll_drag_view);
        tvDragViewTop = (TextView) findViewById(R.id.tv_drag_view_top);
        tvDragViewBottom = (TextView) findViewById(R.id.tv_drag_view_bottom);


        //设置提示框背景颜色
        int which = sp.getInt("which", 0);
        int ids[] = {R.drawable.daily_theme_1, R.drawable.daily_theme_2,
                R.drawable.daily_theme_3, R.drawable.daily_theme_4};
        llDragView.setBackgroundResource(ids[which]);


        int lastX = sp.getInt("lastX", 0);
        int lastY = sp.getInt("lastY", 0);
        Log.e("当前坐标", lastX+";"+lastY);

        //控制tv控件显示位置
        if (lastY > mHeight/2) {
            //tv控件在底部
            tvDragViewTop.setVisibility(View.VISIBLE);
            tvDragViewBottom.setVisibility(View.INVISIBLE);
            Log.e("执行tv控件在底部", "11111111111111111111111111111");
        } else {
            //tv控件在顶部
            tvDragViewTop.setVisibility(View.INVISIBLE);
            tvDragViewBottom.setVisibility(View.VISIBLE);
            Log.e("执行tv控件在顶部", "222222222222222222222222222222");
        }

        /**
         * 一个控件或者View从创建到显示过程的主要方法
         * 1.构造方法
         * 2.测量 -onMeasure(boolean, int, int, int, int);
         * 3.指定位置 -onLayout();
         * 4.onDraw(canvas);
         */

        //布局在第一个阶段是不能用这个方法的
//        llDragView.layout(lastX, lastY,
//                lastX + llDragView.getWidth(), lastY + llDragView.getHeight());

        //使用第一个阶段就起作用的方法
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) llDragView.getLayoutParams();
        params.leftMargin =lastX;
        params.topMargin = lastY;
        llDragView.setLayoutParams(params);

//
//        //设置点击事件 - 实现 双击事件
//        llDragView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (firstClickTime > 0) {
//                    long secondClickTime = SystemClock.uptimeMillis();
//                    long dTime = secondClickTime - firstClickTime;
//                    if (dTime < 500) {
//                        firstClickTime = 0;
//                        return;
//                    }
//                }
//                firstClickTime = SystemClock.uptimeMillis();//已开机时间
//            }
//        });

        //设置点击事件 - 实现 多击事件

        llDragView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * src 拷贝的原数组
                 * srcPos 拷贝原数组从那个地方开始
                 * dst 拷贝到那个数组
                 * dstPos 从那个地方开始拷贝
                 * length 拷贝数组元素的个数
                 */
                System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
                mHits[mHits.length-1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
                    //居中
                    //屏幕宽度的一半-控件宽度的一半，到屏幕上部的距离，屏幕宽的一半+控件宽度的一半，屏幕高度的一半+控件的高度
                    llDragView.layout(mWidth/2 - llDragView.getWidth()/2, llDragView.getTop(),
                            mWidth/2 + llDragView.getWidth()/2, llDragView.getBottom());
                    saveXYData();
                }
            }
        });


        /**
         * 触摸事件
         * 实现 设置拖动
         *
         * 在Activity中，只对其设置触摸事件必须为true，如果设置了点击事件，这个时间需要把触摸事件的返回值设置为false
         */
        llDragView.setOnTouchListener(new View.OnTouchListener() {
            float startX = 0;
            float startY = 0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN://手指按下屏幕
                        //1.记录手指第一次按住的坐标
                        startX = event.getRawX();//getRawX()获得的是相对屏幕的位置，getX()获得的永远是view的触摸位置坐标
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

                        //屏蔽非法拖动
                        int newl = llDragView.getLeft() + dX;
                        int newt = llDragView.getTop() + dY;
                        int newr = llDragView.getRight() + dX;
                        int newb = llDragView.getBottom() + dY;
                        if (newl < 0 || newt < 0 || newr > mWidth || newb >mHeight - getStatusBarHeight()) {
                            break;
                        }
                        llDragView.layout(newl, newt, newr, newb);

                        //控制tv控件显示位置
                        if (newt > mHeight/2) {
                            //tv控件在底部
                            tvDragViewTop.setVisibility(View.VISIBLE);
                            tvDragViewBottom.setVisibility(View.INVISIBLE);
                            Log.e("执行tv控件在底部", "1");
                        } else {
                            //tv控件在顶部
                            tvDragViewTop.setVisibility(View.INVISIBLE);
                            tvDragViewBottom.setVisibility(View.VISIBLE);
                            Log.e("执行tv控件在顶部", "2");
                        }

                        //5.重新记录坐标

                        startX = event.getRawX();
                        startY = event.getRawY();
                        Log.e("重新记录坐标", startX+";"+startY);
                        break;

                    case MotionEvent.ACTION_UP://手指离开屏幕
                        saveXYData();
                        Log.e("手指离开屏幕", startX+";"+startY);
                        break;
                }

                return false;
            }
        });

    }

    private void saveXYData() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("lastX", llDragView.getLeft());
        editor.putInt("lastY", llDragView.getTop());
        editor.commit();
    }

    /**
     * 用于获取状态栏的高度
     */
    private  int statusBarHeight;
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return statusBarHeight;
    }
}
