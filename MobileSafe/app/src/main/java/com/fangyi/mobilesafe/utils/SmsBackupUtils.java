package com.fangyi.mobilesafe.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by FANGYI on 2016/6/14.
 */

public class SmsBackupUtils {

    /**
     * 定义的短信备份接口或者回调
     */
    public interface SmsBackupCallBack {
        /**
         * 短信备份前调用
         *
         * @param total 短信的总条数
         */
        public void smsBackupBefore(int total);

        /**
         * 短信备份过程中调用
         *
         * @param progress 短信备份的进度或备份了多少条
         */
        public void smsBackupProgress(int progress);
    }

    /**
     * 短信备份
     *
     * @param context 上下文
     * @param path    要保存短信的路径
     */
    public static void smsBackup(Context context, String path, SmsBackupCallBack back) throws IOException {
        ContentResolver resolver = context.getContentResolver();
        XmlSerializer serializer = Xml.newSerializer();
        File file = new File(path);
        FileOutputStream os = new FileOutputStream(file);
        serializer.setOutput(os, "utf-8");
        serializer.startDocument("utf-8", true);
        serializer.startTag(null, "smss");
        //把所有的短信备份
        Uri uri = Uri.parse("content://sms");
        Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);
        //设置总条数
//        dialog.setMax(cursor.getCount());
        back.smsBackupBefore(cursor.getCount());
        int progress = 0;//进度

        while (cursor.moveToNext()) {
            serializer.startTag(null, "sms");

            serializer.startTag(null, "address");
            String address = cursor.getString(0);
            serializer.text(address);
            serializer.endTag(null, "address");

            serializer.startTag(null, "date");
            String date = cursor.getString(1);
            serializer.text(date);
            serializer.endTag(null, "date");

            serializer.startTag(null, "type");
            String type = cursor.getString(2);
            serializer.text(type);
            serializer.endTag(null, "type");

            serializer.startTag(null, "body");
            String body = cursor.getString(3);
            serializer.text(body);
            serializer.endTag(null, "body");

            serializer.endTag(null, "sms");

            SystemClock.sleep(60);//模拟延迟

            progress ++;
//            dialog.setProgress(progress);
            back.smsBackupProgress(progress);
        }
        cursor.close();
        serializer.startTag(null, "smss");
        serializer.endDocument();
    }
}
