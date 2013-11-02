package com.m6.gocook.biz.buy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.m6.gocook.R;
import com.m6.gocook.base.db.table.RecipeMaterialPurchaseList;
import com.m6.gocook.base.entity.request.CShopcartInfo;
import com.m6.gocook.base.entity.request.CShopcartInfo.CShopcartWareInfo;
import com.m6.gocook.base.entity.response.CShopCartResult;
import com.m6.gocook.base.entity.response.CWareItem;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.fragment.OnActivityAction;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.main.MainActivityHelper;
import com.m6.gocook.util.model.ModelUtils;

public class BuyListFragment extends BaseFragment implements OnActivityAction {

	public static final String PARAM_RECIPE_ID = "param_recipe_id";
	
	private BuyListAdapter mAdapter;
	private String mRecipeId;
	private List<Map<String, Object>> mData;
	
	private OrderTask mTask;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivityHelper.registerOnActivityActionListener(this);
		
		Bundle args = getArguments();
		if (args != null) {
			mRecipeId = args.getString(PARAM_RECIPE_ID);
		}
		
		mData = BuyModel.getBuyList(getActivity(), mRecipeId);
	}
	
	@Override
	public View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_buylist, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(R.string.biz_buy_title);
		actionBar.setRightButton(R.string.biz_buy_list_actionbar_right, R.drawable.actionbar_btn_selector);
		
		mAdapter = new BuyListAdapter(getActivity(), mData);
		((ListView) getView().findViewById(R.id.list)).setAdapter(mAdapter);
	}
	
	private void saveData(String recordId, CWareItem wareItem) {
		if (mData == null || mData.isEmpty()) {
			return;
		}
		
		final List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		data.addAll(mData);
		for (Map<String, Object> map : data) {
			String recId = ModelUtils.getStringValue(map, RecipeMaterialPurchaseList._ID);
			if (!TextUtils.isEmpty(recordId) && recordId.equals(recId)) {
				map.put(BuyModel.NAME, wareItem.getName());
				map.put(BuyModel.UNIT, wareItem.getUnit());
				map.put(BuyModel.NORM, wareItem.getNorm());
				map.put(BuyModel.QUANTITY, wareItem.getQuantity());
				map.put(BuyModel.PRICE, wareItem.getPrice());
				map.put(BuyModel.METHOD, wareItem.getDealMethod().get(0));
				map.put(BuyModel.REMARK, wareItem.getRemark());
				map.put(BuyModel.WAREID, wareItem.getId());
			}
		}
		
		if (mAdapter != null) {
			mData.clear();
			mData.addAll(data);
			mAdapter.notifyDataSetChanged();
		}
	}

    @Override
	public void onDestroy() {
		super.onDestroy();
		MainActivityHelper.unRegisterOnActivityActionListener(this);
	}
	
	@Override
	public void onActionBarRightButtonClick(View v) {
		if (mTask == null) {
			showProgress(true);
			setProgressMessage(R.string.biz_buy_list_progress_message);
			
			mTask = new OrderTask(getActivity(), mData);
			mTask.execute((Void) null); 
		}
	}

	@Override
	public void onCustomActivityResult(int requestCode, int resultCode,
			Intent data) {
		if (resultCode == MainActivityHelper.RESULT_CODE_INPUT) {
			if (data != null) {
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					String recordId = data.getStringExtra(BuySearchFragment.PARAM_RECORD_ID);
					CWareItem wareItem = (CWareItem) bundle.getSerializable(BuyDetailsFragment.PARAM_RESULT_DATA);
					saveData(recordId, wareItem);
				}
				
			}
		}
	}
	
	public class OrderTask extends AsyncTask<Void, Void, CShopCartResult> {

		private Context mContext;
		private List<Map<String, Object>> mItems;
		
		public OrderTask(Context context, List<Map<String, Object>> items) {
			mContext = context.getApplicationContext();
			mItems = items;
		}
		
		@Override
		protected CShopCartResult doInBackground(Void... params) {
			if (mItems != null && !mItems.isEmpty()) {
				List<CShopcartWareInfo> cart = new ArrayList<CShopcartWareInfo>();
				for (Map<String, Object> item : mItems) {
					int wareId = ModelUtils.getIntValue(item, BuyModel.WAREID, 0);
					double quantity = ModelUtils.getDoubleValue(item, BuyModel.QUANTITY, 0.00d);
					String remark = ModelUtils.getStringValue(item, BuyModel.REMARK);
					
					if (quantity != 0 && wareId != 0) {
						CShopcartWareInfo shopcartWareInfo = new CShopcartWareInfo(wareId, quantity, remark);
						cart.add(shopcartWareInfo);
					}
				}
				
				CShopCartResult resutl = BuyModel.orderRequest(mContext, new CShopcartInfo(cart));
				return resutl;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(CShopCartResult result) {
			mTask = null;
			
			if (isAdded()) {
				showProgress(false);
				if (result != null && result.getResult() == Protocol.VALUE_RESULT_OK
						&& !TextUtils.isEmpty(result.getOrderId())) {
					Toast.makeText(getActivity(), R.string.biz_buy_list_order_success, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity(), R.string.biz_buy_list_order_failure, Toast.LENGTH_SHORT).show();
				}
			}
		}
		
		@Override
		protected void onCancelled() {
			mTask = null;
			
			if (isAdded()) {
				showProgress(false);
			}
		}
	}

}
