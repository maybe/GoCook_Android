package com.m6.gocook.biz.buy;

import com.m6.gocook.R;
import com.m6.gocook.base.entity.response.CKeywordQueryResult;
import com.m6.gocook.base.entity.response.CWareItem;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BuySearchAdapter extends BaseAdapter {

	private LayoutInflater minInflater;
	private Resources mResources;
	private CKeywordQueryResult mCKeywordQueryResult;
	
	public BuySearchAdapter(Context context, CKeywordQueryResult cKeywordQueryResult) {
		minInflater = LayoutInflater.from(context);
		mResources = context.getResources();
		mCKeywordQueryResult = cKeywordQueryResult;
	}
	
	public void setData(CKeywordQueryResult cKeywordQueryResult) {
		if (mCKeywordQueryResult == null) {
			mCKeywordQueryResult = cKeywordQueryResult;
		}
		mCKeywordQueryResult.getRows().addAll(cKeywordQueryResult.getRows());
	}
	
	@Override
	public int getCount() {
		if (mCKeywordQueryResult == null || mCKeywordQueryResult.getRows() == null) {
			return 0;
		}
		return mCKeywordQueryResult.getRows().size();
	}

	@Override
	public CWareItem getItem(int position) {
		if (mCKeywordQueryResult != null && mCKeywordQueryResult.getRows() != null) {
			return mCKeywordQueryResult.getRows().get(position);
		}
		return null;
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
			convertView = minInflater.inflate(R.layout.adapter_buy_search, parent, false);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.norm = (TextView) convertView.findViewById(R.id.norm);
			holder.unit = (TextView) convertView.findViewById(R.id.unit);
			holder.price = (TextView) convertView.findViewById(R.id.price);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		CWareItem item = mCKeywordQueryResult.getRows().get(position);
		holder.name.setText(item.getName());
		holder.norm.setText(mResources.getString(R.string.biz_buy_search_adapter_norm, item.getNorm()));
		holder.unit.setText(mResources.getString(R.string.biz_buy_search_adapter_unit, item.getUnit()));
		holder.price.setText(mResources.getString(R.string.biz_buy_search_adapter_price, String.valueOf(item.getPrice())));
		return convertView;
	}
	
	class ViewHolder {
		private TextView name;
		private TextView norm;
		private TextView unit;
		private TextView price;
	}

}
