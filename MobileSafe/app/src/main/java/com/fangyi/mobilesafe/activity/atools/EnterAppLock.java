package com.fangyi.mobilesafe.activity.atools;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fangyi.mobilesafe.R;
import com.fangyi.mobilesafe.activity.taskmanager.TaskManagerActivity;
import com.fangyi.mobilesafe.dao.AppLockDao;
import com.fangyi.mobilesafe.domain.AppInfo;
import com.fangyi.mobilesafe.engine.AppInfoProvider;
import com.fangyi.mobilesafe.service.WatchDogSerivce;
import com.fangyi.mobilesafe.utils.ServiceStatusUtils;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FANGYI on 2016/6/21.
 */

public class EnterAppLock extends AppCompatActivity implements View.OnClickListener {
    private TextView tvUnlock;
    private TextView tvLocked;

    private LinearLayout llUnlock;
    private TextView tvUnlockInfo;
    private ListView lvUnlock;

    private LinearLayout llLocked;
    private TextView tvLockedInfo;
    private ListView lvLocked;

    private AppLockDao dao;

    private List<AppInfo> appInfos;//所有应用列表
    private List<AppInfo> unlockAppInfos;//未加锁应用列表
    private List<AppInfo> lockedAppInfos;//已加锁应用列表

    private AppLockAdapter unappLockAdapter;
    private AppLockAdapter lockedappLockAdapter;


