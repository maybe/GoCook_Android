package com.m6.gocook.biz.buy;

import java.util.List;
import java.util.Map;

import com.m6.gocook.R;
import com.m6.gocook.base.db.table.RecipeMaterialPurchaseList;
import com.m6.gocook.util.model.ModelUtils;

import android.content.Context;
import android.content.res.Resources;
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
			holder.price = (TextView) convertView.findViewById(R.id.price);
			holder.process = (TextView) convertView.findViewById(R.id.process);
			holder.gotoShop = (Button) convertView.findViewById(R.id.gotoshop);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		// bind data
		Map<String, Object> map = mData.get(position);
		holder.target.setText(mResources.getString(R.string.biz_buy_list_adapter_target, ModelUtils.getStringValue(map, RecipeMaterialPurchaseList.MATERIAL_NAME)));
		
		holder.gotoShop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, R.string.biz_buy_incompleted, Toast.LENGTH_SHORT).show();
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
		TextView price;
		TextView process;
		Button gotoShop;
	}

}
