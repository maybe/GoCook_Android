package com.m6.gocook.biz.purchase;

import com.m6.gocook.R;
import com.m6.gocook.base.db.table.RecipeMaterialPurchaseList;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.View.OnClickListener;

public class PurchaseMaterialListAdapter extends SimpleCursorAdapter {

	public PurchaseMaterialListAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		super.bindView(view, context, cursor);

		boolean isBought = cursor.getInt(
				cursor.getColumnIndex(RecipeMaterialPurchaseList.IS_BOUGHT)) == 1;
		View lineSwipeImage = view.findViewById(R.id.line_swipe);
		lineSwipeImage.setVisibility(isBought ? View.VISIBLE : View.INVISIBLE);
		
		String id = cursor.getString(
				cursor.getColumnIndex(RecipeMaterialPurchaseList._ID));
		View materialItemView = view.findViewById(R.id.material_item);
		materialItemView.setOnClickListener(markMaterialListener);
		materialItemView.setTag(id);
	}

	private OnClickListener markMaterialListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			View lineSwipeImage = v.findViewById(R.id.line_swipe);
			boolean isBought = lineSwipeImage.getVisibility() == View.VISIBLE;
			isBought = !isBought;
			if (PurchaseListModel.updateRecipeMaterialPurchased(mContext, v
					.getTag().toString(), isBought)) {
				lineSwipeImage.setVisibility(isBought ? View.VISIBLE
						: View.INVISIBLE);
			}
		}
	};

}
