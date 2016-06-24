package com.fangyi.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fangyi.mobilesafe.R;

/**
 * Created by FANGYI on 2016/5/29.
 */
public class SettingClickView extends RelativeLayout {
    private TextView tvDesc;
    private TextView tvTitle;
    private String title;
    private String updateOff;
    private String updateOn;

    /**
     * 初始化布局文件
     */
    private void initView(Context context) {
        //inflate方法的作用：把布局文件--》View
        //最后一个参数：添加谁进来，谁就是setting_item_view的父亲，布局文件挂载在传进来的这个控件上
        View.inflate(context, R.layout.activity_setting_click_view, SettingClickView.this);
        tvDesc = (TextView) findViewById(R.id.tv_desc);
        tvTitle = (TextView) findViewById(R.id.tv_title);
    }



    //在代码中实例化的时候使用
    public SettingClickView(Context context) {
        super(context);
        initView(context);
    }

    //在布局文件实例化的时候使用
    public SettingClickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        title = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "title");
        updateOff = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "update_off");
        updateOn = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "update_on");
        tvTitle.setText(title);
        //设置描述信息
        setDescription(updateOff);
    }

    //要这只样式的时候使用
    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 设置组合控件的标题
     */
    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    /**
     * 设置组合控件的状态信息
     */
    public void setDescription(String text) {
        tvDesc.setText(text);
    }


}
