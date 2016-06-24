package com.fangyi.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.fangyi.mobilesafe.dao.BlackNumberDao;

import java.lang.reflect.Method;

/**
 * Created by FANGYI on 2016/6/13.
 */

public class SmsSafeService extends Service {
    private InnerSMSReceiver receiver;
    private BlackNumberDao dao;
    private TelephonyManager tm;
    private MyPhoneStateListener listener;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        dao = new BlackNumberDao(this);

        //注册监听短信
        receiver = new InnerSMSReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");//对短信感兴趣
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);//注册最高优先级
        registerReceiver(receiver, filter);

        //注册监听来电
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new MyPhoneStateListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);


    }


    /**
     * 广播接收者 - 拦截短信
     */
    private class InnerSMSReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            for (Object pdu : pdus) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);

                String sender = sms.getOriginatingAddress();//短信发送者

                //通过数据库中添加的电话号进行拦截
                if (dao.queryNumber(sender)) {//要拦截的电话号码
                    String mode = dao.queryMode(sender);
                    if ("1".equals(mode) || "2".equals(mode)) {//短信拦截
                        abortBroadcast();//把短信广播终止
                    }
                }

                String bode = sms.getMessageBody();//短信内容

                //按照内容去拦截
                if (bode.contains("fapiao")) {
                    abortBroadcast();//把短信广播终止
                }
            }
        }
    }

    /**
     * 广播接收者 - 拦截电话
     */
    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING://电话打进来
                    //电话挂断-电话拦截
                    if (dao.queryNumber(incomingNumber)) {
                        String mode = dao.queryMode(incomingNumber);
                        if ("1".equals(mode) || "2".equals(mode)) {//短信拦截
                            //把当前的电话挂断
                            endCall();//生成的呼叫记录是异步
                            //观察数据变化，再去删除
                            Uri uri = Uri.parse("content://call_log/calls");
                            //注册内容观察者
                            getContentResolver().registerContentObserver(uri, true,
                                    new MyContentObserver(new Handler(), incomingNumber));
                        }

                    }
                    break;
            }
        }
    }

    /**
     * 挂断电话
     */
    private void endCall() {
        //用反射得到Serviceanager的实例
        try {
            //1.得到字节码
            Class classz = SmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
            //2.得到对应的方法geyService
            Method method = classz.getMethod("getService", String.class);
            //3.得到实例
            //4.执行这个方法
            IBinder b = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
            //以上四部就是反射的过程

            //5.拷贝.aidl文件
            //6.生成Java代码
            ITelephony service = ITelephony.Stub.asInterface(b);
            //7.执行Java中的endCall();
            service.endCall();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 删除呼叫记录
     *
     * @param incomingNumber 要删除记录的电话号码
     */
    public void deleteCallLog(String incomingNumber) {
        ContentResolver resolver = getContentResolver();
        Uri uri = Uri.parse("content://call_log/calls");
        resolver.delete(uri, "number=?", new String[]{incomingNumber});
    }

    /**
     * 内容观察者
     */
    class MyContentObserver extends ContentObserver {

        private String incomingNumber;

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler, String incomingNumber) {
            super(handler);
            this.incomingNumber = incomingNumber;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            //当观察的路径变化的时候（呼叫记录变化），在做删除处理
            deleteCallLog(incomingNumber);
            //取消注册内容观察者
            getContentResolver().unregisterContentObserver(this);
        }
    }

    /**
     * 销毁
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册监听短信
        unregisterReceiver(receiver);
        receiver = null;
        //取消注册监听电话
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
        listener = null;

    }
}
