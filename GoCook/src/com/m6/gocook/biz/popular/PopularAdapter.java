package com.m6.gocook.biz.popular;

import com.m6.gocook.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PopularAdapter extends BaseAdapter {
	
	private static final int VIEW_TYPE_HEADER = 0;
	
	private static final int VIEW_TYPE_NORMAL = 1;
	
	private LayoutInflater mInflater;
	
	public PopularAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return 0;
	}
	
	@Override
	public int getItemViewType(int position) {
		if(position == 0) {
			return VIEW_TYPE_HEADER;
		} else {
			return VIEW_TYPE_NORMAL;
		}
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HeaderViewHolder headerHolder;
		NormalViewHolder normalHolder;
		int type = getItemViewType(position);
		if(convertView == null) {
			if(type == VIEW_TYPE_HEADER) {
				convertView = mInflater.inflate(R.layout.adapter_popular_header_item, null);
				headerHolder = new HeaderViewHolder();
				headerHolder.image1 = (ImageView) convertView.findViewById(R.id.image1);
				headerHolder.image2 = (ImageView) convertView.findViewById(R.id.image2);
				headerHolder.text1 = (TextView) convertView.findViewById(R.id.text1);
				headerHolder.text2 = (TextView) convertView.findViewById(R.id.text2);
				convertView.setTag(headerHolder);
			} else if(type == VIEW_TYPE_NORMAL) {
				convertView = mInflater.inflate(R.layout.adapter_popular_normal_item, null);
				normalHolder = new NormalViewHolder();
				normalHolder.title = (TextView) convertView.findViewById(R.id.title);
				normalHolder.image1 = (ImageView) convertView.findViewById(R.id.image1);
				normalHolder.image2 = (ImageView) convertView.findViewById(R.id.image2);
				normalHolder.image3 = (ImageView) convertView.findViewById(R.id.image3);
				normalHolder.image4 = (ImageView) convertView.findViewById(R.id.image4);
				convertView.setTag(normalHolder);
			}
		} else {
			if(type == VIEW_TYPE_HEADER) {
				headerHolder = (HeaderViewHolder) convertView.getTag();
			} else if(type == VIEW_TYPE_NORMAL) {
				normalHolder = (NormalViewHolder) convertView.getTag();
			}
		}
		
		if(type == VIEW_TYPE_HEADER) {
			
		} else if(type == VIEW_TYPE_NORMAL) {
			
		}
		return convertView;
	}
	
	class HeaderViewHolder {
		private ImageView image1;
		private ImageView image2;
		private TextView text1;
		private TextView text2;
	}
	
	class NormalViewHolder {
		private TextView title;
		private ImageView image1;
		private ImageView image2;
		private ImageView image3;
		private ImageView image4;
	}

}
