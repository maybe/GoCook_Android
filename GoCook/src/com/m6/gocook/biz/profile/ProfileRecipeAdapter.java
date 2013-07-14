package com.m6.gocook.biz.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.m6.gocook.R;
import com.m6.gocook.base.entity.RecipeList;
import com.m6.gocook.base.protocol.ProtocolUtils;
import com.m6.gocook.util.cache.util.ImageFetcher;

public class ProfileRecipeAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private RecipeList mData;
	private ImageFetcher mImageFetcher;
	
	public ProfileRecipeAdapter(Context context, ImageFetcher imageFetcher, RecipeList data) {
		mInflater = LayoutInflater.from(context);
		mData = data;
		mImageFetcher = imageFetcher;
	}
	
	@Override
	public int getCount() {
		if(mData == null || mData.getRecipes() == null) {
			return 0;
		}
		int size = mData.getRecipes().size();
		return size <= 3 ? size : 3;
	}

	@Override
	public Object getItem(int position) {
		if (mData != null) {
			return mData.getRecipes().get(position);
		} else {
			return null;
		}
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
		
		RecipeList.RecipeItem item = mData.getRecipes().get(position);
		mImageFetcher.loadImage(ProtocolUtils.getURL(item.getImage()), holder.image);
		holder.title.setText(item.getName());
		return convertView;
	}
	
	private class ViewHold {
		private ImageView image;
		private TextView title;
	}

}
