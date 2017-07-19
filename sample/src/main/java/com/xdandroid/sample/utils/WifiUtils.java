package com.xdandroid.sample.utils;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 本类作用: WIFI的管理
 * Created by kGod on 2017/7/18.
 * Email 18252032703@163.com
 * qq 827746955 mobile 18252032703
 * Thank you for watching my code
 * 公司:南京红蜂网络科技有限公司 RedBee
 */

public class WifiUtils {


    private static WifiUtils instance;
    // 管理wifi
    private WifiManager mWifiManager;
    private Context mContext;
    // 扫描出的网络连接列表
    private List<ScanResult> mWifiList;
    // 配置过的网络连接列表
    private List<WifiConfiguration> mWifiConfiguration;
    // 定义WifiInfo对象
    private WifiInfo mWifiInfo;

    private WifiUtils(Context context) {
        this.mContext = context;
        //获取系统Wifi服务   WIFI_SERVICE
        this. mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //获取连接信息
        this.mWifiInfo = this.mWifiManager.getConnectionInfo();
    }

    public static WifiUtils newInstance(Context context){
        if(instance ==null){
            instance = new WifiUtils(context);
        }
        return  instance;
    }



    /**
     * 判断wifi是否可用
     *
     * @return
     */
    public boolean checkWifi() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();
        if (mWifiManager.isWifiEnabled() && ipAddress != 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 打开wifi
     */
    public void OpenWifi() {
        if(!this.mWifiManager.isWifiEnabled()){ //当前wifi不可用
            this.mWifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 关闭wifi
     */
    public void closeWifi() {
        if(mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }


    public void startScan() {
        mWifiManager.startScan();
        // 得到扫描结果
        mWifiList = mWifiManager.getScanResults();
        // 得到配置好的网络连接
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
    }

    /**
     * 附近wifi列表
     * @return
     */
    public List<ScanResult> getWifiList() {
        // 从强到弱排序
//        sortByLevel((ArrayList<ScanResult>) mWifiList);
        return mWifiList;
    }

    /**
     * 将搜索到的wifi根据信号强度从强到时弱进行排序
     * @param list  存放周围wifi热点对象的列表
     */
    private void sortByLevel(ArrayList<ScanResult> list) {

        Collections.sort(list, new Comparator<ScanResult>() {

            @Override
            public int compare(ScanResult lhs, ScanResult rhs) {
                return rhs.level - lhs.level;
            }
        });
    }

    public  String getWifiName(Context context) {
        String sWifiName;
        if ("<unknown ssid>".equals(mWifiInfo.getSSID())) {
            sWifiName = "";
        } else {
            sWifiName = mWifiInfo.getSSID();
        }
        return sWifiName;
    }

}
