package com.m6.gocook.biz.recipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.m6.gocook.R;

public class RecipeprocedureAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	
	public RecipeprocedureAdapter(Context context) {
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
			convertView = mInflater.inflate(R.layout.adapter_recipe_procedure_item, null);
			holder = new ViewHold();
			holder.name = (TextView) convertView.findViewById(R.id.index);
			holder.remark = (TextView) convertView.findViewById(R.id.item);
			convertView.setTag(holder);
		} else {
			holder = (ViewHold) convertView.getTag();
		}
		
		holder.name.setText(String.valueOf(position + 1));
		if(position % 2 == 0) {
			holder.remark.setText("切切块切块切块切块切块切块切块切块切块");
			
		} else {
			holder.remark.setText("切块切块切块切块切块切块切块切块切块切块切块切块切块切块切块切块切块切块切块切块切块切块切块切块切块切块切块切块切块切块切块切块切块切块切块");
			
		}
		return convertView;
	}
	
	private class ViewHold {
		private TextView name;
		private TextView remark;
	}

}
