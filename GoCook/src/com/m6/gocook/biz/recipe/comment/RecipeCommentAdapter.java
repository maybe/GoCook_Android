package com.m6.gocook.biz.recipe.comment;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.m6.gocook.R;
import com.m6.gocook.base.entity.RecipeCommentList;
import com.m6.gocook.base.entity.RecipeCommentList.RecipeCommentItem;
import com.m6.gocook.base.protocol.ProtocolUtils;
import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.biz.profile.ProfileFragment;
import com.m6.gocook.util.cache.util.ImageFetcher;

public class RecipeCommentAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private RecipeCommentList mList;
	
	private Context mContext;

	private ImageFetcher mImageFetcher;
	
	public RecipeCommentAdapter(Context context, RecipeCommentList list, ImageFetcher fetcher) {
		mInflater = LayoutInflater.from(context);
		mList = list;
		mImageFetcher = fetcher;
		mContext = context;
	}
	
	public void setData(RecipeCommentList data) {
		if (data != null) {
			mList = data;
		}
	}
	
	@Override
	public int getCount() {
		return mList == null ? 0 : mList.getCount();
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
		final RecipeCommentItem item = (RecipeCommentItem) getItem(position);
		holder.content.setText(item.getContent());
		holder.date.setText(item.getCreateTime());
		holder.avatar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(item.getUserId())) {
					return;
				}
				
				ProfileFragment.startProfileFragment(mContext,
						item.getUserId().equals(AccountModel.getUserId(mContext)) ? 
								ProfileFragment.PROFILE_MYSELF : ProfileFragment.PROFILE_OTHERS, 
								item.getUserId());
			}
		});
		if(!TextUtils.isEmpty(item.getPortrait())) {
			mImageFetcher.loadImage(ProtocolUtils.getURL(item.getPortrait()), holder.avatar);
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
