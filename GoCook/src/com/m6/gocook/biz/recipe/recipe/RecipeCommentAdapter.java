package com.m6.gocook.biz.recipe.recipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.m6.gocook.R;

public class RecipeCommentAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	
	public RecipeCommentAdapter(Context context) {
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
			convertView = mInflater.inflate(R.layout.adapter_recipe_comment_item, null);
			holder = new ViewHold();
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.date = (TextView) convertView.findViewById(R.id.date);
			convertView.setTag(holder);
		} else {
			holder = (ViewHold) convertView.getTag();
		}
		
		holder.content.setText("做了效果很好！很好吃很好吃很好吃很好吃很好吃很好吃很好吃很好吃很好吃很好吃很好吃很好吃很好吃很好吃很好吃很好吃");
		holder.date.setText("2013-04-03 14：34：65");
		return convertView;
	}
	
	private class ViewHold {
		private TextView content;
		private TextView date;
	}

}
