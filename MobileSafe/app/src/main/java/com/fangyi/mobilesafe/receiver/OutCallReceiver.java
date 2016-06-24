package com.fangyi.mobilesafe.receiver;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.fangyi.mobilesafe.dao.NumberAddressQueryDao;

/**
 * Created by FANGYI on 2016/6/8.
 */
public class OutCallReceiver extends BootCompleteReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String number = getResultData();//去电号码
        String address = NumberAddressQueryDao.getAddess(number);
        Toast.makeText(context, address, Toast.LENGTH_SHORT).show();
    }
}
