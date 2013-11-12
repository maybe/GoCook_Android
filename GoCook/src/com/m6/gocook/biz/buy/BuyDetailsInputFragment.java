package com.m6.gocook.biz.buy;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
		
		mIntegerNecessary = BuyModel.MATERIAL_UNIT.contains(mWareItem.getUnit());
		
		List<String> methods = mWareItem.getDealMethod();
		RadioGroup methodView = (RadioGroup) view.findViewById(R.id.method);
		LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
				RadioGroup.LayoutParams.WRAP_CONTENT,
				RadioGroup.LayoutParams.WRAP_CONTENT);
		if (methods != null && !methods.isEmpty()) {
			for (int i = 0; i < methods.size(); i++) {
				RadioButton newRadioButton = new RadioButton(getActivity());
				newRadioButton.setText(methods.get(i));
				newRadioButton.setId(newRadioButton.getId());
				methodView.addView(newRadioButton, layoutParams);
			}
		} else {
			RadioButton newRadioButton = new RadioButton(getActivity());
			newRadioButton.setText(getString(R.string.biz_buy_details_input_method_none));
			newRadioButton.setTextColor(getResources().getColor(android.R.color.black));
			newRadioButton.setId(0);
			methodView.addView(newRadioButton, layoutParams);
			methodView.check(0);
		}
		
		final EditText countView = (EditText) getView().findViewById(R.id.count);
		
		view.findViewById(R.id.submit).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
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
				
				RadioGroup methodView = (RadioGroup) getView().findViewById(R.id.method);
				Button selectedButton = (Button) getView().findViewById(methodView.getCheckedRadioButtonId());
				String method = null;
				if (selectedButton != null) {
					method = selectedButton.getText().toString();
				}
				
				List<String> methods = new ArrayList<String>();
				methods.add(method);
				mWareItem.setDealMethod(methods);
				//  TODO  remark
				mWareItem.setRemark(method);
				
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
	
	
}
