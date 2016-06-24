package com.fangyi.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;


/**
 * Created by FANGYI on 2016/6/3.
 */
public class GPSService extends Service {

    /**
     * 位置服务
     */
    private LocationManager lm;
    private MyLocationListener listener;
    private SharedPreferences sp;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences("config", MODE_PRIVATE);
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        //在服务里面监听位置变化
        listener = new MyLocationListener();
        //设置条件
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//设置精确度为最精准
        //设置参数细化
//        criteria.setAltitudeRequired(false);//不要求海拔信息
//        criteria.setBearingRequired(false);//不要求方位信息
//        criteria.setCostAllowed(true);//是否允许付费
        criteria.setPowerRequirement(Criteria.POWER_LOW);//对电量的要求
        String provider = lm.getBestProvider(criteria, true);
        lm.requestLocationUpdates(provider, 0, 0, listener);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消监听位置监听
        lm.removeUpdates(listener);
        listener = null;
    }

    private class MyLocationListener implements LocationListener {
        /**
         * 当位置改变的时候回调
         * @param location
         */
        @Override
        public void onLocationChanged(Location location) {
            String longitude = "J:" +  location.getLongitude();
            String latitude = "W:" +  location.getLatitude();
            String accuracy = "a:" + location.getAccuracy();

            //位置变化-发短信给安全号码
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("locationManager", longitude + latitude + accuracy);
            editor.commit();


        }

        /**
         * 当状态发生变化的时候回调
         * 开启-关闭
         * 关闭-开启
         * @param provider
         * @param status
         * @param extras
         */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        /**
         * 当某个位置提供者可用的时候回调
         * @param provider
         */
        @Override
        public void onProviderEnabled(String provider) {

        }

        /**
         * 当某个位置提供者不可用的时候回调
         * @param provider
         */
        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
