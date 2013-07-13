package com.m6.gocook.biz.recipe.comment;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.m6.gocook.R;
import com.m6.gocook.base.entity.RecipeCommentList;
import com.m6.gocook.base.entity.RecipeCommentList.RecipeCommentItem;
import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.util.cache.util.ImageFetcher;

public class RecipeCommentAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private RecipeCommentList mList;

	private ImageFetcher mImageFetcher;
	
	public RecipeCommentAdapter(Context context, RecipeCommentList list, ImageFetcher fetcher) {
		mInflater = LayoutInflater.from(context);
		mList = list;
		mImageFetcher = fetcher;
	}
	
	@Override
	public int getCount() {
		return mList.getCount();
	}

	@Override
	public Object getItem(int position) {
		return mList.getItem(position);
	}

	@Override
	public long getItemId(int position) {
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
			holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
			convertView.setTag(holder);
		} else {
			holder = (ViewHold) convertView.getTag();
		}
		RecipeCommentItem item = (RecipeCommentItem) getItem(position);
		holder.content.setText(item.getContent());
		holder.date.setText(item.getCreateTime());
		if(!TextUtils.isEmpty(item.getPortrait())) {
			mImageFetcher.loadImage(item.getPortrait(), holder.avatar);
		} else {
			holder.avatar.setImageResource(R.drawable.register_photo);
		}
		return convertView;
	}
	
	private class ViewHold {
		private TextView content;
		private TextView date;
		private ImageView avatar;
	}
	
	public void addItem(RecipeCommentItem item) {
		mList.addItem(item);
	}

}
