package com.m6.gocook.biz.purchase;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.m6.gocook.R;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.db.table.RecipeMaterialPurchaseList;
import com.m6.gocook.base.db.table.RecipePurchaseList;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.biz.buy.BuyListFragment;
import com.m6.gocook.biz.recipe.recipe.RecipeFragment;

public class PurchaseListAdapter extends CursorAdapter {

	private LayoutInflater mInflater;
	

	public PurchaseListAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		mInflater = LayoutInflater.from(context);
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
			holder.delete = (Button) convertView.findViewById(R.id.delete);
			holder.buy = (Button) convertView.findViewById(R.id.buy);
			convertView.setTag(holder);
		} else {
			holder = (ViewHold) convertView.getTag();
		}
		
		mCursor.moveToPosition(position);
		holder.name.setText(mCursor.getString(mCursor.getColumnIndex(RecipePurchaseList.RECIPE_NAME)));
		holder.name.setTag(mCursor.getInt(mCursor.getColumnIndex(RecipePurchaseList.RECIPE_ID)));
		
		// Add materials
		holder.materialGroup.removeAllViews();
		Cursor materialCursor = PurchaseListModel.getRecipeMaterialPurchaseListCursorById(mContext, holder.name.getTag().toString());
		if (materialCursor.moveToFirst()) {
			do {
				String name = materialCursor.getString(
						materialCursor.getColumnIndex(RecipeMaterialPurchaseList.MATERIAL_NAME));
				String remark = materialCursor.getString(
						materialCursor.getColumnIndex(RecipeMaterialPurchaseList.MATERIAL_REMARK));
				String id = materialCursor.getString(
						materialCursor.getColumnIndex(RecipeMaterialPurchaseList._ID));
				boolean isBought = materialCursor.getInt(
						materialCursor.getColumnIndex(RecipeMaterialPurchaseList.IS_BOUGHT)) == 1;
				
				View material = createMaterial(name, remark, id, isBought);
				holder.materialGroup.addView(material);

				
			} while (materialCursor.moveToNext());
			materialCursor.close();
		}
		
		// Event Listener Setting
		View bgView = convertView.findViewById(R.id.recipe_title_bg);
		final View deleteButtonView = convertView.findViewById(R.id.delete_item_image);
		deleteButtonView.setVisibility(View.INVISIBLE);
		bgView.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				deleteButtonView.setVisibility(View.VISIBLE);
				return true;
			}
		});
		
		final String recipeId = holder.name.getTag().toString();
		final String recipeName = holder.name.getText().toString();
		bgView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RecipeFragment.startInActivity(mContext, recipeId, recipeName);
			}
		});
		
		deleteButtonView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PurchaseListModel.removeRecipeFromPurchaseList(mContext, recipeId);
			}
		});
		
		holder.delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PurchaseListModel.removeRecipeFromPurchaseList(mContext, recipeId);
			}
		});
		
		holder.buy.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString(BuyListFragment.PARAM_RECIPE_ID, recipeId);
				mContext.startActivity(FragmentHelper.getIntent(mContext, BaseActivity.class,
						BuyListFragment.class.getName(), BuyListFragment.class.getName(), bundle));
			}
		});
		
		return convertView;
	}
	
	
	private View createMaterial(String name, String remark, String id, boolean isBought) {
		
		View view = mInflater.inflate(R.layout.adapter_purchase_recipe_material_list_item, null);
		TextView nameView = (TextView) view.findViewById(R.id.name);
		nameView.setText(name);
		TextView remarkView = (TextView) view.findViewById(R.id.remark);
		remarkView.setText(remark);
		
		View materialItemView = view.findViewById(R.id.material_item);
		materialItemView.setOnClickListener(markMaterialListener);
		materialItemView.setTag(id);
		
		View lineSwipeImage = view.findViewById(R.id.line_swipe);
		lineSwipeImage.setVisibility(isBought ? View.VISIBLE : View.INVISIBLE);
		
		return view;
	}
	
	private OnClickListener markMaterialListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			View lineSwipeImage = v.findViewById(R.id.line_swipe);
			boolean isBought = lineSwipeImage.getVisibility() == View.VISIBLE;
			isBought = !isBought;
			if(PurchaseListModel.updateRecipeMaterialPurchased(mContext, v.getTag().toString(), isBought)) {
				lineSwipeImage.setVisibility(isBought?
						View.VISIBLE : View.INVISIBLE);
			}
		}
	};
	
	private class ViewHold {
		private TextView name;
		private LinearLayout materialGroup;
		private Button delete;
		private Button buy;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		
	}

}
