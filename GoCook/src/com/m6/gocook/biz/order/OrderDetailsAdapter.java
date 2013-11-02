package com.m6.gocook.biz.order;

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
	private List<COrderWareItem> mRows;
	private ImageFetcher mImageFetcher;
	
	public OrderDetailsAdapter(Context context, ImageFetcher imageFetcher, List<COrderWareItem> rows) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mImageFetcher = imageFetcher;
		mRows = rows;
	}

	@Override
	public int getCount() {
		return mRows == null ? 0 : mRows.size();
	}

	@Override
	public Object getItem(int position) {
		return mRows == null ? null : mRows.get(position);
	}

	@Override
	public long getItemId(int position) {
		if (mRows == null || mRows.get(position) == null) {
			return 0;
		}
		return mRows.get(position).getId();
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
		
		COrderWareItem item = mRows.get(position);
		holder.name.setText(mContext.getString(R.string.biz_buy_order_details_item_name, item.getName()));
		holder.norm.setText(mContext.getString(R.string.biz_buy_order_details_item_norm, item.getNorm()));
		holder.unit.setText(mContext.getString(R.string.biz_buy_order_details_item_unit, item.getUnit()));
		holder.price.setText(mContext.getString(R.string.biz_buy_order_details_item_price, String.valueOf(item.getPrice())));
		holder.count.setText(mContext.getString(R.string.biz_buy_order_details_item_count, String.valueOf(item.getQuantity())));
		holder.cost.setText(mContext.getString(R.string.biz_buy_order_details_item_cost, String.valueOf(item.getCost())));
		holder.method.setText(mContext.getString(R.string.biz_buy_order_details_item_method, item.getDealMethod()));
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
