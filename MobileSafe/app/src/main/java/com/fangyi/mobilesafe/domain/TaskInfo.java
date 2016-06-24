package com.fangyi.mobilesafe.domain;

/**
 * Created by FANGYI on 2016/6/17.
 */

import android.graphics.drawable.Drawable;

/**
 * 代表一个进程
 */
public class TaskInfo {
    private Drawable icon;
    private String name;
    private String packname;

    /**
     * 单位byte
     */
    private long meninfosize;
    /**
     * true用户进程
     * false系统进程
     */
    private boolean isUser;
    /**
     * true被选中
     * false未被选中
     */
    private boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackname() {
        return packname;
    }

    public void setPackname(String packname) {
        this.packname = packname;
    }

    public long getMeninfosize() {
        return meninfosize;
    }

    public void setMeninfosize(long meninfosize) {
        this.meninfosize = meninfosize;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }
}
