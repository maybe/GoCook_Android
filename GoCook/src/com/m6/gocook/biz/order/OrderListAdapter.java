package com.m6.gocook.biz.order;

import com.m6.gocook.R;
import com.m6.gocook.base.entity.response.COrderQueryResult;
import com.m6.gocook.base.entity.response.COrderQueryResult.COrderItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OrderListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private COrderQueryResult mCOrderQueryResult;
	
	public OrderListAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
	}
	
	public void setData(COrderQueryResult cOrderQueryResult) {
		mCOrderQueryResult = cOrderQueryResult;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		if (mCOrderQueryResult == null || mCOrderQueryResult.getRows() == null) {
			return 0;
		}
		return mCOrderQueryResult.getRows().size();
	}

	@Override
	public COrderItem getItem(int position) {
		if (mCOrderQueryResult == null || mCOrderQueryResult.getRows() == null) {
			return null;
		}
		return mCOrderQueryResult.getRows().get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.adapter_buy_order_list, parent, false);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.materials = (TextView) convertView.findViewById(R.id.materials);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		COrderItem orderItem = mCOrderQueryResult.getRows().get(position);
		holder.time.setText(orderItem.getCreateTime());
		holder.materials.setText(orderItem.getOrderWaresInfo());
		return convertView;
	}
	
	class ViewHolder {
		private TextView time;
		private TextView materials;
	}

}
