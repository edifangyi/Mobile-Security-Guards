package com.fangyi.mobilesafe.activity.appmanager;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fangyi.mobilesafe.R;
import com.fangyi.mobilesafe.domain.AppInfo;
import com.fangyi.mobilesafe.engine.AppInfoProvider;

import java.util.ArrayList;
import java.util.List;

import static com.fangyi.mobilesafe.R.id.ll_uninstall;


/**
 * Created by FANGYI on 2016/6/15.
 */

public class AppManagerActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvAvailRom;
    private TextView tvAvailSdcard;
    private TextView tvAppStatus;
    private ListView lvAppmanger;
    private LinearLayout llLoading;

    //PopupWindow中的LinearLayout
    private LinearLayout llUninstall;
    private LinearLayout llStart;
    private LinearLayout llShare;

    private List<AppInfo> appInfos; //所有应用程序的信息
    private List<AppInfo> systemAppInfos; //系统应用程序的信息
    private List<AppInfo> userAppInfos; //用户应用程序的信息


    private AppMangerAdapter adapter;

    private PopupWindow window;
    private AppInfo appInfo;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            adapter = new AppMangerAdapter();
            lvAppmanger.setAdapter(adapter);
            llLoading.setVisibility(View.GONE);
            tvAppStatus.setVisibility(View.VISIBLE);
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appmanager);
        tvAvailRom = (TextView) findViewById(R.id.tv_avail_rom);
        tvAvailSdcard = (TextView) findViewById(R.id.tv_avail_sdcard);
        tvAppStatus = (TextView) findViewById(R.id.tv_app_status);
        lvAppmanger = (ListView) findViewById(R.id.lv_appmanger);
        llLoading = (LinearLayout) findViewById(R.id.ll_loading);

        tvAvailRom.setText("内存可用：" + getAvailSpace(Environment.getDataDirectory().getAbsolutePath()));
        tvAvailSdcard.setText("sd卡可用：" + getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath()));

        fillData();
        /**
         * 监听滚动
         */
        lvAppmanger.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                dismissPopupWindows();

                if (systemAppInfos == null || userAppInfos == null) {
                    return;
                }

                if (firstVisibleItem > userAppInfos.size()) {
                    //显示系统程序
                    tvAppStatus.setText("系统程序(" + systemAppInfos.size() + ")");
                } else {
                    //用户程序
                    tvAppStatus.setText("用户程序(" + userAppInfos.size() + ")");
                }
            }
        });

        //设置点击某一条的响应
        lvAppmanger.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = lvAppmanger.getItemAtPosition(position);

                if (obj != null) {
                    dismissPopupWindows();//消掉PopupWindows
                    appInfo = (AppInfo) obj;
                    System.out.println("appInfo==" + appInfo.getPackName());
//                    TextView textView = new TextView(AppManagerActivity.this);
//                    textView.setTextColor(Color.BLUE);
//                    textView.setText(appInfo.getPackName());
                    View contentView = View.inflate(AppManagerActivity.this, R.layout.popupwindow, null);

                    llUninstall = (LinearLayout) contentView.findViewById(ll_uninstall);
                    llStart = (LinearLayout) contentView.findViewById(R.id.ll_start);
                    llShare = (LinearLayout) contentView.findViewById(R.id.ll_share);

                    llUninstall.setOnClickListener(AppManagerActivity.this);
                    llStart.setOnClickListener(AppManagerActivity.this);
                    llShare.setOnClickListener(AppManagerActivity.this);


                    //要想PopupWindow播放动画，需要加载背景
                    window = new PopupWindow(contentView, -2, ActionBar.LayoutParams.WRAP_CONTENT);
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//透明
                    int[] location = new int[2];
                    view.getLocationInWindow(location);//得到ListView中某条的坐标
                    //在代码里面写的长度单位都是像素
                    window.showAtLocation(parent, Gravity.LEFT + Gravity.TOP, 180, location[1] - 170);

                    //渐变动画
                    AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
                    aa.setDuration(500);
                    //补间动画
                    ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
                    sa.setDuration(500);

                    AnimationSet set = new AnimationSet(false);
                    set.addAnimation(aa);
                    set.addAnimation(sa);
                    contentView.setAnimation(set);
                }

            }
        });

    }

    /**
     * 消掉PopupWindows
     */
    private void dismissPopupWindows() {
        if (window != null && window.isShowing()) {
            window.dismiss();
            window = null;
        }
    }


    private class AppMangerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return userAppInfos.size() + systemAppInfos.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AppInfo appInfo;

            if (position == 0) {
                TextView tv = new TextView(AppManagerActivity.this);
                tv.setHeight(0);
//                tv.setBackgroundColor(Color.GRAY);
//                tv.setText("用户AAA程序(" + userAppInfos.size() + ")");
//                tv.setTextColor(Color.WHITE);
                return tv;
            } else if (position == userAppInfos.size() + 1) {
                TextView tv = new TextView(AppManagerActivity.this);
                tv.setBackgroundColor(Color.GRAY);
                tv.setText("系统程序(" + systemAppInfos.size() + ")");
                tv.setTextColor(Color.WHITE);
                return tv;
            } else if (position <= userAppInfos.size()) {
                //用户程序
                int newposition = position - 1;
                appInfo = userAppInfos.get(newposition);
            } else {
                int newposition = position - userAppInfos.size() - 1 - 1;
                appInfo = systemAppInfos.get(newposition);
            }

            View view;
            ViewHolder viewholder;
            if (convertView != null && convertView instanceof RelativeLayout) {
                view = convertView;
                viewholder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(AppManagerActivity.this, R.layout.activity_appmanager_item, null);
                viewholder = new ViewHolder();

                viewholder.tvAppName = (TextView) view.findViewById(R.id.tv_app_name);
                viewholder.tvAppLocation = (TextView) view.findViewById(R.id.tv_app_location);
                viewholder.ivIcon = (ImageView) view.findViewById(R.id.iv_app_icon);

                //把对应关系保存起来
                view.setTag(viewholder);
            }
