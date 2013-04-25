package com.m6.gocook.biz.popular;

import java.util.ArrayList;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.m6.gocook.R;
import com.m6.gocook.base.protocol.ProtocolUtils;
import com.m6.gocook.util.cache.util.ImageFetcher;

public class PopularAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	
	private ImageFetcher mImageFetcher;

	private ArrayList<Pair<String, String[]>> mData = new ArrayList<Pair<String, String[]>>();
	
	public PopularAdapter(Context context, ImageFetcher imageFetcher, ArrayList<Pair<String, String[]>> data) {
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
		
		Pair<String, String[]> data = mData.get(position);
		holder.title.setText(data.first);
		String[] images = data.second;
		if(images != null) {
			int length = images.length;
			if(length >=4) {
				mImageFetcher.loadImage(ProtocolUtils.getURL(images[0]), holder.image1);
				mImageFetcher.loadImage(ProtocolUtils.getURL(images[1]), holder.image2);
				mImageFetcher.loadImage(ProtocolUtils.getURL(images[2]), holder.image3);
				mImageFetcher.loadImage(ProtocolUtils.getURL(images[3]), holder.image4);
			} else if (length  == 3) {
				mImageFetcher.loadImage(ProtocolUtils.getURL(images[0]), holder.image1);
				mImageFetcher.loadImage(ProtocolUtils.getURL(images[1]), holder.image2);
				mImageFetcher.loadImage(ProtocolUtils.getURL(images[2]), holder.image3);
			} else if (length  == 2) {
				mImageFetcher.loadImage(ProtocolUtils.getURL(images[0]), holder.image1);
				mImageFetcher.loadImage(ProtocolUtils.getURL(images[1]), holder.image2);
			} else if (length  == 1) {
				mImageFetcher.loadImage(ProtocolUtils.getURL(images[0]), holder.image1);
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
