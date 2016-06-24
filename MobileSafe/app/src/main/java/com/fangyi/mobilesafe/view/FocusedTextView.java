package com.fangyi.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by FANGYI on 2016/5/29.
 */
public class FocusedTextView extends TextView {

    /**
     * 通常是在代码实例化的时候用
     * @param context
     */
    public FocusedTextView(Context context) {
        super(context);
    }

    /**
     * 在Android中，我们布局文件使用的中间控件，默认会调用钓友两个参数构造方法
     * @param context
     * @param attrs
     */
    public FocusedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置样式的时候
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public FocusedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FocusedTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 当前这个控件不一定获得的焦点，只是欺骗系统，让系统以为获得焦点的方式去处理事务
     * @return
     */
    @Override
    public boolean isFocused() {
        return true;
    }
}
