package com.fangyi.mobilesafe.activity.atools;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fangyi.mobilesafe.R;
import com.fangyi.mobilesafe.dao.CommonNumberQueryDao;

/**
 * Created by FANGYI on 2016/6/21.
 */

public class CommonNumberQueryActivity extends AppCompatActivity {
    private ExpandableListView elv;
    private SQLiteDatabase db;
    private static String path = "/data/data/com.fangyi.mobilesafe/files/commonnum.db";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools_common_number_query);
        //优化数据库访问
        db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);

        elv = (ExpandableListView) findViewById(R.id.elv);

        elv.setAdapter(new CommonNumberQueryAdapter());

        /**
         * 孩子的点击事件
         */
        elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                TextView tv = (TextView) v;//把adapter的tv转换为tv
                String number = tv.getText().toString().split("\n")[1].trim();//切割数组,trim()修整空格
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);//打开拨号界面
//                callIntent.setAction(Intent.ACTION_CALL);//直接拨打电话
                intent.setData(Uri.parse("tel:10001"));
//                intent.setData(Uri.parse("tel:"+number));
                Toast.makeText(CommonNumberQueryActivity.this, "电话号码指向10001", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                return true;
            }
        });
    }

    //    private class CommonNumberQueryAdapter implements ExpandableListAdapter {
//          20个方法 太多了
//    }
    private class CommonNumberQueryAdapter extends BaseExpandableListAdapter {

        //设计几组
        @Override
        public int getGroupCount() {
            return CommonNumberQueryDao.getGroupCount(db);
        }

        //一组里几个孩子
        @Override
        public int getChildrenCount(int groupPosition) {
            return CommonNumberQueryDao.getChildCount(groupPosition, db);
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        //分组的id
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        //孩子的id
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        //是否准许id相同
        @Override
        public boolean hasStableIds() {
            return false;
        }

        //组的样式
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView view;
            if (convertView != null) {
                view = (TextView) convertView;
            } else {
                view = new TextView(CommonNumberQueryActivity.this);
            }

            view.setTextColor(Color.RED);
            view.setTextSize(20);
            view.setText("       " + CommonNumberQueryDao.getGroupName(groupPosition, db));
            view.setPadding(45, 0, 0, 0);
            return view;
        }

        //孩子的样式
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            TextView view;
            if (convertView != null) {
                view = (TextView) convertView;
            } else {
                view = new TextView(CommonNumberQueryActivity.this);
            }
            view.setTextColor(Color.BLUE);
            view.setTextSize(15);
            view.setText("  " + CommonNumberQueryDao.getChildrenName(groupPosition, childPosition, db));
            view.setPadding(45, 0, 0, 0);
            return view;
        }

        //孩子是否被选择 true 选择，false不能被选择
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
            db = null;
        }

    }
}
