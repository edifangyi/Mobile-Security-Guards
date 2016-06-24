package com.fangyi.mobilesafe.activity.cleancache;

import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
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
                    tvCleancacheStatus.setText("扫描结束");
                    break;
                case SHOW_CACHE://显示缓存信息
                    CacheInfo cacheInfo = (CacheInfo) msg.obj;
                    View view = View.inflate(CleanCacheActivity.this, R.layout.activity_cleancache_item, null);


                    ImageView ivCleancacheIcon = (ImageView) view.findViewById(R.id.iv_cleancache_icon);
                    TextView tvCleancacheName = (TextView) view.findViewById(R.id.tv_cleancache_name);
                    TextView tvCleancache = (TextView) view.findViewById(R.id.tv_cleancache);
                    ivCleancacheIcon.setImageDrawable(cacheInfo.icon);
                    tvCleancacheName.setText(cacheInfo.name);
                    tvCleancache.setText(Formatter.formatFileSize(CleanCacheActivity.this, cacheInfo.cacheSize));

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


}
