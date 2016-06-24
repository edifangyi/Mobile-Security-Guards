package com.fangyi.mobilesafe.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by FANGYI on 2016/6/5.
 */
public class CommonNumberQueryDao {


    /**
     * 得到分组的总数
     */
    public static int getGroupCount(SQLiteDatabase db) {
        int result = 0;
//        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);//只读
        Cursor cursor = db.rawQuery("SELECT count(*) FROM classlist;", null);
        cursor.moveToFirst();
        result = cursor.getInt(0);
        cursor.close();
//        db.close();
        return result;
    }

    /**
     * 得到分组的名称
     */
    public static String getGroupName(int groupPosition, SQLiteDatabase db) {
        String result = "";
        int newgroupPosition = groupPosition + 1;
//        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);//只读
        Cursor cursor = db.rawQuery("SELECT name FROM classlist WHERE idx=?", new String[]{newgroupPosition + ""});
        cursor.moveToFirst();
        result = cursor.getString(0);
        cursor.close();
//        db.close();
        return result;
    }

    /**
     * 得到某组中孩子的总数
     */
    public static int getChildCount(int groupPosition, SQLiteDatabase db) {
        int result = 0;
        int newgroupPosition = groupPosition + 1;
//        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);//只读
        Cursor cursor = db.rawQuery("SELECT count(*) FROM table" + newgroupPosition, null);
        cursor.moveToFirst();
        result = cursor.getInt(0);
        cursor.close();
//        db.close();
        return result;
    }

    /**
     * 得到某组中孩子的名称
     */
    public static String getChildrenName(int groupPosition, int childPosition, SQLiteDatabase db) {
        String result = "";
        int newgroupPosition = groupPosition + 1;
        int newchildPosition = childPosition + 1;
//        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);//只读
        Cursor cursor = db.rawQuery("SELECT name, number FROM table"+ newgroupPosition +" WHERE _id = ?", new String[]{newchildPosition + ""});
        cursor.moveToFirst();
        String name = cursor.getString(0);
        String number = cursor.getString(1);
        result = name + "\n  " + number;
        cursor.close();
//        db.close();
        return result;
    }
}
