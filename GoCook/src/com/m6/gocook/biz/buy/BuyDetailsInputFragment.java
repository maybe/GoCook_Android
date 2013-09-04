package com.m6.gocook.biz.buy;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
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

import com.m6.gocook.R;
import com.m6.gocook.base.entity.response.CWareItem;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.main.MainActivityHelper;

public class BuyDetailsInputFragment extends BaseFragment {

	private CWareItem mWareItem;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle args = getArguments();
		if (args != null) {
			mWareItem = (CWareItem) args.getSerializable(BuyDetailsFragment.PARAM_RESULT);
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
		
		List<String> methods = mWareItem.getDealMethod();
		if (methods != null) {
			RadioGroup methodView = (RadioGroup) view.findViewById(R.id.method);
			LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
					RadioGroup.LayoutParams.WRAP_CONTENT,
					RadioGroup.LayoutParams.WRAP_CONTENT);
			for (int i = 0; i < methods.size(); i++) {
				RadioButton newRadioButton = new RadioButton(getActivity());
				newRadioButton.setText(methods.get(i));
				newRadioButton.setId(newRadioButton.getId());
				methodView.addView(newRadioButton, 0, layoutParams);
			}
		}
		
		view.findViewById(R.id.submit).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText countView = (EditText) getView().findViewById(R.id.count);
				int count = 0;
				if (countView.getText() != null) {
					count = Integer.valueOf(countView.getText().toString());
				}
				
				RadioGroup methodView = (RadioGroup) getView().findViewById(R.id.method);
				String method = ((Button) getView().findViewById(methodView.getCheckedRadioButtonId())).getText().toString();
				
				Intent intent = new Intent();
				intent.putExtra(BuyDetailsFragment.PARAM_RESULT_COUNT, count);
				intent.putExtra(BuyDetailsFragment.PARAM_RESULT_METHOD, method);
				getActivity().setResult(MainActivityHelper.RESULT_CODE_INPUT);
				
			}
		});
	}
	
	
}
