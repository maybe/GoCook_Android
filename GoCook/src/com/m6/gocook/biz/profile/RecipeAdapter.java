package com.m6.gocook.biz.profile;

import com.m6.gocook.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RecipeAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	
	public RecipeAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return 3;
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
			convertView = mInflater.inflate(R.layout.adapter_profile_recipe, null);
			holder = new ViewHold();
			holder.image = (ImageView) convertView.findViewById(R.id.image);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHold) convertView.getTag();
		}
		
		holder.image.setImageResource(R.drawable.register_photo);
		holder.title.setText("蜜汁鸡翅");
		return convertView;
	}
	
	private class ViewHold {
		private ImageView image;
		private TextView title;
	}

}
