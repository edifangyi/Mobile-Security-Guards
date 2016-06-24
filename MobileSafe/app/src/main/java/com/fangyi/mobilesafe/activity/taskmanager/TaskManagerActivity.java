package com.fangyi.mobilesafe.activity.taskmanager;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fangyi.mobilesafe.R;
import com.fangyi.mobilesafe.domain.TaskInfo;
import com.fangyi.mobilesafe.engine.TaskInfoProvider;
import com.fangyi.mobilesafe.utils.SystemInfoUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by FANGYI on 2016/6/15.
 */

public class TaskManagerActivity extends AppCompatActivity {

    private TextView tvRunProcessCount;
    private TextView tvAvailRam;
    private ListView lvTaskmanger;
    private LinearLayout llTaskLoading;
    private TextView tvTaskStatus;

    private List<TaskInfo> taskInfos;//所有在运行的进程列表
    private List<TaskInfo> systemtaskInfos;//系统运行的进程列表
    private List<TaskInfo> usertaskInfos;//用户运行的进程列表

    private int runningProcessConut;//系统运行进程
    private long availRam;//可用内存
    private long totalRam;//总内存

    private TaskInfoAdapter adapter;
    private ActivityManager am;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    adapter = new TaskInfoAdapter();
                    lvTaskmanger.setAdapter(adapter);
                    llTaskLoading.setVisibility(View.GONE);
                    tvTaskStatus.setVisibility(View.VISIBLE);
                    break;
                case 1:

                    tvRunProcessCount.setText("运行中进程：" + msg.getData().getInt("runningProcessConut") + "个");
                    tvAvailRam.setText("剩余/总内存：" + Formatter.formatFileSize(TaskManagerActivity.this, msg.getData().getLong("availRam")) + "/" + Formatter.formatFileSize(TaskManagerActivity.this, totalRam));
                    Toast.makeText(TaskManagerActivity.this, "杀死了：" + msg.getData().getInt("killedCount") +
                            "个进程，释放了" + Formatter.formatFileSize(TaskManagerActivity.this, msg.getData().getLong("addRam")) + "M内存", Toast.LENGTH_SHORT).show();

                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskmanager);
        am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);

        tvRunProcessCount = (TextView) findViewById(R.id.tv_run_process_count);
        tvAvailRam = (TextView) findViewById(R.id.tv_avail_ram);
        lvTaskmanger = (ListView) findViewById(R.id.lv_taskmanger);
        llTaskLoading = (LinearLayout) findViewById(R.id.ll_task_loading);
        tvTaskStatus = (TextView) findViewById(R.id.tv_task_status);


