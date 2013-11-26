package com.m6.gocook.biz.buy;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.m6.gocook.R;
import com.m6.gocook.base.entity.response.CWareItem;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.main.MainActivityHelper;

public class BuyDetailsInputFragment extends BaseFragment {

	private CWareItem mWareItem;
	private String mRecordId;
	
	private boolean mIntegerNecessary = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle args = getArguments();
		if (args != null) {
			mWareItem = (CWareItem) args.getSerializable(BuyDetailsFragment.PARAM_RESULT);
			mRecordId = args.getString(BuySearchFragment.PARAM_RECORD_ID);
		}
	}
	
	public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_buy_details_input, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(R.string.biz_buy_details_input_title);
		
		((TextView) view.findViewById(R.id.name)).setText(mWareItem.getName());
		((TextView) view.findViewById(R.id.price)).setText(getString(R.string.biz_buy_search_adapter_price, String.valueOf(mWareItem.getPrice())));
		((TextView) view.findViewById(R.id.unit)).setText(getString(R.string.biz_buy_search_adapter_unit, mWareItem.getUnit()));
		((TextView) view.findViewById(R.id.norm)).setText(getString(R.string.biz_buy_search_adapter_norm, mWareItem.getNorm()));
		((TextView) view.findViewById(R.id.count_suffix)).setText("/" + mWareItem.getUnit());
		
		mIntegerNecessary = !BuyModel.MATERIAL_UNIT.contains(mWareItem.getUnit());
		
		final ArrayList<String> methods = mWareItem.getDealMethod();
		TextView methodView = (TextView) view.findViewById(R.id.method);
		if (methods != null && !methods.isEmpty()) {
			methodView.setText(methods.get(0));
		} else {
			methodView.setText(getString(R.string.biz_buy_details_input_method_none));
		}
		
		methodView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivityForResult(BuyMethodFragment.getIntent(getActivity(), methods), 
						MainActivityHelper.REQUEST_CODE_BUY_METHOD);
			}
		});
		
		final EditText countView = (EditText) getView().findViewById(R.id.count);
		
		view.findViewById(R.id.submit).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 购买数量
				if (TextUtils.isEmpty(countView.getText())) {
					Toast.makeText(getActivity(), R.string.biz_buy_details_input_count_empty, Toast.LENGTH_SHORT).show();
					return;
				}
				
				double count;
				try {
					count = Double.valueOf(countView.getText().toString());
					if (mIntegerNecessary) {
						int realCount = (int) Math.floor(count);
						countView.setText(String.valueOf(realCount));
						mWareItem.setQuantity(realCount);
					} else {
						mWareItem.setQuantity(count);
					}
				} catch (NumberFormatException e) {
					Toast.makeText(getActivity(), R.string.biz_buy_details_input_count_illegal, Toast.LENGTH_SHORT).show();
					return;
				}
				
				// 加工方式
				TextView methodView = (TextView) getView().findViewById(R.id.method);
				String method = methodView.getText().toString();;
				//  TODO  remark
				mWareItem.setRemark(method);
				
				ArrayList<String> methods = new ArrayList<String>();
				methods.add(method);
				mWareItem.setDealMethod(methods);
				
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable(BuyDetailsFragment.PARAM_RESULT_DATA, mWareItem);
				intent.putExtras(bundle);
				intent.putExtra(BuySearchFragment.PARAM_RECORD_ID, mRecordId);
				getActivity().setResult(MainActivityHelper.RESULT_CODE_INPUT, intent);
				getActivity().finish();
			}
		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MainActivityHelper.REQUEST_CODE_BUY_METHOD
				&& resultCode == MainActivityHelper.RESULT_CODE_BUY_METHOD && data != null) {
			
			String method = data.getStringExtra(BuyMethodFragment.PARAM_RESULT);
			if (!TextUtils.isEmpty(method)) {
				TextView methodView = (TextView) getView().findViewById(R.id.method);
				methodView.setText(method);
			}
		}
	}
	
}
