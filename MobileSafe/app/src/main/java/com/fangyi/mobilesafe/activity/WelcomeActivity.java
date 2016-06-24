package com.fangyi.mobilesafe.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.fangyi.mobilesafe.R;
import com.fangyi.mobilesafe.activity.guide.Guide;
import com.fangyi.mobilesafe.utils.StreamTools;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by FANGYI on 2016/5/26.
 */
public class WelcomeActivity extends Activity {

    private static final String TAG = "WelcomeActivity";

    private boolean isFirstIn = false;

    private static final int GO_HOME = 1000;
    private static final int GO_GUIDE = 1001;
    private static final int SHOW_UPDATE_DIALOG = 1002;
    private static final int URL_ERROR = 1003;
    private static final int NETWORK_ERROR = 1004;
    private static final int JSON_ERROR = 1005;

    private TextView tvWelcomeVersion;
    private TextView tvWelcomeVpdateinfo;
    /**
     * 升级的描述信息
     */
    private String description;
    /**
     * 最新的APK升级地址
     */
    private String apkurl;
    /**
     * 获取设置中保存自动升级的选项
     */
    private SharedPreferences sp;


    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case GO_HOME://进入主页面
                    goHome();

                    break;

                case GO_GUIDE://进入引导页面
                    goGuide();
                    break;

                case SHOW_UPDATE_DIALOG://弹出升级对话框
                    Log.e(TAG, "有新版本，弹出升级对话框");
                    showUpdateDialog();
                    break;

                case URL_ERROR://URL错位
                    goHome();
                    Toast.makeText(WelcomeActivity.this, "URL错位", Toast.LENGTH_SHORT).show();
                    break;

