package com.m6.gocook.biz.profile;

import java.util.ArrayList;

import com.m6.gocook.R;
import com.m6.gocook.base.entity.People;
import com.m6.gocook.base.protocol.ProtocolUtils;
import com.m6.gocook.util.cache.util.ImageFetcher;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PeopleAdapter extends BaseAdapter {
	
	private ArrayList<People> mData;
	private LayoutInflater mInflater;
	private ImageFetcher mImageFetcher;
	private Context mContext;
	
	public PeopleAdapter(Context context, ImageFetcher imageFetcher, ArrayList<People> data) {
		mInflater = LayoutInflater.from(context);
		mData = data;
		mImageFetcher = imageFetcher;
		mContext = context;
	}

	@Override
	public int getCount() {
		return mData == null ? 0 : mData.size();
	}

	@Override
	public People getItem(int position) {
		return mData == null ? null : mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setPeoples(ArrayList<People> data) {
		mData = data;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.adapter_profile_people, null);
			holder.image = (ImageView) convertView.findViewById(R.id.image);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.fans = (TextView) convertView.findViewById(R.id.fans);
			holder.follows = (TextView) convertView.findViewById(R.id.follow);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.name.setText(mData.get(position).getName());
		holder.fans.setText(mContext.getString(R.string.biz_profile_myaccount_fans_count, mData.get(position).getFans()));
		holder.follows.setText(mContext.getString(R.string.biz_profile_myaccount_follows_count, mData.get(position).getFollows()));
		String url = mData.get(position).getImage();
		if (!TextUtils.isEmpty(url)) {
			mImageFetcher.loadImage(ProtocolUtils.getURL(url), holder.image);
		}
		
		holder.fans.setVisibility(View.GONE);
		holder.follows.setVisibility(View.GONE);
		
		return convertView;
	}
	
	class ViewHolder {
		private TextView name;
		private TextView fans;
		private TextView follows;
		private ImageView image;
	}

}
