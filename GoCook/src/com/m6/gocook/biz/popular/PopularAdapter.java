package com.m6.gocook.biz.popular;

import java.util.ArrayList;
import java.util.HashMap;

import com.m6.gocook.R;
import com.m6.gocook.base.protocol.ServerProtocol;
import com.m6.gocook.util.cache.util.ImageFetcher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PopularAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	
	private ImageFetcher mImageFetcher;

	private ArrayList<HashMap<String, Object>> mData = new ArrayList<HashMap<String,Object>>();
	
	public PopularAdapter(Context context, ImageFetcher imageFetcher, ArrayList<HashMap<String,Object>> data) {
		mInflater = LayoutInflater.from(context);
		mImageFetcher = imageFetcher;
		mData.addAll(data);
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
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
			convertView = mInflater.inflate(R.layout.adapter_popular_normal_item, null);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.image1 = (ImageView) convertView.findViewById(R.id.image1);
			holder.image2 = (ImageView) convertView.findViewById(R.id.image2);
			holder.image3 = (ImageView) convertView.findViewById(R.id.image3);
			holder.image4 = (ImageView) convertView.findViewById(R.id.image4);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		HashMap<String, Object> map = mData.get(position);
		holder.title.setText((String) map.get(ServerProtocol.KEY_POPULAR_RECOMMEND_ITEM_NAME));
		ArrayList<String> images = (ArrayList<String>) map.get(ServerProtocol.KEY_POPULAR_RECOMMEND_ITEM_IMG);
		if(images != null) {
			int length = images.size();
			if(length >=4) {
				mImageFetcher.loadImage(ServerProtocol.URL_ROOT + images.get(0), holder.image1);
				mImageFetcher.loadImage(ServerProtocol.URL_ROOT + images.get(1), holder.image2);
				mImageFetcher.loadImage(ServerProtocol.URL_ROOT + images.get(2), holder.image3);
				mImageFetcher.loadImage(ServerProtocol.URL_ROOT + images.get(3), holder.image4);
			} else if (length  == 3) {
				mImageFetcher.loadImage(ServerProtocol.URL_ROOT + images.get(0), holder.image1);
				mImageFetcher.loadImage(ServerProtocol.URL_ROOT + images.get(1), holder.image2);
				mImageFetcher.loadImage(ServerProtocol.URL_ROOT + images.get(2), holder.image3);
			} else if (length  == 2) {
				mImageFetcher.loadImage(ServerProtocol.URL_ROOT + images.get(0), holder.image1);
				mImageFetcher.loadImage(ServerProtocol.URL_ROOT + images.get(1), holder.image2);
			} else if (length  == 1) {
				mImageFetcher.loadImage(ServerProtocol.URL_ROOT + images.get(0), holder.image1);
			}
		}
		return convertView;
	}

	class ViewHolder {
		private TextView title;
		private ImageView image1;
		private ImageView image2;
		private ImageView image3;
		private ImageView image4;
	}

}