                case NETWORK_ERROR://网络异常
                    goHome();
                    Toast.makeText(WelcomeActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    break;

                case JSON_ERROR://解析JSON异常
                    goHome();
                    Toast.makeText(WelcomeActivity.this, "解析JSON异常", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //引导页
        init();
        alphaAnimation();
    }


    /**
     * 判断是否第一次启动
     * 进行选择
     */
    private void init() {
        SharedPreferences perPreferences = getSharedPreferences("jike", MODE_PRIVATE);
        isFirstIn = perPreferences.getBoolean("isFirstIn", true);
        if (!isFirstIn) {
            assignViews();
        } else {
            handler.sendEmptyMessageDelayed(GO_GUIDE, 0);
            SharedPreferences.Editor editor = perPreferences.edit();
            editor.putBoolean("isFirstIn", false);
            editor.commit();
        }

    }

    private void goHome() {
        Intent i = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void goGuide() {
        Intent i = new Intent(WelcomeActivity.this, Guide.class);
        startActivity(i);
        finish();
    }

    /**
     * 加载任务
     */
    private void assignViews() {
        tvWelcomeVersion = (TextView) findViewById(R.id.tv_welcome_version);
        tvWelcomeVpdateinfo = (TextView) findViewById(R.id.tv_welcome_updateinfo);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        //设置版本号
        tvWelcomeVersion.setText("版本名：" + getVersionName());

        //拷贝查询数据库
        copyDB("naddress.db");
        copyDB("commonnum.db");
        copyDB("antivirus.db");

        //创建快捷键
        createShortcut();

        //自动升级默认开启
        if (sp.getBoolean("update", true)) {
            //检测是否有新版本
            checkVersion();
        } else {
            //延迟两秒进入主界面
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goHome();
                }
            }, 2000);
        }

    }

    /**
     * 创建快捷键
     */
    private void createShortcut() {

        if (sp.getBoolean("installshortcut", false)) {
            return;
        }
        Intent intent = new Intent();
        //动作
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");//安装快捷 + 权限
        //<uses-permission android:name="com.android.launcher.permission.INSTALL_SHORTCUT"/>
//
// intent.setAction(Intent.ACTION_CREATE_SHORTCUT);//动作创建快捷方式
        //额外的快捷键名称
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机卫士");
        //额外的快捷键图标
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_1));

        //点击我要我干什么 - 直接进入主界面
        Intent callIntent = new Intent();
        callIntent.setAction("com.fangyi.mobilesafe.home");//在清单文件注册


        //把意图装进动作里
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, callIntent);

        sendBroadcast(intent);

        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("installshortcut", true);
        editor.commit();

    }

    /**
     * 把assets目录下的 NumeberAddressQuery.db 拷贝到 path = "/data/data/com.fangyi.mobilesafe/files/NumeberAddressQuery.db"
     */
    private void copyDB(String dbname) {
        File file = new File(getFilesDir(), dbname);
        if (file.exists() && file.length() > 0) {
            //数据库已经存在
            Log.e("数据库已经存在", "数据库已经存在");
        } else {
            Log.e("数据库正在拷贝", "数据库正在拷贝");

            InputStream is = null;
            try {
                is = getAssets().open(dbname);

                FileOutputStream fos = new FileOutputStream(file);


                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                is.close();
                fos.close();

                Log.e("拷贝完成", "拷贝完成");

            } catch (IOException e) {
                e.printStackTrace();
            }


        }


    }

    /**
     * WelcomeActivity动画效果
     */
    public void alphaAnimation() {
        AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
        aa.setDuration(1000);
        findViewById(R.id.rl_welcome_root).startAnimation(aa);
    }


    /**
     * 得到版本名称
     *
     * @return
     */
    public String getVersionName() {
        //包管理器
        PackageManager pm = getPackageManager();
        //得到功能清单文件信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 软件的升级
     */
    private void checkVersion() {
        //升级的流程
        new Thread() {
            @Override
            public void run() {
                //请求网络，得到最新的版本信息
                Message msg = Message.obtain();
                long startTime = System.currentTimeMillis();
                try {
                    URL url = new URL(getString(R.string.serverurl));
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    //设置请求方法
                    con.setRequestMethod("GET");
                    //设置超时的时间
                    con.setConnectTimeout(4000);
                    //相应码
                    if (con.getResponseCode() == 200) {
                        //请求成功
                        //把流转换成String类型
                        InputStream is = con.getInputStream();
                        String result = StreamTools.readFromStream(is);

                        Log.e(TAG, "result==" + result);
                        //解析json
                        JSONObject obj = new JSONObject(result);
                        //服务器最新的版本
                        String version = (String) obj.get("version");
                        description = (String) obj.get("description");
                        apkurl = (String) obj.get("apkurl");

                        if (getVersionName().equals(version)) {
                            //没有新版本-进入主页面
                            msg.what = GO_HOME;
                        } else {
                            //弹出升级对话框
                            msg.what = SHOW_UPDATE_DIALOG;
                        }
                    }


                } catch (MalformedURLException e) {
                    //URL错位
                    e.printStackTrace();
                    msg.what = URL_ERROR;
                } catch (IOException e) {
                    //网络异常
                    e.printStackTrace();
                    msg.what = NETWORK_ERROR;
                } catch (JSONException e) {
                    //解析JSON异常
                    e.printStackTrace();
                    msg.what = JSON_ERROR;
                } finally {
                    long endTime = System.currentTimeMillis();
                    long dTime = endTime - startTime;//花的时间没有到2秒
                    if (dTime < 2000) {
                        SystemClock.sleep(2000 - dTime);
                    }
                    handler.sendMessage(msg);
                }
            }
        }.start();

    }

    /**
     * 弹出对话框
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");

//        //强制升级使用
//        builder.setCancelable(false);

        //监听事件
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                goHome();
            }
        });

        builder.setMessage(description);
        builder.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                goHome();
            }
        });
        builder.setPositiveButton("立刻升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    //1.下载APK
                    FinalHttp http = new FinalHttp();
                    http.download(apkurl, Environment.getExternalStorageDirectory() + "/mobilesafe2.0apk", new AjaxCallBack<File>() {
                        @Override
                        public void onSuccess(File file) {
                            super.onSuccess(file);
                            installApk(file);
                            Toast.makeText(WelcomeActivity.this, "下载成功了", Toast.LENGTH_SHORT).show();
                        }

                        private void installApk(File file) {
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Throwable t, int errorNo, String strMsg) {
                            t.printStackTrace();//打印错误日志
                            Toast.makeText(WelcomeActivity.this, "下载失败了", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onLoading(long count, long current) {
                            super.onLoading(count, current);
                            tvWelcomeVpdateinfo.setVisibility(View.VISIBLE);
                            int progess = (int) (current * 100 / count);
                            tvWelcomeVpdateinfo.setText("下载进度：" + progess + "%");
                        }
                    });
                    //2.替换安装
                } else {
                    Toast.makeText(WelcomeActivity.this, "sdcard", Toast.LENGTH_SHORT).show();
                }

            }
        });

        builder.show();
    }

}

