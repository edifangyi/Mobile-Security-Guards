package com.fangyi.mobilesafe.activity.atools;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fangyi.mobilesafe.R;
import com.fangyi.mobilesafe.utils.SmsBackupUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by FANGYI on 2016/6/5.
 */
public class AToolsActivity extends AppCompatActivity {
    private SharedPreferences sp;
    private ListView listView;
    private static final String names[] = {"号码归属地查询", "短信备份", "创建快捷键", "常用电话号码查询", "程序锁"};
    private static final int ids[] = {R.drawable.ic_activity_atools_main_1, R.drawable.ic_activity_atools_main_2,
            R.drawable.ic_activity_atools_main_3, R.drawable.ic_activity_atools_main_4,
            R.drawable.ic_activity_atools_main_5};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
        sp = getSharedPreferences("config", MODE_PRIVATE);

        listView = (ListView) findViewById(R.id.lv_atools_list_main);
        listView.setAdapter(new HomeAdapter());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position) {
                    case 0://号码归属地查询
                        intent = new Intent(AToolsActivity.this, NumberAddressQueryActivity.class);
                        startActivity(intent);
                        break;
                    case 1://进入短信备份
                        smsBackup();
                        break;
                    case 2://创建快捷键
                        shortcutKey();
                        break;
                    case 3://常用电话号码查询
                        intent = new Intent(AToolsActivity.this, CommonNumberQueryActivity.class);
                        startActivity(intent);
                        break;
                    case 4://程序锁
                        intent = new Intent(AToolsActivity.this, EnterAppLock.class);
                        startActivity(intent);
                        break;

                }
            }

        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class HomeAdapter extends BaseAdapter {

        //返回长度
        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(AToolsActivity.this, R.layout.activity_atools_item, null);
            ImageView ivAToolsIcon = (ImageView) view.findViewById(R.id.iv_atools_icon);
            TextView tvAToolsName = (TextView) view.findViewById(R.id.tv_atools_name);
            ivAToolsIcon.setImageResource(ids[position]);
            tvAToolsName.setText(names[position]);
            return view;
        }
    }

    /**
     * 点击事件 - 短信备份
     */
    private void smsBackup() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            final File file = new File(Environment.getExternalStorageDirectory(), "smsBackup.xml");
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("正在短信备份中...");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//样式更改为水平，带进度条的那种
            dialog.show();

            new Thread() {
                @Override
                public void run() {
                    try {
                        SmsBackupUtils.smsBackup(AToolsActivity.this, file.getAbsolutePath(), new SmsBackupUtils.SmsBackupCallBack() {
                            @Override
                            public void smsBackupBefore(int total) {
                                dialog.setMax(total);
                            }

                            @Override
                            public void smsBackupProgress(int progress) {
                                dialog.setProgress(progress);
                            }
                        });
                        dialog.dismiss();
                    } catch (IOException e) {
                        dialog.dismiss();
                        e.printStackTrace();
                    }
                }
            }.start();

        } else {
            Toast.makeText(this, "sd卡不可用", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 创建快捷键
     */
    private void shortcutKey() {

        Intent intent = new Intent();
        //动作
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");//安装快捷 + 权限
        //<uses-permission android:name="com.android.launcher.permission.INSTALL_SHORTCUT"/>
//
// intent.setAction(Intent.ACTION_CREATE_SHORTCUT);//动作创建快捷方式
        //额外的快捷键名称
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "女神报警");
        //额外的快捷键图标
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.b72));

        //点击我要干什么 - 一键报警
        Intent callIntent = new Intent();
        callIntent.setAction(Intent.ACTION_CALL);//直接拨打电话
//        intent.setAction(Intent.ACTION_DIAL);//打开拨号界面
        callIntent.setData(Uri.parse("tel:10001"));

        //把意图装进动作里
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, callIntent);

        sendBroadcast(intent);

        Toast.makeText(this, "创建快捷键成功", Toast.LENGTH_SHORT).show();

    }
}
