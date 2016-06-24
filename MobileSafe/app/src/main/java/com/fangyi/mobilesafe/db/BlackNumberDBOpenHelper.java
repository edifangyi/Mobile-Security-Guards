package com.fangyi.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by FANGYI on 2016/6/10.
 */
public class BlackNumberDBOpenHelper extends SQLiteOpenHelper{

    //构造方法 - 数据库已经创建了
    public BlackNumberDBOpenHelper(Context context) {
        super(context, "blacknumber.db", null, 1);
    }

    //数据库已经创建了 - 回调 - 特别适合创建表
    @Override
    public void onCreate(SQLiteDatabase db) {
        //_id 主键 自动增长， number用于存放黑名单电话号码，mode拦截模式：0拦截电话，1.短信拦截，2.全部拦截
        db.execSQL("create table blacknumber(_id integer primary key autoincrement, " +
                "number varchar(20), " +
                "mode varchar(2))");
    }

    //数据库升级的时候调用的方法 -  1--> 2
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
