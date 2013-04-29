package com.m6.gocook.biz.recipe.recipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.m6.gocook.R;

public class RecipeRelatedRecipesAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	
	public RecipeRelatedRecipesAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return 6;
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
			convertView = mInflater.inflate(R.layout.adapter_recipe_related_recipes, null);
			holder = new ViewHold();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.image = (ImageView) convertView.findViewById(R.id.image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHold) convertView.getTag();
		}
		holder.name.setText("什么菜什么菜");
		return convertView;
	}
	
	private class ViewHold {
		private TextView name;
		private ImageView image;
	}

}
