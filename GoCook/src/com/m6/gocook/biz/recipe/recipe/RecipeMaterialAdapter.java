package com.m6.gocook.biz.recipe.recipe;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.m6.gocook.R;
import com.m6.gocook.base.entity.RecipeEntity;
import com.m6.gocook.base.entity.RecipeEntity.Material;

public class RecipeMaterialAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<Material> mMaterials;
	
	public RecipeMaterialAdapter(Context context, ArrayList<Material> materials) {
		mInflater = LayoutInflater.from(context);
		mMaterials = materials;
	}
	
	@Override
	public int getCount() {
		if(mMaterials != null) {
			return mMaterials.size();
		}
		return 0;
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
			convertView = mInflater.inflate(R.layout.adapter_recipe_material_item, null);
			holder = new ViewHold();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.remark = (TextView) convertView.findViewById(R.id.remark);
			convertView.setTag(holder);
		} else {
			holder = (ViewHold) convertView.getTag();
		}
		
		Material material = mMaterials.get(position);
		holder.name.setText(material.getName());
		holder.remark.setText(material.getRemark());
		return convertView;
	}
	
	private class ViewHold {
		private TextView name;
		private TextView remark;
	}

}
