package com.m6.gocook.biz.buy;

import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.m6.gocook.R;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.fragment.OnActivityAction;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.main.MainActivityHelper;

public class BuyListFragment extends BaseFragment implements OnActivityAction {

	public static final String PARAM_RECIPE_ID = "param_recipe_id";
	
	private BuyListAdapter mAdapter;
	private String mRecipeId;
	private List<Map<String, Object>> mData;
	
	
	@Override
	public View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_buylist, container, false);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle args = getArguments();
		if (args != null) {
			mRecipeId = args.getString(PARAM_RECIPE_ID);
		}
		
		mData = BuyModel.getBuyList(getActivity(), mRecipeId);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(R.string.biz_buy_title);
		
		mAdapter = new BuyListAdapter(getActivity(), mData);
		((ListView) getView().findViewById(R.id.list)).setAdapter(mAdapter);
	}
	
	private void saveData(int wareId, int count, String method) {
		
	}

	@Override
	public void onCustomActivityResult(int requestCode, int resultCode,
			Intent data) {
		if (requestCode == MainActivityHelper.REQUEST_CODE_INPUT && resultCode == MainActivityHelper.RESULT_CODE_INPUT) {
			if (data != null) {
				int wareId = data.getIntExtra(BuyDetailsFragment.PARAM_RESULT_ID, -1);
				int count = data.getIntExtra(BuyDetailsFragment.PARAM_RESULT_COUNT, 0);
				String method = data.getStringExtra(BuyDetailsFragment.PARAM_RESULT_METHOD);
				
				saveData(wareId, count, method);
			}
		}
	}

}
