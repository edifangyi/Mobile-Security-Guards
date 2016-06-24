package com.fangyi.mobilesafe.activity.antivirus;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fangyi.mobilesafe.R;
import com.fangyi.mobilesafe.dao.AntiVirusQueryDao;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by FANGYI on 2016/6/23.
 */

public class AntiVirusActivity extends AppCompatActivity {
    private static final int SCANING = 0;
    private static final int SCANING_FINISH = 1;
    private ImageView ivAntivirusRadar;
    private ProgressBar pbAntivirus;

    private TextView tvAntivirusStatus;
    private LinearLayout llContaner;
    private List<Scaninfo> antiViruslist;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCANING:
                    final Scaninfo scaninfo = (Scaninfo) msg.obj;
                    tvAntivirusStatus.setText("正在扫描：" + scaninfo.name);
                    TextView tvPackName = new TextView(AntiVirusActivity.this);
                    if (scaninfo.isAntiViris) {
                        tvPackName.setTextColor(Color.RED);
                        tvPackName.setText("发现病毒：" + scaninfo.name);
                    } else {
                        tvPackName.setTextColor(Color.WHITE);
                        tvPackName.setText("扫描安全：" + scaninfo.name);
                    }
                    llContaner.addView(tvPackName, 0);
                    break;
                case SCANING_FINISH:
                    tvAntivirusStatus.setText("扫描结束");
                    ivAntivirusRadar.clearAnimation();
                    if (antiViruslist != null && antiViruslist.size() > 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AntiVirusActivity.this);
                        builder.setTitle("警告！！！");
                        builder.setMessage("您的手机处于十分危险的状态，发现了：" + antiViruslist.size() + "个病毒，赶快杀毒");
                        builder.setPositiveButton("立即杀毒", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //卸载软件
                                for (Scaninfo scaninfo : antiViruslist) {
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_DELETE);
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.setData(Uri.parse("package:" + scaninfo.packName));
                                    startActivity(intent);
                                }
                            }
                        });
                        builder.setNegativeButton("下次再说", null);
                        builder.show();
                    } else {
                        Toast.makeText(AntiVirusActivity.this, "您的手机十分安全，不需要扫描", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antivirus);

        ivAntivirusRadar = (ImageView) findViewById(R.id.iv_antivirus_radar);
        pbAntivirus = (ProgressBar) findViewById(R.id.pb_antivirus);
        tvAntivirusStatus = (TextView) findViewById(R.id.tv_antivirus_status);
        llContaner = (LinearLayout) findViewById(R.id.ll_contaner);

        RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setInterpolator(new LinearInterpolator());
        ra.setRepeatCount(-1);
        ra.setDuration(2000);
        ra.setRepeatCount(RotateAnimation.INFINITE);//-1 无限循环
        ivAntivirusRadar.startAnimation(ra);

//        new Thread() {
//            @Override
//            public void run() {
//                SystemClock.sleep(3000);
//                ivAntivirusRadar.clearColorFilter();
//            }
//        }.start();

        tvAntivirusStatus.setText("手机杀毒128核正在初始化...");
        antiViruslist = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                SystemClock.sleep(2000);//模拟包和引擎初始化

                PackageManager pm = getPackageManager();
                List<PackageInfo> packInfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
                pbAntivirus.setMax(packInfos.size());

                Random random = new Random();
                int progress = 0;


                for (PackageInfo packInfo : packInfos) {
                    Scaninfo scaninfo = new Scaninfo();
                    String name = packInfo.applicationInfo.loadLabel(pm).toString();
                    scaninfo.packName = packInfo.packageName;
                    scaninfo.name = name;


                    //签名信息不能用
//                    String signaturesInfo = packInfo.signatures[0].toCharsString();//返回的是签名 数组
//                    String antiVirusMd5 = MD5Utils.ecoder(signaturesInfo);


                    //模拟查到病毒
                    final double d = Math.random();
                    final String c = String.valueOf((int) (d * 9));
                    String antiVirusMd5 = "12345" + c;
                    String result = AntiVirusQueryDao.getDesc(antiVirusMd5);
                    if (result != null) {
                        //是病毒
                        scaninfo.isAntiViris = true;
                        antiViruslist.add(scaninfo);
                    } else {
                        scaninfo.isAntiViris = false;
                    }

                    Message msg = Message.obtain();
                    msg.what = SCANING;
                    msg.obj = scaninfo;
                    handler.sendMessage(msg);
                    progress++;

                    pbAntivirus.setProgress(progress);

                    KLog.e("正在扫描================");
                    SystemClock.sleep(50 + random.nextInt(50));//必要的耗时操作，否则ProgressBar显示不正常

                }
                Message msg = Message.obtain();
                msg.what = SCANING_FINISH;
                handler.sendMessage(msg);
                KLog.e("病毒扫描完成");
                KLog.e("病毒扫描完成");
                KLog.e("病毒扫描完成");

            }
        }.start();

    }

    class Scaninfo {
        String name;
        String packName;
        boolean isAntiViris;
    }

}
