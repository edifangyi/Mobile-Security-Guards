package com.fangyi.mobilesafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.fangyi.mobilesafe.db.AppLockDBOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FANGYI on 2016/6/12.
 */

public class AppLockDao {
    private AppLockDBOpenHelper mHelper;
    private Context context;

    public AppLockDao(Context context) {
        mHelper = new AppLockDBOpenHelper(context);
        this.context = context;
    }


    /**
     * 添加一条数据 - 已加锁数据
     *
     * @param packname 要加锁应用的包名
     */
    public void add(String packname) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packname", packname);
        db.insert("applock", null, values);
        db.close();
        //改变就发送消息
        Uri uri = Uri.parse("content://com.fangyi.mobilesafe.dbchange");
        context.getContentResolver().notifyChange(uri, null);
    }

    /**
     * 删除一条已加锁数据
     *
     * @param packname
     */
    public void delete(String packname) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete("applock", "packname=?", new String[]{packname});
        db.close();
        //改变就发送消息
        Uri uri = Uri.parse("content://com.fangyi.mobilesafe.dbchange");
        context.getContentResolver().notifyChange(uri, null);
    }

    /**
     * 这是一条已加锁数据
     *
     * @param packname
     * @return true 已加锁 false 未加锁
     */
    public boolean query(String packname) {
        boolean result = false;
        SQLiteDatabase db = mHelper.getWritableDatabase();
        Cursor cursor = db.query("applock", null, "packname=?", new String[]{packname}, null, null, null);
        if (cursor.moveToNext()) {
            result = true;
        }
        db.close();
        //改变就发送消息
        Uri uri = Uri.parse("content://com.fangyi.mobilesafe.dbchange");
        context.getContentResolver().notifyChange(uri, null);
        return result;
    }

    /**
     * 所有的已加锁应用的包名
     *
     * @return
     */
    public List<String> queryAll() {
        List<String> result = new ArrayList<>();
        SQLiteDatabase db = mHelper.getWritableDatabase();
        Cursor cursor = db.query("applock", new String[]{"packname"}, null, null, null, null, null);
        while (cursor.moveToNext()) {

            String packname = cursor.getString(0);

            result.add(packname);
        }
        db.close();
        //改变就发送消息
        Uri uri = Uri.parse("content://com.fangyi.mobilesafe.dbchange");
        context.getContentResolver().notifyChange(uri, null);
        return result;

    }

}
