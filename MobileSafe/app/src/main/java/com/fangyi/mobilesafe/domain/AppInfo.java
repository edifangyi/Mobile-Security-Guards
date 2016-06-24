package com.fangyi.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by FANGYI on 2016/6/15.
 */
public class AppInfo {
    private Drawable icon;
    private String name;
    private String packName;
    /**
     * true安装在内部
     * false安装在sdcard
     */
    private boolean isRom;

    /**
     * true用户程序
     * false系统程序
     */
    private boolean isUser;

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

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public boolean isRom() {
        return isRom;
    }

    public void setRom(boolean rom) {
        isRom = rom;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }
}
