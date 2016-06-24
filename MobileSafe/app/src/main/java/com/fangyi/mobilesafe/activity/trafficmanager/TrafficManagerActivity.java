package com.fangyi.mobilesafe.activity.trafficmanager;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.fangyi.mobilesafe.R;
import com.fangyi.mobilesafe.domain.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FANGYI on 2016/6/22.
 */

public class TrafficManagerActivity extends AppCompatActivity {

    private int uid;
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trafficmanager);

//        List<AppInfo> mAppInfos = new ArrayList<>();
//        //包管理器
//        PackageManager pm = getPackageManager();
//        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
//
//        for (PackageInfo packageInfo : packageInfos) {
//            AppInfo appInfo = new AppInfo();
//
//            String packName = packageInfo.packageName;
//            appInfo.setPackName(packName);
//
//            Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
//            appInfo.setIcon(icon);
//
//            String name = packageInfo.applicationInfo.loadLabel(pm).toString();
//            appInfo.setName(name);
//
//            uid = packageInfo.applicationInfo.uid;
//
//            mAppInfos.add(appInfo);
//        }
//
//        //2.2版本以后，就引入流量统计的接口
//        //统计总流量的接口
//        TrafficStats.getMobileRxBytes();//手机(2G, 2.5G, 3G, 4G )流量下载的总和
//        TrafficStats.getMobileTxBytes();//手机流量上传的总和
//
//        TrafficStats.getTotalRxBytes();//手机+wifi流量下载的总和
//        TrafficStats.getTotalTxBytes();//手机+wifi流量上传的总和
//
//        //统计某一款应用消耗的流量
//        TrafficStats.getUidRxBytes(uid);//根据用户ID获取它下载了多少流量
//        TrafficStats.getUidTxBytes(uid);//根据用户ID获取它上传了多少钱流量

        textView = (TextView) findViewById(R.id.tv_trafic);
        textView.setText("//统计总流量的接口\n" +
                "TrafficStats.getMobileRxBytes();//手机(2G, 2.5G, 3G, 4G )流量下载的总和\n" +
                "TrafficStats.getMobileTxBytes();//手机流量上传的总和\n" +

                "TrafficStats.getTotalRxBytes();//手机+wifi流量下载的总和\n" +
                "TrafficStats.getTotalTxBytes();//手机+wifi流量上传的总和\n" +

                "//统计某一款应用消耗的流量\n" +
                "TrafficStats.getUidRxBytes(uid);//根据用户ID获取它下载了多少流量\n" +
                "TrafficStats.getUidTxBytes(uid);//根据用户ID获取它上传了多少钱流量\n");

    }
}
