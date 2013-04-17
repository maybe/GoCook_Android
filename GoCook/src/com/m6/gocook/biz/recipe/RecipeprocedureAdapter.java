package com.m6.gocook.biz.recipe;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.m6.gocook.R;
import com.m6.gocook.base.entity.RecipeEntity.Procedure;

public class RecipeProcedureAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<Procedure> mProcedures;
	
	public RecipeProcedureAdapter(Context context, ArrayList<Procedure> procedures) {
		mInflater = LayoutInflater.from(context);
		mProcedures = procedures;
	}
	
	@Override
	public int getCount() {
		if(mProcedures != null) {
			return mProcedures.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHold holder;
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_recipe_procedure_item, null);
			holder = new ViewHold();
			holder.index = (TextView) convertView.findViewById(R.id.index);
			holder.desc = (TextView) convertView.findViewById(R.id.desc);
			convertView.setTag(holder);
		} else {
			holder = (ViewHold) convertView.getTag();
		}
		
		Procedure procedure = mProcedures.get(position);
		holder.index.setText(String.valueOf(position + 1));
		holder.desc.setText(procedure.getDesc());
		
		return convertView;
	}
	
	private class ViewHold {
		private TextView index;
		private TextView desc;
		private Bitmap image;
	}

}