//        runningProcessConut = SysTemInfoUtils.getRunningProcessCount(this);//5.1挂掉了
        runningProcessConut = SystemInfoUtils.getRunningProcessCount();
        availRam = SystemInfoUtils.getAvailRam(this);
        totalRam = SystemInfoUtils.getTotalRam(this);


        tvRunProcessCount.setText("运行中进程：" + runningProcessConut + "个");
        tvAvailRam.setText("剩余/总内存：" + Formatter.formatFileSize(this, availRam) + "/" + Formatter.formatFileSize(this, totalRam));

        fillData();

        //设置一整条的点击事件
        lvTaskmanger.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object object = lvTaskmanger.getItemAtPosition(position);
                if (object != null) {
                    TaskInfo taskInfo = (TaskInfo) object;
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.cr_taskmanager_status);
                    if (getPackageName().equals(taskInfo.getPackname())) {
                        return;
                    }
                    if (taskInfo.isChecked()) {
                        //选中
                        taskInfo.setChecked(false);
                        checkBox.setChecked(false);
                    } else {
                        taskInfo.setChecked(true);
                        checkBox.setChecked(true);
                    }
                }


            }
        });

        /**
         * 监听滚动
         */
        lvTaskmanger.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (systemtaskInfos == null || usertaskInfos == null) {
                    return;
                }

                if (firstVisibleItem > usertaskInfos.size()) {
                    //显示系统程序
                    tvTaskStatus.setText("系统进程(" + systemtaskInfos.size() + ")");
                } else {
                    //用户程序
                    tvTaskStatus.setText("用户进程(" + usertaskInfos.size() + ")");
                }
            }
        });


    }

    /**
     * 加载数据
     */
    private void fillData() {
        llTaskLoading.setVisibility(View.VISIBLE);
        new Thread() {
            @Override
            public void run() {
                taskInfos = TaskInfoProvider.getAllTaskInfos(TaskManagerActivity.this);
                systemtaskInfos = new ArrayList<TaskInfo>();
                usertaskInfos = new ArrayList<TaskInfo>();
                for (TaskInfo taskInfo : taskInfos) {
                    if (taskInfo.isUser()) {
                        //用户进程
                        usertaskInfos.add(taskInfo);
                    } else {
                        //系统进程
                        systemtaskInfos.add(taskInfo);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    /**
     * 填充数据
     */
    private class TaskInfoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
            boolean showsystem = sp.getBoolean("showsystem", true);
            if (showsystem) {
                return usertaskInfos.size() + 1 + systemtaskInfos.size() + 1;
            } else {
                return usertaskInfos.size() + 1;
            }

        }

        @Override
        public Object getItem(int position) {
            TaskInfo taskInfo;
            if (position == 0) {
                return null;
            } else if (position == usertaskInfos.size() + 1) {
                return null;
            } else if (position <= usertaskInfos.size()) {
                //用户进程
                int newposition = position - 1;
                taskInfo = usertaskInfos.get(newposition);
            } else {
                int newposition = position - usertaskInfos.size() - 1 - 1;
                taskInfo = systemtaskInfos.get(newposition);
            }
            return taskInfo;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TaskInfo taskInfo;
            if (position == 0) {
                TextView tv = new TextView(TaskManagerActivity.this);
                tv.setHeight(0);
                return tv;
            } else if (position == usertaskInfos.size() + 1) {
                TextView tv = new TextView(TaskManagerActivity.this);
                tv.setBackgroundColor(Color.GRAY);
                tv.setText("系统进程(" + systemtaskInfos.size() + ")");
                tv.setTextColor(Color.WHITE);
                return tv;
            } else if (position <= usertaskInfos.size()) {
                //用户进程
                int newposition = position - 1;
                taskInfo = usertaskInfos.get(newposition);
            } else {
                int newposition = position - usertaskInfos.size() - 1 - 1;
                taskInfo = systemtaskInfos.get(newposition);
            }
            View view;
            ViewHolder viewholder;
            if (convertView != null && convertView instanceof RelativeLayout) {
                view = convertView;
                viewholder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(TaskManagerActivity.this, R.layout.activity_taskmanager_item, null);
                viewholder = new ViewHolder();

                viewholder.tvTaskName = (TextView) view.findViewById(R.id.tv_task_name);
                viewholder.tvMeninFoSize = (TextView) view.findViewById(R.id.tv_meninfosize);
                viewholder.ivTaskIcon = (ImageView) view.findViewById(R.id.iv_task_icon);
                viewholder.cb_status = (CheckBox) view.findViewById(R.id.cr_taskmanager_status);

                //把对应关系保存起来
                view.setTag(viewholder);
            }
            //根据位置得到进程信息
//            taskInfo = taskInfos.get(position);
            viewholder.tvTaskName.setText(taskInfo.getName());
            viewholder.tvMeninFoSize.setText(Formatter.formatFileSize(TaskManagerActivity.this, taskInfo.getMeninfosize()));
            viewholder.ivTaskIcon.setImageDrawable(taskInfo.getIcon());

            if (taskInfo.isChecked()) {
                //被选中 - 显示出来
                viewholder.cb_status.setChecked(true);
            } else {
                //没有被勾选
                viewholder.cb_status.setChecked(false);
            }

            if (getPackageName().equals(taskInfo.getPackname())) {
                viewholder.cb_status.setVisibility(View.GONE);
            } else {
                viewholder.cb_status.setVisibility(View.VISIBLE);
            }

            return view;
        }
    }

    static class ViewHolder {
        TextView tvTaskName;
        TextView tvMeninFoSize;
        ImageView ivTaskIcon;
        CheckBox cb_status;
    }


    /**
     * 全选按钮
     *
     * @param view
     */

    public void selectAll(View view) {
        for (TaskInfo usertaskInfo : usertaskInfos) {
            if (getPackageName().equals(usertaskInfo.getPackname())) {
                continue;
            }
            usertaskInfo.setChecked(true);
        }

        for (TaskInfo systemtaskInfo : systemtaskInfos) {
            systemtaskInfo.setChecked(true);

        }

        /**
         * 刷新UI
         */
        adapter.notifyDataSetChanged();

    }

    /**
     * 反选
     *
     * @param view
     */
    public void unSelect(View view) {
        for (TaskInfo usertaskInfo : usertaskInfos) {
            if (getPackageName().equals(usertaskInfo.getPackname())) {
                continue;
            }
            usertaskInfo.setChecked(!usertaskInfo.isChecked());
        }

        for (TaskInfo systemtaskInfo : systemtaskInfos) {
            systemtaskInfo.setChecked(!systemtaskInfo.isChecked());
        }

        /**
         * 刷新UI
         */
        adapter.notifyDataSetChanged();//getCount() -- getView()
    }

    /**
     * 一键清理
     *
     * @param view
     */
    public void killAll(View view) {

        final ProgressDialog dialog = new ProgressDialog(TaskManagerActivity.this);
        dialog.setMessage("正在清理中...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//样式更改为水平，微调式
        dialog.show();

        new Thread() {
            int killedCount = 0;//杀死进程数
            long addRam = 0;//剩余增加

            List<TaskInfo> killedTaskInfo = new ArrayList<>();

            @Override
            public void run() {

                for (TaskInfo usertaskInfo : usertaskInfos) {
                    if (usertaskInfo.isChecked()) {
                        //把进程杀死 - 自杀
//                android.os.Process.killProcess(android.os.Process.myPid());过时
                        am.killBackgroundProcesses(usertaskInfo.getPackname());
                        killedTaskInfo.add(usertaskInfo);
                        killedCount++;
                        addRam += usertaskInfo.getMeninfosize();
                    }
                }


                for (TaskInfo systemtaskInfo : systemtaskInfos) {

                    if (systemtaskInfo.isChecked()) {
                        //把进程杀死
                        am.killBackgroundProcesses(systemtaskInfo.getPackname());
                        killedTaskInfo.add(systemtaskInfo);
                        killedCount++;
                        addRam += systemtaskInfo.getMeninfosize();
                    }

                }

                for (TaskInfo taskInfo : killedTaskInfo) {
                    if (taskInfo.isUser()) {
                        usertaskInfos.remove(taskInfo);
                    } else {
                        systemtaskInfos.remove(taskInfo);
                    }
                }

                dialog.dismiss();


                runningProcessConut -= killedCount;
                availRam += addRam;


                Message msg = new Message();
                Bundle b = new Bundle();
                b.putInt("killedCount", killedCount);
                b.putLong("addRam", addRam);
                b.putInt("runningProcessConut", runningProcessConut);
                b.putLong("availRam", availRam);
                msg.setData(b);
                msg.what = 1;
                handler.sendMessage(msg);


            }
        }.start();

//        fillData();
    }

    /**
     * 设置
     *
     * @param view
     */
    public void ReEnterSetting(View view) {
        Intent intent = new Intent(TaskManagerActivity.this, TaskManagerSettingActivity.class);
        startActivityForResult(intent, 0);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        adapter.notifyDataSetChanged();
    }
}
