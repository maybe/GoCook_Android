package com.m6.gocook.biz.buy;

import java.util.List;
import java.util.Map;

import com.m6.gocook.R;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.db.table.RecipeMaterialPurchaseList;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.biz.main.MainActivityHelper;
import com.m6.gocook.util.File.StringUtils;
import com.m6.gocook.util.model.ModelUtils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BuyListAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private Resources mResources;
	
	private List<Map<String, Object>> mData;
	
	public BuyListAdapter(Context context, List<Map<String, Object>> data) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mData = data;
		mResources = context.getResources();
	}
	
	@Override
	public int getCount() {
		return mData == null ? 0 : mData.size();
	}

	@Override
	public Object getItem(int position) {
		return  mData == null ? null : mData.get(position);
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
			convertView = mInflater.inflate(R.layout.adapter_buy_list, parent, false);
			holder.target = (TextView) convertView.findViewById(R.id.target);
			holder.status = (TextView) convertView.findViewById(R.id.status);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.format = (TextView) convertView.findViewById(R.id.format);
			holder.num = (TextView) convertView.findViewById(R.id.num);
			holder.cost = (TextView) convertView.findViewById(R.id.cost);
			holder.method = (TextView) convertView.findViewById(R.id.process);
			holder.gotoShop = (Button) convertView.findViewById(R.id.gotoshop);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		// bind data
		Map<String, Object> map = mData.get(position);
		final String target = ModelUtils.getStringValue(map, RecipeMaterialPurchaseList.MATERIAL_NAME);
		holder.target.setText(mResources.getString(R.string.biz_buy_list_adapter_target, target));
		
		// after order
		String name = ModelUtils.getStringValue(map, BuyModel.NAME);
		if (!TextUtils.isEmpty(name)) {
			holder.name.setText(name);
			holder.name.setVisibility(View.VISIBLE);
		} else {
			holder.name.setVisibility(View.GONE);
		}
		
		double quantity = ModelUtils.getDoubleValue(map, BuyModel.QUANTITY, 0.00d);
		double price = ModelUtils.getDoubleValue(map, BuyModel.PRICE, 0.00d);
		String unit = ModelUtils.getStringValue(map, BuyModel.UNIT);
		if (!TextUtils.isEmpty(unit) && quantity > 0) {
			String num = "￥" + price + "/" + unit + " x " + quantity + unit;
			holder.num.setText(num);
			String cost = "=￥" + quantity * price;
			holder.cost.setText(cost);
			
			holder.num.setVisibility(View.VISIBLE);
			holder.cost.setVisibility(View.VISIBLE);
			holder.status.setVisibility(View.GONE);
		} else {
			holder.num.setVisibility(View.GONE);
			holder.cost.setVisibility(View.GONE);
			holder.status.setVisibility(View.VISIBLE);
		}
		
		String norm = ModelUtils.getStringValue(map, BuyModel.NORM);
		if (!TextUtils.isEmpty(norm)) {
			holder.format.setText(mResources.getString(R.string.biz_buy_search_adapter_norm, norm));
			holder.format.setVisibility(View.VISIBLE);
		} else {
			holder.format.setVisibility(View.GONE);
		}
		
		String method = ModelUtils.getStringValue(map, BuyModel.METHOD);
		if (!TextUtils.isEmpty(method)) {
			holder.method.setText(mResources.getString(R.string.biz_buy_search_adapter_method, method));
			holder.method.setVisibility(View.VISIBLE);
		} else {
			holder.method.setVisibility(View.GONE);
		}
		
		final String id = ModelUtils.getStringValue(map, RecipeMaterialPurchaseList._ID);
		holder.gotoShop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle args = new Bundle();
				args.putString(BuySearchFragment.PARAM_RECORD_ID, id);
				args.putString(BuySearchFragment.PARAM_KEYWORD, target);
				Intent intent = FragmentHelper.getIntent(mContext, BaseActivity.class, 
						BuySearchFragment.class.getName(), BuySearchFragment.class.getName(), args);
				((FragmentActivity) mContext).startActivityForResult(intent, MainActivityHelper.REQUEST_CODE_INPUT);
			}
		});
		return convertView;
	}
	
	class ViewHolder {
		TextView target;
		TextView status;
		TextView name;
		TextView format;
		TextView num;
		TextView cost;
		TextView method;
		Button gotoShop;
	}

}
