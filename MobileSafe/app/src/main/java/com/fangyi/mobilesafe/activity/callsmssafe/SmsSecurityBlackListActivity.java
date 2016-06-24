package com.fangyi.mobilesafe.activity.callsmssafe;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fangyi.mobilesafe.R;
import com.fangyi.mobilesafe.dao.BlackNumberDao;
import com.fangyi.mobilesafe.domain.BlackNumberInfo;
import com.socks.library.KLog;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by FANGYI on 2016/6/10.
 */
public class SmsSecurityBlackListActivity extends AppCompatActivity {
    private ListView lvSmsSecurityBlacklistList;
    private LinearLayout llDoading;
    private BlackNumberDao dao;

    /**
     * 所有黑名单
     */
    private List<BlackNumberInfo> infos;

    /**
     * 从哪个地方开始加载下一个20条数据
     */
    private int index = 0;
    /**
     * 数据库中的总条数
     */
    private int dbCount;

    private SmsSBLAdapter adapter;

    /**
     * 防止过度加载
     */
    private boolean isLoading = false;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (adapter == null) {
                adapter = new SmsSBLAdapter();
                lvSmsSecurityBlacklistList.setAdapter(adapter);
            } else {
                //刷新页面
                adapter.notifyDataSetChanged();
            }
            isLoading = false;
//            lvSmsSecurityBlacklistList.setSelection(index);//不推荐  定位
            Log.e("4.数据加载完成", "隐藏");
            //数据加载完成，隐藏
            llDoading.setVisibility(View.INVISIBLE);
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smssecurityblack);

        lvSmsSecurityBlacklistList = (ListView) findViewById(R.id.lv_sms_security_blacklist_list);
        llDoading = (LinearLayout) findViewById(R.id.ll_loading);

        fillData();


        //设置滚动到底部的监听
        lvSmsSecurityBlacklistList.setOnScrollListener(new AbsListView.OnScrollListener() {

            //当状态发送变化的时候回调
            //静止 --> 滑动
            //滑动 --> 静止
            //手指滑动 --> 惯性滑动
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE: //静止状态 - 空闲
                        if (isLoading) {
                            Toast.makeText(SmsSecurityBlackListActivity.this, "数据正在加载...", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //第一个看的见的位置
//                        lvSmsSecurityBlacklistList.getFirstVisiblePosition();
                        //最后一个看的见的位置
                        int lastPostion = lvSmsSecurityBlacklistList.getLastVisiblePosition();
                        int currentTotalSize = infos.size();//当前的总条数，20
                        if (index >= dbCount) {
                            Toast.makeText(SmsSecurityBlackListActivity.this, "没有数据了...", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (lastPostion == (currentTotalSize - 1)) {
                            isLoading = true;
                            Toast.makeText(SmsSecurityBlackListActivity.this, "加载更多数据", Toast.LENGTH_SHORT).show();
                            index += 20;
                            fillData();
                        }


                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING: //滚动状态
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: //触摸滑动状态
                        break;
                }

            }

            //当滚动的时候执行这个方法
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

    }


    private class SmsSBLAdapter extends BaseAdapter {

        //得到黑名单的总数
        @Override
        public int getCount() {
            return infos.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        //每显示一行就执行一次
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view;
            ViewHolder holder;

            if (convertView != null && convertView instanceof RelativeLayout) {
                view = convertView;
                System.out.println("使用历史缓存的 View==" + position);
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(SmsSecurityBlackListActivity.this, R.layout.activity_smssecurityblack_item, null);
                System.out.println("创建新的 View==" + position);
                //当这个View对象呗创建的时候，就把id给查找了，并且放在容器（类）
                holder = new ViewHolder();
                holder.tvNumber = (TextView) view.findViewById(R.id.tv_black_number);
                holder.tvMode = (TextView) view.findViewById(R.id.tv_black_mode);
                holder.ivDelete = (ImageView) view.findViewById(R.id.iv_delete);

                //view对象和容器进行关联，保存View对象的层次结构
                view.setTag(holder);
            }


            //得到黑名单数据 - 根据位置去得到
            final BlackNumberInfo info = infos.get(position);
            holder.tvNumber.setText(info.getNumber());
            String mode = info.getMode();
            if ("0".equals(mode)) {
                //电话拦截
                holder.tvMode.setText("电话拦截");
            } else if ("1".equals(mode)) {
                //短信拦截
                holder.tvMode.setText("短信拦截");
            } else {
                //全部拦截
                holder.tvMode.setText("短信 + 电话拦截");
            }

            //设置垃圾桶图片的点击事件
            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //1.把数据库中的数据删除
                    dao.delete(info.getNumber());
                    //2.当前列表数据删除
                    infos.remove(info);
                    //3.刷新数据
                    adapter.notifyDataSetChanged();
                }
            });

            return view;
        }
    }

    /**
     * 容器
     */
    static class ViewHolder {
        TextView tvNumber;
        TextView tvMode;
        ImageView ivDelete;
    }


    /**
     * 加载数据
     */
    private void fillData() {
        dao = new BlackNumberDao(this);
        //数据加载前，显示
        Log.e("1.数据加载前", "显示");
        llDoading.setVisibility(View.VISIBLE);
        new Thread() {
            @Override
            public void run() {
                Log.e("2.执行数据加载", "隐藏");
                if (infos == null) {
                    //搜索部分内容 - 20条
                    infos = dao.queryPart(index);//联网的操作或者耗时的操作
                } else {
                    infos.addAll(dao.queryPart(index));
                }
                dbCount = dao.queryCount();
                handler.sendEmptyMessage(0);
            }
        }.start();
    }


    /**
     * 添加黑名单的点击事件，弹出对话框添加黑名单号码
     */
    private AlertDialog dialog;

    public void addBlackNumber(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog = builder.create();
        View contentview = View.inflate(this, R.layout.dialog_addblacknumber, null);

        final EditText etAddBlackNumber = (EditText) contentview.findViewById(R.id.et_dialog_addBlack_number);

        etAddBlackNumber.setInputType(InputType.TYPE_CLASS_NUMBER);//限制输入

        //自动弹出键盘
        etAddBlackNumber.setFocusableInTouchMode(true);
        etAddBlackNumber.requestFocus();

        Timer timer = new Timer();//定时器
        timer.schedule(new TimerTask() {

                           public void run() {
                               InputMethodManager inputManager =
                                       (InputMethodManager) etAddBlackNumber.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(etAddBlackNumber, 0);
                           }

                       },
                200);


        final RadioGroup rgMode = (RadioGroup) contentview.findViewById(R.id.rg_mode);
        Button btDialogAddBlackConfirm = (Button) contentview.findViewById(R.id.bt_dialog_addBlack_confirm);
        Button btDialogAddBlackCancel = (Button) contentview.findViewById(R.id.bt_dialog_addBlack_cancel);

        btDialogAddBlackCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btDialogAddBlackConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.得到电话号码，拦截模式
                String number = etAddBlackNumber.getText().toString().trim();
                int checkedid = rgMode.getCheckedRadioButtonId();//得到选中的id


                String mode = "2";//默认全部拦截
                switch (checkedid) {
                    case R.id.rb_number://电话
                        mode = "0";
                        break;
                    case R.id.rb_sms://短信
                        mode = "1";
                        break;
                    case R.id.rb_all://全部
                        mode = "2";
                        break;
                }
                KLog.e("mode ========" + mode);

                //2.判断是否为空
                if (TextUtils.isEmpty(number)) {
                    Toast.makeText(SmsSecurityBlackListActivity.this, "电话号码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //3.保存到数据库里面
                dao.add(number, mode);
//                dao.add("119", "1");
                //4.保存到当前列表
                BlackNumberInfo object = new BlackNumberInfo();
                object.setNumber(number);
                object.setMode(mode);
                infos.add(0, object);
                //5.对话框消掉,刷新UI
                dialog.dismiss();
                adapter.notifyDataSetChanged();
            }
        });

        dialog.setView(contentview, 0, 0, 0, 0);
        dialog.show();
    }

}
