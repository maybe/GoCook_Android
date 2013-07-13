package com.m6.gocook.biz.recipe.comment;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.m6.gocook.R;
import com.m6.gocook.base.entity.RecipeCommentList;
import com.m6.gocook.base.entity.RecipeCommentList.RecipeCommentItem;
import com.m6.gocook.biz.account.AccountModel;

public class RecipeCommentAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private RecipeCommentList mList;
	
	public RecipeCommentAdapter(Context context, RecipeCommentList list) {
		mInflater = LayoutInflater.from(context);
		mList = list;
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
			convertView.setTag(holder);
		} else {
			holder = (ViewHold) convertView.getTag();
		}
		RecipeCommentItem item = (RecipeCommentItem) getItem(position);
		holder.content.setText(item.getContent());
		holder.date.setText(item.getCreateTime());
		return convertView;
	}
	
	private class ViewHold {
		private TextView content;
		private TextView date;
	}
	
	public void addItem(RecipeCommentItem item) {
		mList.addItem(item);
	}

}
