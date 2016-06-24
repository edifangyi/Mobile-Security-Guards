package com.fangyi.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by FANGYI on 2016/6/10.
 */
public class AppLockDBOpenHelper extends SQLiteOpenHelper{

    //构造方法 - 数据库已经创建了
    public AppLockDBOpenHelper(Context context) {
        super(context, "applock.db", null, 1);
    }

    //数据库已经创建了 - 回调 - 特别适合创建表
    @Override
    public void onCreate(SQLiteDatabase db) {
        //_id 主键 自动增长， packname已加锁应用的包名
        db.execSQL("create table applock(_id integer primary key autoincrement, packname varchar(20))");
    }

    //数据库升级的时候调用的方法 -  1--> 2
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
