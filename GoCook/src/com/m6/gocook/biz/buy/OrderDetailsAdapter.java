package com.m6.gocook.biz.buy;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.m6.gocook.R;
import com.m6.gocook.base.entity.response.COrderQueryResult.COrderWareItem;
import com.m6.gocook.util.cache.util.ImageFetcher;

public class OrderDetailsAdapter extends BaseAdapter {
	
	private Context mContext;
	private LayoutInflater mInflater;
	private List<COrderWareItem> rows;
	private ImageFetcher mImageFetcher;
	
	public OrderDetailsAdapter(Context context, ImageFetcher imageFetcher) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mImageFetcher = imageFetcher;
	}

	@Override
	public int getCount() {
		return rows == null ? 0 : rows.size();
	}

	@Override
	public Object getItem(int position) {
		return rows == null ? null : rows.get(position);
	}

	@Override
	public long getItemId(int position) {
		if (rows == null || rows.get(position) == null) {
			return 0;
		}
		return rows.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.adapter_buy_order_details, parent, false);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.norm = (TextView) convertView.findViewById(R.id.norm);
			holder.unit = (TextView) convertView.findViewById(R.id.unit);
			holder.price = (TextView) convertView.findViewById(R.id.price);
			holder.count = (TextView) convertView.findViewById(R.id.count);
			holder.cost = (TextView) convertView.findViewById(R.id.cost);
			holder.method = (TextView) convertView.findViewById(R.id.method);
			holder.cover = (ImageView) convertView.findViewById(R.id.cover);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		COrderWareItem item = rows.get(position);
		holder.name.setText(item.getName());
		holder.norm.setText(item.getNorm());
		holder.unit.setText(item.getUnit());
		holder.price.setText(String.valueOf(item.getPrice()));
		holder.count.setText(String.valueOf(item.getQuantity()));
		holder.cost.setText(String.valueOf(item.getCost()));
		holder.method.setText(item.getDealMethod());
		mImageFetcher.loadImage(item.getImageUrl(), holder.cover);
		return convertView;
	}
	
	class ViewHolder {
		private TextView name;
		private TextView norm;
		private TextView unit;
		private TextView price;
		private TextView count;
		private TextView cost;
		private TextView method;
		private ImageView cover;
	}

}
