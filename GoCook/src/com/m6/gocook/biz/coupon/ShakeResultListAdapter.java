package com.m6.gocook.biz.coupon;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.m6.gocook.R;
import com.m6.gocook.base.entity.Coupon;

public class ShakeResultListAdapter extends BaseAdapter {
	
	private List<Coupon> mData;
	private LayoutInflater mInflater;
	private Context mContext;
	
	public ShakeResultListAdapter(Context context, List<Coupon> data) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mData = data;
	}

	@Override
	public int getCount() {
		return mData == null ? 0 : mData.size();
	}

	@Override
	public Coupon getItem(int position) {
		return mData == null ? null : mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.adapter_shake_result, parent, false);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Coupon coupon = mData.get(position);
		holder.content.setText(mContext.getString(R.string.biz_coupon_shake_result_get_coupon_success,
				coupon.getVal(), coupon.getName(), coupon.getExpDay(), coupon.getCoupon(),
				coupon.getStores(), coupon.getRemark()));
		
		return convertView;
	}
	
	class ViewHolder {
		private TextView content;
	}

}
