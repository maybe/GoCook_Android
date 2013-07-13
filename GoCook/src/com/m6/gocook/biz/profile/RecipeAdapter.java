package com.m6.gocook.biz.profile;

import java.util.List;
import java.util.Map;

import com.m6.gocook.R;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.base.protocol.ProtocolUtils;
import com.m6.gocook.util.cache.util.ImageFetcher;
import com.m6.gocook.util.model.ModelUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RecipeAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<Map<String, Object>> mData;
	private ImageFetcher mImageFetcher;
	
	public RecipeAdapter(Context context, ImageFetcher imageFetcher, List<Map<String, Object>> data) {
		mInflater = LayoutInflater.from(context);
		mData = data;
		mImageFetcher = imageFetcher;
	}
	
	@Override
	public int getCount() {
		return mData == null ? 0 : mData.size();
	}

	@Override
	public Map<String, Object> getItem(int position) {
		return mData == null ? null : (position < mData.size() ? mData.get(position) : null);
	}

	@Override
	public long getItemId(int position) {
		return position;
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
		
		mImageFetcher.loadImage(ProtocolUtils.getURL(ModelUtils.getStringValue(mData.get(position), Protocol.KEY_RECIPE_LIST_IMAGE)), holder.image);
		holder.title.setText(ModelUtils.getStringValue(mData.get(position), Protocol.KEY_RECIPE_LIST_NAME));
		return convertView;
	}
	
	private class ViewHold {
		private ImageView image;
		private TextView title;
	}

}
