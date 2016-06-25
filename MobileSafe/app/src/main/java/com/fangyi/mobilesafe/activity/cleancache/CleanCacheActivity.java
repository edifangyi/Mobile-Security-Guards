package com.fangyi.mobilesafe.activity.cleancache;

import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fangyi.mobilesafe.R;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

/**
 * Created by FANGYI on 2016/6/24.
 */

public class CleanCacheActivity extends AppCompatActivity {

    private static final int SCANING = 0;
    private static final int SCANING_FINISH = 1;
    private static final int SHOW_CACHE = 2;
    private Button btnCleancache;
    private ProgressBar pbCleancache;
    private TextView tvCleancacheStatus;
    private LinearLayout llCleancacheContaner;
    // 存储带有缓存的应用的名称
    private int cachePagenames = 0;
    PackageManager pm;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCANING://扫描中
                    String name = (String) msg.obj;
                    tvCleancacheStatus.setText("正在扫描：" + name);


                    break;
                case SCANING_FINISH://扫描结束
                    tvCleancacheStatus.setText("扫描完毕：发现有" +cachePagenames+ "个缓存信息");
                    break;
                case SHOW_CACHE://显示缓存信息
                    final CacheInfo cacheInfo = (CacheInfo) msg.obj;
                    View view = View.inflate(CleanCacheActivity.this, R.layout.activity_cleancache_item, null);
                    ImageView ivCleancacheIcon = (ImageView) view.findViewById(R.id.iv_cleancache_icon);
                    ImageView ivCleancacheDelete = (ImageView) view.findViewById(R.id.iv_cleancacheiv_delete);
                    TextView tvCleancacheName = (TextView) view.findViewById(R.id.tv_cleancache_name);
                    TextView tvCleancache = (TextView) view.findViewById(R.id.tv_cleancache);
                    ivCleancacheIcon.setImageDrawable(cacheInfo.icon);
                    tvCleancacheName.setText(cacheInfo.name);
                    tvCleancache.setText(Formatter.formatFileSize(CleanCacheActivity.this, cacheInfo.cacheSize));



                    ivCleancacheDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Method[] methods = PackageManager.class.getMethods();
                            for (Method method : methods) {
                                if ("deleteApplicationCacheFiles".equals(method.getName())) {
                                    try {
                                        method.invoke(pm, cacheInfo.packname, new IPackageDataObserver.Stub() {

                                            @Override
                                            public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {

                                            }


                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        //根据报名，跳转到对应的系统页面
                                        Intent intent = new Intent();
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        intent.setData(Uri.parse("package:" + cacheInfo.packname));
                                        startActivity(intent);

                                    }
                                }
                            }
                        }
                    });

                    llCleancacheContaner.addView(view, 0);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleancache);

        btnCleancache = (Button) findViewById(R.id.btn_cleancache);
        pbCleancache = (ProgressBar) findViewById(R.id.pb_cleancache);
        tvCleancacheStatus = (TextView) findViewById(R.id.tv_cleancache_status);
        llCleancacheContaner = (LinearLayout) findViewById(R.id.ll_cleancache_contaner);


        new Thread() {
            @Override
            public void run() {
                pm = getPackageManager();
                List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);

                pbCleancache.setMax(packageInfos.size());
                int progress = 0;
                Random random = new Random();

                for (PackageInfo packageInfo : packageInfos) {

                    try {
                        String packname = packageInfo.packageName;
                        String name = packageInfo.applicationInfo.loadLabel(pm).toString();

                        Message msg = Message.obtain();
                        msg.what = SCANING;
                        msg.obj = name;
                        handler.sendMessage(msg);

                        SystemClock.sleep(50 + random.nextInt(50));//必要的耗时操作，否则ProgressBar显示不正常

                        progress++;
                        pbCleancache.setProgress(progress);

                        //用反射得到缓存大小

                        Method method = PackageManager.class.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
                        method.invoke(pm, packname, new MyIPackageStatsObserver());


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Message msg = Message.obtain();
                msg.what = SCANING_FINISH;
                handler.sendMessage(msg);
            }
        }.start();
    }

    class MyIPackageStatsObserver extends IPackageStatsObserver.Stub {

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            long cacheSize = pStats.cacheSize;
            if (cacheSize > 0) {
                cachePagenames++;
                try {
                    CacheInfo cacheInfo = new CacheInfo();
                    cacheInfo.cacheSize = cacheSize;
                    cacheInfo.packname = pStats.packageName;
                    cacheInfo.name = pm.getApplicationInfo(pStats.packageName, 0).loadLabel(pm).toString();
                    cacheInfo.icon = pm.getApplicationInfo(pStats.packageName, 0).loadIcon(pm);

                    Message msg = Message.obtain();
                    msg.what = SHOW_CACHE;
                    msg.obj = cacheInfo;
                    handler.sendMessage(msg);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class CacheInfo {
        long cacheSize;
        String name;
        String packname;
        Drawable icon;
    }


    /**
     * 1.功能相当于,点击了 应用程序信息 里面的 清楚缓存按钮，而非 清除数据
     * <p>
     * 2.功能相当于,删除了/data/data/packageName/cache 文件夹里面所有的东西
     * <p>
     * 3.需要权限 <uses-permission android:name="android.permission.CLEAR_APP_CACHE" />
     */
    public void cleanAll(View view) {
        Method[] methods = PackageManager.class.getMethods();
        for (Method method : methods) {
            if ("freeStorageAndNotify".equals(method.getName())) {
                try {
                    method.invoke(pm, Integer.MAX_VALUE, new IPackageDataObserver.Stub() {

                        @Override
                        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {

                        }


                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
