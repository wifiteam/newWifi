package com.xdandroid.sample.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xdandroid.sample.R;

import java.util.List;

public class MyAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<ScanResult> list;
    private Context mContext;

    public MyAdapter(Context context, List<ScanResult> list) {
        this.inflater = LayoutInflater.from(context);
        this.mContext = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.item_wifi_list, null);

        TextView textView = (TextView) convertView.findViewById(R.id.textView);
        TextView signal = (TextView) convertView.findViewById(R.id.signal_strenth);
        TextView tvLevel = (TextView) convertView.findViewById(R.id.tv_level);

        ScanResult scanResult = list.get(position);
        textView.setText(scanResult.SSID);
        signal.setText(String.valueOf(Math.abs(scanResult.level)));

        int level = WifiManager.calculateSignalLevel(scanResult.level, 100);

        //判断信号强度，显示对应的指示图标
        if (Math.abs(scanResult.level) > 100) {
            tvLevel.setText("无信号");
        } else if (Math.abs(scanResult.level) > 80) {
            tvLevel.setText("差");
        } else if (Math.abs(scanResult.level) > 70) {
            tvLevel.setText("较差");
        } else if (Math.abs(scanResult.level) > 60) {
            tvLevel.setText("一般");
        } else if (Math.abs(scanResult.level) > 50) {
            tvLevel.setText("较好");
        } else {
            tvLevel.setText("强");
        }
        return convertView;

    }


}