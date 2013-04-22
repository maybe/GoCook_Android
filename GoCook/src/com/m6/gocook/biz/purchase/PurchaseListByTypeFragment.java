package com.m6.gocook.biz.purchase;

import com.m6.gocook.R;
import com.m6.gocook.base.db.table.RecipeMaterialPurchaseList;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class PurchaseListByTypeFragment extends Fragment {
	
	private Context mContext;
	private Cursor mMainMaterialCursor;
	private Cursor mNMainMaterialCursor;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mContext = this.getActivity();

		View view = inflater.inflate(R.layout.fragment_purchase_list_by_type, container, false);
		
		mMainMaterialCursor = PurchaseListModel.getRecipeMaterialPurchaseListCursorByType(mContext, true);
		mNMainMaterialCursor = PurchaseListModel.getRecipeMaterialPurchaseListCursorByType(mContext, false);
		
		ListView mainMaterialListView = (ListView) view.findViewById(R.id.purchase_main_material_listview);
		ListView nmainMaterialListView = (ListView) view.findViewById(R.id.purchase_nmain_material_listview);
		
		String[] from = {RecipeMaterialPurchaseList.MATERIAL_NAME,
				RecipeMaterialPurchaseList.MATERIAL_REMARK};
		int[] to = {R.id.name,
				R.id.remark};
		
		mainMaterialListView.setAdapter(new PurchaseMaterialListAdapter(mContext,
				R.layout.adapter_purchase_recipe_material_list_item,
				mMainMaterialCursor, from, to,
				CursorAdapter.FLAG_AUTO_REQUERY));
		
		nmainMaterialListView.setAdapter(new PurchaseMaterialListAdapter(mContext,
				R.layout.adapter_purchase_recipe_material_list_item,
				mNMainMaterialCursor, from, to,
				CursorAdapter.FLAG_AUTO_REQUERY));
		
		int recipePurchasedCount = PurchaseListModel.getRecipePurchaseCount(mContext);
		
		TextView mainMaterialTitleTextView = ((TextView)view.findViewById(R.id.main_material_title));
		mainMaterialTitleTextView.setText(
		String.format(mContext.getResources().getString(R.string.biz_recipe_purchase_main_material),
				String.valueOf(recipePurchasedCount)));
		mainMaterialTitleTextView.setTypeface(Typeface.MONOSPACE,Typeface.ITALIC);
		
		TextView nMainMaterialTitleTextView = ((TextView)view.findViewById(R.id.nmain_material_title));
		nMainMaterialTitleTextView.setText(
				String.format(mContext.getResources().getString(R.string.biz_recipe_purchase_nmain_material),
						String.valueOf(recipePurchasedCount)));
		nMainMaterialTitleTextView.setTypeface(Typeface.MONOSPACE,Typeface.ITALIC);
		
		return view;

	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
	}
	
	
	@Override
	public void onDestroy() {
		
		super.onDestroy();
		
		if(mMainMaterialCursor != null) {
			mMainMaterialCursor.close();
		}
		
		if(mNMainMaterialCursor != null) {
			mNMainMaterialCursor.close();
		}
	}

}
