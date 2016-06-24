package com.fangyi.mobilesafe.activity.lostFind;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.fangyi.mobilesafe.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by FANGYI on 2016/6/2.
 */
public class SelectContactActivity extends AppCompatActivity {
    private ListView lvSelectContact;
    private List<Map<String, String>> data;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            lvSelectContact.setAdapter(new SimpleAdapter(SelectContactActivity.this, data,
                    R.layout.activity_lostfind_setup3_select_contact_item, new String[]{"name", "number"},
                    new int[]{R.id.tv_select_contact_name, R.id.tv_select_contact_number}));
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lostfind_setup3_select_contact);
        lvSelectContact = (ListView) findViewById(R.id.lv_select_contact);

        fillData();

        lvSelectContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String number = data.get(position).get("number");
                //1.回传数据
                Intent intent = new Intent();
                intent.putExtra("number", number);
                setResult(1, intent);
                //2.关闭当前页面
                finish();
            }
        });
    }

    /**
     * 加载数据
     */
    private void fillData() {
        new Thread() {
            @Override
            public void run() {
                data = getAllContacts();
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    /**
     * 得到手机里所有联系人
     *
     * @return
     */
    private List<Map<String, String>> getAllContacts() {
        List<Map<String, String>> maps = new ArrayList<>();

        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{"contact_id"}, null, null, null);

        Uri data_url = Uri.parse("content://com.android.contacts/data");

        while (cursor.moveToNext()) {
            //这里有一个人
            Map<String, String> map = new HashMap<>();

            String contact_id = cursor.getString(0);
            if (contact_id != null) {
                Cursor datacursor = cr.query(data_url, new String[]{"data1", "mimetype"},
                        "raw_contact_id = ?", new String[]{contact_id}, null);

                while (datacursor.moveToNext()) {
                    String data1 = datacursor.getString(0);
                    String mimetype = datacursor.getString(1);

                    if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
                        map.put("number", data1);
                    } else if ("vnd.android.cursor.item/name".equals(mimetype)) {
                        map.put("name", data1);
                    }
                }
                datacursor.close();
                if (map.get("name")!= null && map.get("number") != null) {
                    maps.add(map);
                }
            }
        }
        return maps;
    }


}
