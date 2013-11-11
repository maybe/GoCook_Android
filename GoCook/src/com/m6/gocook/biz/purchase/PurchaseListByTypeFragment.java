package com.m6.gocook.biz.purchase;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.m6.gocook.R;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.db.table.RecipeMaterialPurchaseList;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.biz.buy.BuyListFragment;

public class PurchaseListByTypeFragment extends Fragment {
	
	private Context mContext;
	private Cursor mMainMaterialCursor;
	private TextView mMainMaterialTitleTextView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mContext = this.getActivity();

		View view = inflater.inflate(R.layout.fragment_purchase_list_by_type, container, false);
		
		mMainMaterialTitleTextView = ((TextView) view.findViewById(R.id.main_material_title));
		updateRecipeCount();
		
		mMainMaterialCursor = PurchaseListModel.getRecipeMaterialPurchaseListCursorByType(mContext, true);
		
		ListView mainMaterialListView = (ListView) view.findViewById(R.id.purchase_main_material_listview);
		
		String[] from = {RecipeMaterialPurchaseList.MATERIAL_NAME,
				RecipeMaterialPurchaseList.MATERIAL_REMARK};
		int[] to = {R.id.name,
				R.id.remark};
		
		mainMaterialListView.setAdapter(new PurchaseMaterialListAdapter(mContext,
				R.layout.adapter_purchase_recipe_material_list_item,
				mMainMaterialCursor, from, to,
				CursorAdapter.FLAG_AUTO_REQUERY));
		
		view.findViewById(R.id.buy).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				mContext.startActivity(FragmentHelper.getIntent(mContext, BaseActivity.class,
						BuyListFragment.class.getName(), BuyListFragment.class.getName(), bundle));
			}
		});
		return view;
	}
	
	private void updateRecipeCount() {
		int recipePurchasedCount = PurchaseListModel.getRecipePurchaseCount(mContext);
		mMainMaterialTitleTextView.setText(
		String.format(mContext.getResources().getString(R.string.biz_recipe_purchase_material),
				String.valueOf(recipePurchasedCount)));
		mMainMaterialTitleTextView.setTypeface(Typeface.MONOSPACE,Typeface.ITALIC);
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		updateRecipeCount();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mMainMaterialTitleTextView = null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if(mMainMaterialCursor != null) {
			mMainMaterialCursor.close();
		}
	}

}
