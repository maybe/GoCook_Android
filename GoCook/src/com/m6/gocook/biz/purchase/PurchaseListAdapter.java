package com.m6.gocook.biz.purchase;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.m6.gocook.R;
import com.m6.gocook.base.db.table.RecipeMaterialPurchaseList;
import com.m6.gocook.base.db.table.RecipePurchaseList;
import com.m6.gocook.base.entity.RecipeEntity;
import com.m6.gocook.base.entity.RecipeEntity.Material;
import com.m6.gocook.biz.recipe.RecipeActivity;

public class PurchaseListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Cursor mCursor;
	private Context mContext;
	
	public PurchaseListAdapter(Context context, Cursor cursor) {
		mInflater = LayoutInflater.from(context);
		mCursor = cursor;
		mContext = context;
		if(mCursor != null) {
			mCursor.moveToFirst();
		}
		
	}
	
	@Override
	public int getCount() {
		if(mCursor != null) {
			return mCursor.getCount();
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
			convertView = mInflater.inflate(R.layout.adapter_purchase_recipe_list_item, null);
			holder = new ViewHold();
			holder.name = (TextView) convertView.findViewById(R.id.recipe_name);
			holder.materialGroup = (LinearLayout) convertView.findViewById(R.id.material_group_layout);
			convertView.setTag(holder);
		} else {
			holder = (ViewHold) convertView.getTag();
		}
		
		holder.name.setText(mCursor.getString(mCursor.getColumnIndex(RecipePurchaseList.RECIPE_NAME)));
		holder.name.setTag(mCursor.getInt(mCursor.getColumnIndex(RecipePurchaseList.RECIPE_ID)));
		
		// Add materials
		holder.materialGroup.removeAllViews();
		Cursor materialCursor = PurchaseListModel.getRecipeMaterialPurchaseListCursor(mContext, holder.name.getTag().toString());
		if (materialCursor.moveToFirst()) {
			do {
				String name = materialCursor.getString(
						materialCursor.getColumnIndex(RecipeMaterialPurchaseList.MATERIAL_NAME));
				String remark = materialCursor.getString(
						materialCursor.getColumnIndex(RecipeMaterialPurchaseList.MATERIAL_REMARK));
				View material = createMaterial(name, remark);
				holder.materialGroup.addView(material);

				
			} while (materialCursor.moveToNext());
		}
		
		View bgView = convertView.findViewById(R.id.recipe_title_bg);
		final View deleteButtonView = convertView.findViewById(R.id.delete_item_image);
		bgView.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				deleteButtonView.setVisibility(View.VISIBLE);
				return true;
			}
		});
		
		bgView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mContext, RecipeActivity.class);
				mContext.startActivity(intent);
			}
		});
		
		return convertView;
	}
	
	
	private View createMaterial(String name, String remark) {
		
		View view = mInflater.inflate(R.layout.adapter_purchase_recipe_material_list_item, null);
		TextView nameView = (TextView) view.findViewById(R.id.name);
		nameView.setText(name);
		TextView remarkView = (TextView) view.findViewById(R.id.remark);
		remarkView.setText(remark);
		
		View materialItemView = view.findViewById(R.id.material_item);
		final View lineSwipeImage = view.findViewById(R.id.line_swipe);
		
		materialItemView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				lineSwipeImage.setVisibility((lineSwipeImage.getVisibility() == View.VISIBLE) ?
						View.INVISIBLE : View.VISIBLE);
			}
		});
		return view;
	}
	
	private class ViewHold {
		private TextView name;
		private LinearLayout materialGroup;
	}

}
