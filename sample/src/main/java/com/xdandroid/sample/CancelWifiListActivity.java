package com.xdandroid.sample;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.xdandroid.sample.adapter.BaseViewHolder;
import com.xdandroid.sample.adapter.MYBaseAdapter;
import com.xdandroid.sample.utils.WifiAdmin;

import java.util.List;

/**
 * 本类作用: 取消wifi配置列表
 * Created by kGod on 2017/7/21.
 * Email 18252032703@163.com
 * qq 827746955 mobile 18252032703
 * Thank you for watching my code
 * 公司:南京红蜂网络科技有限公司 RedBee
 */

public class CancelWifiListActivity extends AppCompatActivity
{
    CancelAdapter mCancelAdapter;
    WifiAdmin wifiAdmin;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView listView = (ListView) findViewById(R.id.listview);

        wifiAdmin = new WifiAdmin(this);
        if (!wifiAdmin.checkWifi()) {
            boolean open = wifiAdmin.openWifi();
        }
        wifiAdmin.startScan();
        List<WifiConfiguration> configurations = wifiAdmin.getConfiguration();
        mCancelAdapter= new CancelAdapter(this);
        mCancelAdapter.setData(configurations);
        listView.setAdapter(mCancelAdapter);
    }



    class CancelAdapter extends MYBaseAdapter{
        WifiAdmin wifiAdmin;
        public CancelAdapter(Context context) {
            super(context);
            wifiAdmin = new WifiAdmin(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView ==null)
                convertView = View.inflate(context,R.layout.item_cancel_wifi_list,null);
            TextView tvSSid = BaseViewHolder.get(convertView,R.id.textView);
            TextView tvCancel = BaseViewHolder.get(convertView,R.id.tv_level);

            final WifiConfiguration bean = (WifiConfiguration) data.get(position);

            tvSSid.setText(bean.SSID);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  boolean isSuccess =   wifiAdmin.removeNetwork(bean.networkId);
                    if(isSuccess==true)
                    {
                        showMsg("取消成功");
                        data.remove(bean);
                        notifyDataSetChanged();
                    }else{
                        showMsg("取消失败");
                    }
                }
            });

            return convertView;
        }
    }

}
