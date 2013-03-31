package com.m6.gocook.biz.recipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.m6.gocook.R;

public class RecipeMaterialAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	
	public RecipeMaterialAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return 11;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHold holder;
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_recipe_material_item, null);
			holder = new ViewHold();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.remark = (TextView) convertView.findViewById(R.id.remark);
			convertView.setTag(holder);
		} else {
			holder = (ViewHold) convertView.getTag();
		}
		
		holder.name.setText("土豆");
		holder.remark.setText("切块");
		return convertView;
	}
	
	private class ViewHold {
		private TextView name;
		private TextView remark;
	}

}
