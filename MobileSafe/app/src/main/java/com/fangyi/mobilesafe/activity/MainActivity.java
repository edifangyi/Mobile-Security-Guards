package com.fangyi.mobilesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fangyi.mobilesafe.R;
import com.fangyi.mobilesafe.activity.antivirus.AntiVirusActivity;
import com.fangyi.mobilesafe.activity.appmanager.AppManagerActivity;
import com.fangyi.mobilesafe.activity.atools.AToolsActivity;
import com.fangyi.mobilesafe.activity.callsmssafe.SmsSecurityBlackListActivity;
import com.fangyi.mobilesafe.activity.cleancache.CleanCacheActivity;
import com.fangyi.mobilesafe.activity.lostFind.LostFindActivity;
import com.fangyi.mobilesafe.activity.setting.SettingActivity;
import com.fangyi.mobilesafe.activity.taskmanager.TaskManagerActivity;
import com.fangyi.mobilesafe.activity.trafficmanager.TrafficManagerActivity;
import com.fangyi.mobilesafe.rocket.Rocket;
import com.fangyi.mobilesafe.utils.MD5Utils;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity.clsss";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignViews();
    }

    private GridView listMain;
    private SharedPreferences sp;

    private static final  String names[] = {"手机防盗", "通信卫士", "应用管理",
                                            "进程管理", "缓存管理", "手机杀毒",
                                            "流量统计", "高级功能", "设置中心"};
    private static final  int ids[] = {R.drawable.ic_1, R.drawable.ic_2, R.drawable.ic_3,
                                       R.drawable.ic_4, R.drawable.ic_5, R.drawable.ic_6,
                                       R.drawable.ic_7, R.drawable.ic_8, R.drawable.ic_9};
    private void assignViews() {
        sp = getSharedPreferences("config", MODE_PRIVATE);
        listMain = (GridView) findViewById(R.id.list_main);
        //设置适配器
        listMain.setAdapter(new HomeAdapter());
        listMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position) {
                    case 0://进入手机防盗
                        showLostFindDialog();
                        break;
                    case 1://进入通信卫士
                        intent = new Intent(MainActivity.this, SmsSecurityBlackListActivity.class);
                        startActivity(intent);
                        break;
                    case 2://进入应用管理
                        intent = new Intent(MainActivity.this, AppManagerActivity.class);
                        startActivity(intent);
                        break;
                    case 3://进程管理
                        intent = new Intent(MainActivity.this, TaskManagerActivity.class);
                        startActivity(intent);
                        break;
                    case 4://进入缓存管理
                        intent = new Intent(MainActivity.this, CleanCacheActivity.class);
                        startActivity(intent);
                        break;
                    case 5://进入病毒查杀
                        intent = new Intent(MainActivity.this, AntiVirusActivity.class);
                        startActivity(intent);
                        break;
                    case 6://进入流量统计
                        intent = new Intent(MainActivity.this, TrafficManagerActivity.class);
                        startActivity(intent);
                        break;
                    case 7://进入高级功能
                        intent = new Intent(MainActivity.this, AToolsActivity.class);
                        startActivity(intent);
                        break;
                    case 8://进入设置中心
                        Intent settingIntent = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(settingIntent);
                        break;

                }
            }

        });
    }

    /**
     * 适配器
     */
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
            View view = View.inflate(MainActivity.this, R.layout.activity_main_item, null);
            ImageView ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
            TextView tvName = (TextView) view.findViewById(R.id.tv_name);
            ivIcon.setImageResource(ids[position]);
            tvName.setText(names[position]);
            return view;
        }
    }

    /**
     * 根据当前情况，弹出不同的对话框
     */
    private void showLostFindDialog() {
        //判断是否是设置了密码，如果没有设置就弹出设置对话框，否则就弹出输入对话框
        if (isSetupPwd()) {
            //已经设置密码
            showEnterDwdDialog();
        } else {
            //没有设置密码
            showSetupDwdDialog();
        }
    }

    /**
     * 输入密码的对话框
     */
    private void showEnterDwdDialog() {
        AlertDialog.Builder bulder = new AlertDialog.Builder(MainActivity.this);

        View view = View.inflate(MainActivity.this, R.layout.dialog_lostfind_enterpwd, null);
        final EditText etDialogEnderwdPassword = (EditText) view.findViewById(R.id.et_dialog_enterpwd_password);
        Button btDialogEnderpwdConfirm = (Button) view.findViewById(R.id.bt_dialog_enterpwd_confirm);
        Button btDialogEnderpwdCancel = (Button) view.findViewById(R.id.bt_dialog_enterpwd_cancel);

        btDialogEnderpwdCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消对话框
                dialog.dismiss();

            }
        });

        btDialogEnderpwdConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.得到两个输入框的密码
                String password = etDialogEnderwdPassword.getText().toString().trim();
                String password_save = sp.getString("password", "");

                //2.判断密码是否为空
                if (TextUtils.isEmpty(password)||TextUtils.isEmpty(password)) {
                    Toast.makeText(MainActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                //3.判断两个密码是否相同，不相同的话提示

                if (MD5Utils.ecoder(password).equals(password_save)) {
                    dialog.dismiss();
                    Log.e(TAG, "密码输入正确，消除对话框");
                    Intent intent = new Intent(MainActivity.this, LostFindActivity.class);
                    startActivity(intent);
                } else {

                    Toast.makeText(MainActivity.this, "您输入的密码不正确", Toast.LENGTH_SHORT).show();

                }

            }
        });
        bulder.setView(view);
        dialog = bulder.show();
    }

    /**
     * 设置密码的对话框
     */
    private AlertDialog dialog;

    private void showSetupDwdDialog() {
        final AlertDialog.Builder bulder = new AlertDialog.Builder(MainActivity.this);

        final View view = View.inflate(MainActivity.this, R.layout.dialog_lostfind_setuppwd, null);
        final EditText etDialogSetuppwdPassword = (EditText) view.findViewById(R.id.et_dialog_setuppwd_password);
        final EditText etDialogSetuppwdPasswordConfirm = (EditText) view.findViewById(R.id.et_dialog_setuppwd_password_confirm);
        Button btDialogSetuppwdConfirm = (Button) view.findViewById(R.id.bt_dialog_setuppwd_confirm);
        Button btDialogSetuppwdCancel = (Button) view.findViewById(R.id.bt_dialog_setuppwd_cancel);

        btDialogSetuppwdCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消对话框
                dialog.dismiss();
            }
        });

        btDialogSetuppwdConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.得到两个输入框的密码
                String password = etDialogSetuppwdPassword.getText().toString().trim();
                String password_confirm = etDialogSetuppwdPasswordConfirm.getText().toString().trim();//已经加密的密文

                //2.判断密码是否为空
                if (TextUtils.isEmpty(password)||TextUtils.isEmpty(password_confirm)) {
                    Toast.makeText(MainActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                //3.判断两个密码是否相同，不相同的话提示
                if (password.equals(password_confirm)) {
                    //4.保存密码，消掉对话框，进入手机放到页面
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("password", MD5Utils.ecoder(password));//保存的是加密后的密文
                    editor.commit();
                    dialog.dismiss();
                    Log.e(TAG, "密码保存，消除对话框");
                    Intent intent = new Intent(MainActivity.this, LostFindActivity.class);
                    startActivity(intent);
                } else {

                    Toast.makeText(MainActivity.this, "输入的两次密码不一致", Toast.LENGTH_SHORT).show();

                }

            }
        });
        bulder.setView(view);
        dialog = bulder.show();
    }



    /**
     * 判断是否设置了密码
     * @return
     */
    private boolean isSetupPwd() {
        String password = sp.getString("password", null);
//        if (TextUtils.isEmpty(password)) {
//            return false;
//        } else {
//            return true;
//        }
        return !TextUtils.isEmpty(password);
    }



    /**
     * 小火箭
     */
    public void rocket(View v) {
        Intent intent = new Intent(MainActivity.this, Rocket.class);
        startActivity(intent);
    }

}
