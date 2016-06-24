package com.fangyi.mobilesafe.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by FANGYI on 2016/6/5.
 */
public class AntiVirusQueryDao {
    private static String path = "/data/data/com.fangyi.mobilesafe/files/antivirus.db";

    /**
     * 病毒库查找
     * @param antiVirusMd5 特征码
     * @return 有值，是病毒，返回null 就是正常软件
     */
    public static String getDesc(String antiVirusMd5) {
        String result = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);

        Cursor cursor = db.rawQuery("SELECT desc FROM datable WHERE md5 =?", new String[]{antiVirusMd5});
        if (cursor.moveToNext()) {
            String desc = cursor.getString(0);
            result = desc;
        }
        return result;
    }
}
