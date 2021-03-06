package com.xdandroid.sample;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.IBinder;
import android.util.Log;

import com.xdandroid.sample.lib.AbsWorkService;
import com.xdandroid.sample.utils.WifiAdmin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.xdandroid.sample.MainActivity.instance;


public class TraceServiceImpl extends AbsWorkService {

    private static final String TAG = "MainActivity";

    private static final int LOOP_TIME = 1;
    //是否 任务完成, 不再需要服务运行?
    public static boolean sShouldStopService;
    public static Disposable sDisposable;
    WifiAdmin wifiAdmin;

    public static void stopService() {
        //我们现在不再需要服务运行了, 将标志位置为 true
        flag = false;
        sShouldStopService = true;
        //取消对任务的订阅
        if (sDisposable != null) sDisposable.dispose();
        //取消 Job / Alarm / Subscription
        cancelJobAlarmSub();
    }

    private void afterRequestPermission() {
        try {
            if (!wifiAdmin.checkWifi()) {
                Log.d(TAG, "MainActivity wifi未开启,需要开启wifi");
                // 开启wifi
                boolean open = wifiAdmin.openWifi();
                Log.d(TAG, "service 中开启wifi成功");
                flag = false;
            } else {
                Log.d(TAG, "MainActivity wifi已开启");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        afterOpenWifi();
    }

    /**
     * 是否 任务完成, 不再需要服务运行?
     *
     * @return 应当停止服务, true; 应当启动服务, false; 无法判断, 什么也不做, null.
     */
    @Override
    public Boolean shouldStopService(Intent intent, int flags, int startId) {
        return sShouldStopService;
    }

    @Override
    public void startWork(Intent intent, int flags, int startId) {
        Log.d(TAG, "service startWork");
        flag = false;
        sDisposable = Flowable
                .interval(LOOP_TIME, TimeUnit.SECONDS)
                //取消任务时取消定时唤醒
                .doOnTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.d(TAG, "取消了service中的订阅任务");
                        cancelJobAlarmSub();
                    }
                }).subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long count) throws Exception {
                        // 后台所做的事
                        doService();
                    }
                });
    }

    /**
     * 开关
     */
    static boolean flag = false;

    //帅比权
    private void doService() {
        try {
            // 初始化wifi管理类
            wifiAdmin = new WifiAdmin(this);
            // 判断是否开启wifi
            afterRequestPermission();
            // 扫描
            wifiAdmin.startScan();
            Log.d(TAG, "start 扫描 wifi List");
            if (!flag) {
                flag = true;
                //已经配置好的且在扫描范围之内的wifi
                List<ScanResult> sameList = new ArrayList<>();
                // 观察当前已配置wifi网络
                List<WifiConfiguration> configurations = wifiAdmin.getConfiguration();

                List<ScanResult> wifiList = wifiAdmin.getWifiList();
                for (ScanResult wifiBean : wifiList) {
                    for (WifiConfiguration configBean : configurations) {
                        if ((wifiBean.SSID).equals(configBean.SSID.replaceAll("\"", ""))) {
                            sameList.add(wifiBean);
                        }
                    }
                }
                Log.d(TAG, "sameList Size = " + sameList.size());
                // 如果有MainActivity存在则刷新listView
                if (instance != null && sameList.size() > 0)
                    instance.upDateListView(sameList);
                // 做连接发消息操作
                Observable.create(new ObservableOnSubscribe<List<ScanResult>>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<List<ScanResult>> e) throws Exception {
                        e.onNext(wifiAdmin.getWifiList());
                        e.onComplete();
                    }
                }).observeOn(Schedulers.io())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Consumer<List<ScanResult>>() {
                            @Override
                            public void accept(@NonNull List<ScanResult> scanResults) throws Exception {
                                try {
                                    //已经配置好的且在扫描范围之内的wifi
                                    Log.d(TAG,"扫描的wifiList size  = " + scanResults.size());
                                    List<ScanResult> sameList = new ArrayList<>();
                                    // 观察当前已配置wifi网络
                                    List<WifiConfiguration> configurations = wifiAdmin.getConfiguration();

                                    List<WifiConfiguration> sameConfigList = new ArrayList<>();

                                    for (ScanResult wifiBean : scanResults) {
                                        for (WifiConfiguration configBean : configurations) {
                                            if ((wifiBean.SSID).equals(configBean.SSID.replaceAll("\"", ""))) {
                                                sameList.add(wifiBean);
                                                sameConfigList.add(configBean);
                                            }
                                        }
                                    }
                                    Log.d(TAG, "当前可用的已配置的wifi size = " + sameList.size());

                                    if (sameList.size() == 1) {
                                        ScanResult scanResult = sameList.get(0);
                                        sendMsg();
                                    } else if (sameList.size() > 1) {
                                        for (int i = 0; i < sameList.size(); i++) {
                                            // 当前连接的wifi账号
                                            String currentConnSSID = wifiAdmin.getSSID().replaceAll("\"", "").trim();
                                            String currentSSID = (sameList.get(i).SSID).replaceAll("\"", "").trim();
                                            if (!currentConnSSID.equals(currentSSID)) {
                                                //// TODO
                                                WifiConfiguration configuration = wifiAdmin.isExsits(sameList.get(i).SSID);
                                                wifiAdmin.disconnectWifi(wifiAdmin.getNetworkId());
                                                wifiAdmin.connectConfigID(configuration);
                                                sendMsg();
                                                Thread.sleep(2000);
//                                                Log.d(TAG, "name:" + wifiAdmin.getSSID() + " 发送了消息");
                                                wifiAdmin = new WifiAdmin(TraceServiceImpl.this);
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    flag = false;
                                }
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 发送消息
     */
    private void sendMsg() {
        Log.d(TAG, "send MSG");
        //MAC，手机串号和SIM卡号
        Log.d(TAG, "------");

    }


    @Override
    public void stopWork(Intent intent, int flags, int startId) {
        stopService();
    }

    /**
     * 任务是否正在运行?
     *
     * @return 任务正在运行, true; 任务当前不在运行, false; 无法判断, 什么也不做, null.
     */
    @Override
    public Boolean isWorkRunning(Intent intent, int flags, int startId) {
        //若还没有取消订阅, 就说明任务仍在运行.
        return sDisposable != null && !sDisposable.isDisposed();
    }

    @Override
    public IBinder onBind(Intent intent, Void v) {
        return null;
    }

    @Override
    public void onServiceKilled(Intent rootIntent) {
        Log.d(TAG, "服务被干掉");
    }

    class CompareLevel implements Comparator<ScanResult> {

        @Override
        public int compare(ScanResult a, ScanResult b) {
            return a.level - b.level;
        }
    }
}
