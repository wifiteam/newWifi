package com.xdandroid.sample;

import android.Manifest;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.xdandroid.sample.lib.IntentWrapper;
import com.xdandroid.sample.adapter.MyAdapter;
import com.xdandroid.sample.utils.ToastUtils;
import com.xdandroid.sample.utils.WifiAdmin;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    // 权限请求码
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 100;
    private  List<ScanResult> mWifiList = new ArrayList<>();
    // 显示附近wifi列表
    private ListView mListView;
    private EditText etWifiSSid1;
    private EditText etWifiSSid2;
    private EditText etWifiPsd1;
    private EditText etWifiPsd2;

    private MyAdapter mAdapter;
    // 循环订阅事件
    private  Disposable sDisposable;
    WifiAdmin wifiAdmin;

    public static MainActivity instance;

    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);
        instance = this;
        initViews();
        // 请求6.0权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionGen.with(MainActivity.this)
                    .addRequestCode(REQUEST_CODE_ASK_PERMISSIONS)
                    .permissions(Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                            ,Manifest.permission.ACCESS_WIFI_STATE)
                    .request();
        }else{
            afterRequestPermission();
        }

    }

    private void afterRequestPermission(){
        wifiAdmin = new WifiAdmin(this);
        if(!wifiAdmin.checkWifi()){
            Log.d(TAG,"MainActivity wifi未开启,需要开启wifi");
            // 开启wifi
            wifiAdmin.openWifi();
        }else{
            Log.d(TAG,"MainActivity wifi已开启");
        }
    }

    private void initViews() {
        etWifiSSid1 = (EditText) this.findViewById(R.id.et_1_ssid);
        etWifiSSid2 = (EditText) this.findViewById(R.id.et_2_ssid);
        etWifiPsd1 = (EditText) this.findViewById(R.id.et_1_psd);
        etWifiPsd2 = (EditText) this.findViewById(R.id.et_2_psd);
        mListView = (ListView) this.findViewById(R.id.wifi_list_view);
        // 模拟手动输入
        //A
        etWifiSSid1.setText("k1");
        etWifiPsd1.setText("5201314.");
        // B
        etWifiSSid2.setText("i6");
        etWifiPsd2.setText("123456789");
        // 初始化附近wifi列表
        mAdapter = new MyAdapter(this,mWifiList);
        mListView.setAdapter(mAdapter);
    }


    /**
     * 刷新 service中使用注意线程问题 只能在UI线程中更新
     * @param wifiList
     */
    public void upDateListView(final List<ScanResult> wifiList){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWifiList.clear();
                mWifiList.addAll(wifiList);
                Log.d(TAG,"wifiList Size = " + wifiList.size());
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    /**
     * Button的点击事件
     * @param v
     */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                try {
                    Log.d(TAG,"点击启动服务");
                    TraceServiceImpl.sShouldStopService = false;
                    startService(new Intent(this, TraceServiceImpl.class));
                } catch (Exception ignored) {
                }
                break;
            case R.id.btn_white:
                IntentWrapper.whiteListMatters(this, "轨迹跟踪服务的持续运行");
                break;
            case R.id.btn_stop:
                Log.d(TAG,"点击停止服务");
                TraceServiceImpl.stopService();
                break;
            case R.id.add_wifi:
                if(etWifiSSid1.getText().toString().trim().equals("")
                        || etWifiSSid2.getText().toString().trim().equals("")
                    || etWifiPsd1.getText().toString().trim().equals("")
                    || etWifiPsd2.getText().toString().trim().equals(""))
                {
                    ToastUtils.toast(this,"请填写完整录入wifi数据");
                    return;
                }
                // 添加配置网络
                if(wifiAdmin!=null) {
                    WifiConfiguration configuration1 = wifiAdmin.createWifiInfo(
                            etWifiSSid1.getText().toString().trim(), etWifiPsd1.getText().toString().trim(),
                            WifiAdmin.WifiCipherType.WIFICIPHER_WPA);
                    WifiConfiguration configuration2 = wifiAdmin.createWifiInfo(
                            etWifiSSid2.getText().toString().trim(), etWifiPsd2.getText().toString().trim(),
                            WifiAdmin.WifiCipherType.WIFICIPHER_WPA);
                    boolean c2 = wifiAdmin.addNetwork(configuration2);
                    boolean c1 = wifiAdmin.addNetwork(configuration1);
                    Log.d(TAG, "-----------------------");
                    Log.d(TAG, "wifi1 连接状态 = " + c1);
                    Log.d(TAG, "wifi2 连接状态 = " + c2);
                    Log.d(TAG, "-----------------------");
                }
                break;
            case R.id.hide:
                // 隐藏界面
                IntentWrapper.onBackPressed(this);
                break;
        }
    }

    //防止华为机型未加入白名单时按返回键回到桌面再锁屏后几秒钟进程被杀
    @Override
    public void onBackPressed() {
        IntentWrapper.onBackPressed(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = REQUEST_CODE_ASK_PERMISSIONS)
    public void doPermissionSuccess() {
        Toast.makeText(this, "设备权限请求成功", Toast.LENGTH_SHORT).show();
        // 权限请求成功
        afterRequestPermission();
    }

    /**
     * 权限请求失败
     */
    @PermissionFail(requestCode = REQUEST_CODE_ASK_PERMISSIONS)
    public void doPermissionFail() {
        Toast.makeText(this, "设备权限请求失败", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(instance!=null)
            instance=null;
    }
}