    //设置程序锁
    private Button btnApplock;
    private Intent watchDogIntent;
    private boolean watchDogService;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    unappLockAdapter = new AppLockAdapter(true);
                    lockedappLockAdapter = new AppLockAdapter(false);
                    lvUnlock.setAdapter(unappLockAdapter);
                    lvLocked.setAdapter(lockedappLockAdapter);
                    break;
                case 1:
                    //4.刷新页面
                    unappLockAdapter.notifyDataSetChanged();
                    lockedappLockAdapter.notifyDataSetChanged();
                    break;
            }

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools_enterapplock);

        tvUnlock = (TextView) findViewById(R.id.tv_unlock);
        tvLocked = (TextView) findViewById(R.id.tv_locked);
        llUnlock = (LinearLayout) findViewById(R.id.ll_unlock);
        tvUnlockInfo = (TextView) findViewById(R.id.tv_unlock_info);
        lvUnlock = (ListView) findViewById(R.id.lv_unlock);
        llLocked = (LinearLayout) findViewById(R.id.ll_locked);
        tvLockedInfo = (TextView) findViewById(R.id.tv_locked_info);
        lvLocked = (ListView) findViewById(R.id.lv_locked);


        //设置点击事件
        tvUnlock.setOnClickListener(this);
        tvLocked.setOnClickListener(this);
        //加载数据
        fillDate();
        //设置看门狗
        watchDog();
    }

    /**
     * 设置看门狗服务
     */
    private void watchDog() {
        btnApplock = (Button) findViewById(R.id.btn_applock);
        watchDogIntent = new Intent(this, WatchDogSerivce.class);

        watchDogService = ServiceStatusUtils.inRunningService(this, "com.fangyi.mobilesafe.service.WatchDogSerivce");

        KLog.e("watchDogService =====================" + watchDogService);

        btnApplock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (watchDogService) {
                    btnApplock.setText("开启程序锁");
                    KLog.e("==========关闭程序锁==========关闭程序锁==========关闭程序锁==========关闭程序锁");
                    stopService(watchDogIntent);
                } else {
                    btnApplock.setText("关闭程序锁");
                    startService(watchDogIntent);
                    watchDogService = true;
                }
            }
        });
    }

    /**
     * 加载数据
     */
    public void fillDate() {

        final ProgressDialog dialog = new ProgressDialog(EnterAppLock.this);
        dialog.setMessage("正在加载...");
        dialog.show();

        new Thread() {
            @Override
            public void run() {
                KLog.e("-------------------------------多线程-------------------------------");
                //把所有未加锁的软件读取出来 - 在线程中写
                dao = new AppLockDao(EnterAppLock.this);
                appInfos = AppInfoProvider.getAllAppInfos(EnterAppLock.this);
                unlockAppInfos = new ArrayList<>();
                lockedAppInfos = new ArrayList<>();

                for (AppInfo appInfo : appInfos) {
                    if (dao.query(appInfo.getPackName())) {
                        //已加锁
                        lockedAppInfos.add(appInfo);
                    } else {
                        //未加锁
                        unlockAppInfos.add(appInfo);
                    }
                }

                dialog.dismiss();
                Message msg = new Message();
                msg.what = 0;
                handler.sendMessage(msg);

            }
        }.start();
    }


    /**
     * 设置点击事件
     *
     * @param v
     */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_unlock://未加锁
                llUnlock.setVisibility(View.VISIBLE);
                KLog.e("未加锁");
                tvUnlock.setBackgroundResource(R.drawable.market_sng_intro_click);
                tvLocked.setBackgroundResource(R.drawable.market_sng_friendrank);
                break;

            case R.id.tv_locked://已加锁
                KLog.e("加锁");
                llUnlock.setVisibility(View.GONE);
                tvUnlock.setBackgroundResource(R.drawable.market_sng_intro);
                tvLocked.setBackgroundResource(R.drawable.market_sng_friendrank_click);
                break;
        }
    }

    /**
     * 适配器
     */
    private class AppLockAdapter extends BaseAdapter {

        /**
         * ture:未加锁
         * false:未加锁
         */
        private boolean isFlag = true;

        public AppLockAdapter(boolean isFlag) {

            this.isFlag = isFlag;
        }

        @Override
        public int getCount() {
            if (isFlag) {
                tvUnlockInfo.setText("未加锁的应用：" + unlockAppInfos.size());
                return unlockAppInfos.size();
            } else {
                tvLockedInfo.setText("已加锁的应用：" + lockedAppInfos.size());
                return lockedAppInfos.size();
            }
//            return appInfos.size();
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
            final View view;
            ViewHolder viewHolder;
            if (convertView != null) {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(EnterAppLock.this, R.layout.activity_atools_enterapplock_item, null);
                viewHolder = new ViewHolder();
                viewHolder.ivApplockIcon = (ImageView) view.findViewById(R.id.iv_applock_icon);
                viewHolder.tvApplockName = (TextView) view.findViewById(R.id.tv_applock_name);
                viewHolder.ivApplockStatus = (ImageView) view.findViewById(R.id.iv_applock_status);
                view.setTag(viewHolder);
            }
            final AppInfo appInfo;
            if (isFlag) {
                viewHolder.ivApplockStatus.setImageResource(R.drawable.ahl);
                appInfo = unlockAppInfos.get(position);
            } else {
                viewHolder.ivApplockStatus.setImageResource(R.drawable.ahk);
                appInfo = lockedAppInfos.get(position);
            }
            viewHolder.ivApplockIcon.setImageDrawable(appInfo.getIcon());
            viewHolder.tvApplockName.setText(appInfo.getName());
            viewHolder.ivApplockStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isFlag) {

//                        new Thread() {
//                            @Override
//                            public void run() {
//                                try {
//                                    sleep(500);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                                //1.添加到已加锁数据库
//                                dao.add(appInfo.getPackName());
//                                //2.添加到已加锁当前列表
//                                lockedAppInfos.add(appInfo);
//                                //3.未加锁列表要把这个应用的信息移除
//                                unlockAppInfos.remove(appInfo);
//
//                                Message msg = new Message();
//                                msg.what = 1;
//                                handler.sendMessage(msg);
//                            }
//                        }.start();

                        //postDelayed中的run方法
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //运行在主线程中
                                //1.添加到已加锁数据库
                                dao.add(appInfo.getPackName());
                                //2.添加到已加锁当前列表
                                lockedAppInfos.add(appInfo);
                                //3.未加锁列表要把这个应用的信息移除
                                unlockAppInfos.remove(appInfo);

                                unappLockAdapter.notifyDataSetChanged();
                                lockedappLockAdapter.notifyDataSetChanged();
                            }
                        }, 500);

                        //5.位移动画
                        TranslateAnimation ta = new TranslateAnimation(
                                Animation.RELATIVE_TO_SELF, 0,
                                Animation.RELATIVE_TO_SELF, 1.0f,
                                Animation.RELATIVE_TO_SELF, 0,
                                Animation.RELATIVE_TO_SELF, 0);
                        ta.setDuration(500);
                        view.startAnimation(ta);


                    } else {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //1.移除数据库
                                dao.delete(appInfo.getPackName());
                                //2.添加到未加锁当前列表
                                unlockAppInfos.add(appInfo);
                                //3.已加锁列表要把这个应用的信息移除
                                lockedAppInfos.remove(appInfo);

                                Message msg = new Message();
                                msg.what = 1;
                                handler.sendMessage(msg);
                            }
                        }, 500);
                        //5.位移动画
                        TranslateAnimation ta = new TranslateAnimation(
                                Animation.RELATIVE_TO_SELF, 0,
                                Animation.RELATIVE_TO_SELF, -1.0f,
                                Animation.RELATIVE_TO_SELF, 0,
                                Animation.RELATIVE_TO_SELF, 0);
                        ta.setDuration(500);
                        view.startAnimation(ta);

                    }


                }
            });

            return view;
        }
    }


    static class ViewHolder {
        ImageView ivApplockIcon;
        TextView tvApplockName;
        ImageView ivApplockStatus;
    }


}