//            //得到应用程序的信息
//            AppInfo appInfo;
//            if (position < userAppInfos.size()) {
//                //用户程序
//                appInfo = userAppInfos.get(position);
//            } else {
//                //系统程序
//                appInfo = systemAppInfos.get(position - userAppInfos.size());
//            }

            viewholder.tvAppName.setText(appInfo.getName());
            if (appInfo.isRom()) {
                //手机内部
                viewholder.tvAppLocation.setText("内部储存");
            } else {
                //外部存储
                viewholder.tvAppLocation.setText("外部存储");
            }
            viewholder.ivIcon.setImageDrawable(appInfo.getIcon());

            return view;
        }

        @Override
        public Object getItem(int position) {
            AppInfo appInfo;

            if (position == 0) {
                return null;
            } else if (position == userAppInfos.size() + 1) {
                return null;
            } else if (position <= userAppInfos.size()) {
                //用户程序
                int newposition = position - 1;
                appInfo = userAppInfos.get(newposition);
            } else {
                int newposition = position - userAppInfos.size() - 1 - 1;
                appInfo = systemAppInfos.get(newposition);
            }
            return appInfo;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

    }

    static class ViewHolder {
        TextView tvAppName;
        TextView tvAppLocation;
        ImageView ivIcon;
    }

    /**
     * 加载数据
     */
    private void fillData() {
        llLoading.setVisibility(View.VISIBLE);
        new Thread() {
            @Override
            public void run() {
                appInfos = AppInfoProvider.getAllAppInfos(AppManagerActivity.this);

                //划分数据
                systemAppInfos = new ArrayList<>();
                userAppInfos = new ArrayList<>();

                for (AppInfo appInfo : appInfos) {
                    if (appInfo.isUser()) {
                        userAppInfos.add(appInfo);
                    } else {
                        systemAppInfos.add(appInfo);
                    }
                }

                handler.sendEmptyMessage(0);
            }
        }.start();
    }


    /**
     * 可到某个目录的可用空间
     *
     * @param path 路径
     * @return
     */
    private String getAvailSpace(String path) {

        StatFs fs = new StatFs(path);
        //得到多少块可用空间
        long blocks = fs.getAvailableBlocksLong();
        //得到每块的可用大小
        long size = fs.getBlockSizeLong();

        Formatter.formatFileSize(this, blocks * size);

        return Formatter.formatFileSize(this, blocks * size);
    }

    /**
     * PopupWindow中的点击事件
     */
    public void onClick(View v) {
        dismissPopupWindows();
        switch (v.getId()) {
            case R.id.ll_uninstall://卸载
                appInfo.getPackName();
                uninstallApp();
                break;
            case R.id.ll_start://启动
//                startApp();
                startApp2();
                break;
            case R.id.ll_share://分享
                shareApp();
                break;
        }
    }

    /**
     * 启动应用
     */
    private void startApp() {
        Intent intent = new Intent();
        PackageManager pm = getPackageManager();
        String packName = appInfo.getPackName();

        try {
            PackageInfo packInfo = pm.getPackageInfo(packName, PackageManager.GET_ACTIVITIES);
            ActivityInfo[] infos = packInfo.activities;
            if (infos != null&& infos.length > 0) {
                //得到第一个Activity
                ActivityInfo activityInfos = infos[0];
                String name = activityInfos.name;//全类名
                intent.setClassName(packName, name);
                startActivity(intent);
            } else {
                Toast.makeText(this, "亲，没找到这个程序的页面", Toast.LENGTH_SHORT).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    /**
     * 启动应用 方式 2
     */
    private void startApp2() {
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackName());
        startActivity(intent);
    }

    /**
     * 分享软件 - 分享到微博，微信，---
     */
    private void shareApp() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);        //"android.intent.action.DELETE"
        intent.addCategory(Intent.CATEGORY_DEFAULT);        //"android.intent.category.DEFAULT"
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "最近使用:" + appInfo.getName() + ",下载地址XXX");
        startActivity(intent);
    }

    /**
     * 卸载软件
     */
    private void uninstallApp() {
        if (appInfo.isUser()) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DELETE);        //"android.intent.action.DELETE"
            intent.addCategory(Intent.CATEGORY_DEFAULT);        //"android.intent.category.DEFAULT"
            intent.setData(Uri.parse("package:" + appInfo.getPackName()));
            startActivityForResult(intent, 0);
            fillData();
        } else {
            Toast.makeText(this, "需要root权限才能卸载", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        fillData();
    }
}
