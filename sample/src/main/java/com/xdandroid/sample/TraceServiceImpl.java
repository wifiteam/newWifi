package com.xdandroid.sample;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.IBinder;
import android.util.Log;

import com.xdandroid.sample.lib.AbsWorkService;
import com.xdandroid.sample.utils.WifiAdmin;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

import static com.xdandroid.sample.MainActivity.instance;


public class TraceServiceImpl extends AbsWorkService {

    private static final String TAG = "MainActivity";

    private static final int LOOP_TIME =  3;
    //是否 任务完成, 不再需要服务运行?
    public static boolean sShouldStopService;
    public static Disposable sDisposable;
    WifiAdmin wifiAdmin;

    public static void stopService() {
        //我们现在不再需要服务运行了, 将标志位置为 true
        sShouldStopService = true;
        //取消对任务的订阅
        if (sDisposable != null) sDisposable.dispose();
        //取消 Job / Alarm / Subscription
        cancelJobAlarmSub();
    }

    private void afterRequestPermission(){

        if(!wifiAdmin.checkWifi()){
            Log.d(TAG,"MainActivity wifi未开启,需要开启wifi");
            // 开启wifi
            boolean open = wifiAdmin.openWifi();
            Log.d(TAG,"service 中开启wifi成功");
        }else{
            Log.d(TAG,"MainActivity wifi已开启");
        }
//        afterOpenWifi();
    }

    /**
     * 是否 任务完成, 不再需要服务运行?
     * @return 应当停止服务, true; 应当启动服务, false; 无法判断, 什么也不做, null.
     */
    @Override
    public Boolean shouldStopService(Intent intent, int flags, int startId) {
        return sShouldStopService;
    }

    @Override
    public void startWork(Intent intent, int flags, int startId) {
        // 初始化wifi管理类
        wifiAdmin = new WifiAdmin(this);
       Log.d(TAG,"service startWork");
        sDisposable = Flowable
                .interval(LOOP_TIME, TimeUnit.SECONDS)
                //取消任务时取消定时唤醒
                .doOnTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                      Log.d(TAG,"取消了service中的订阅任务");
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


    private void doService(){
        // 判断是否开启wifi
        afterRequestPermission();
        Log.d(TAG,"扫描 wifi List");
        wifiAdmin.startScan();
        //附近范围的wifi列表 按强度由高到低显示
        List<ScanResult> wifiList = wifiAdmin.getWifiList();
        Log.d(TAG,"wifiList Size = " + wifiList.size());
        // 如果有MainActivity存在则刷新listView
        if(MainActivity.instance!=null)
            instance.upDateListView(wifiList);
        // 观察当前已配置wifi网络
        List<WifiConfiguration> configurations = wifiAdmin.getConfiguration();
        Log.d(TAG,"已配置的wifi size = " + configurations.size());

    }


    @Override
    public void stopWork(Intent intent, int flags, int startId) {
        stopService();
    }

    /**
     * 任务是否正在运行?
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
        Log.d(TAG,"服务被干掉");
    }
}
