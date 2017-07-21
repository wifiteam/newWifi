package com.xdandroid.sample.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.xdandroid.sample.utils.ToastUtils;
import java.util.List;

public class MYBaseAdapter extends BaseAdapter {
	public List data;
	protected Context context;
	protected SparseArray<View> viewHolder;

	public void showMsg(String message) {
		ToastUtils.toast(context, message);
	}

	public MYBaseAdapter(Context context) {
		super();
		this.context = context;
	}


	@Override
	public int getCount() {
		return data == null ? 0 : data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

	public void setData(List data) {
		this.data = data;
	}
}

