package com.m6.gocook.biz.buy;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.m6.gocook.R;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.main.MainActivityHelper;

public class BuyMethodFragment extends BaseFragment {

	private static final String PARAM_METHODS = "param_methods";
	public static final String PARAM_RESULT = "param_result";
	
	private List<String> mMethods = new ArrayList<String>();
	
	
	public static Intent getIntent(Context context, ArrayList<String> methods) {
		Bundle bundle = new Bundle();
		bundle.putStringArrayList(PARAM_METHODS, methods);
		return FragmentHelper.getIntent(context, BaseActivity.class, 
				BuyMethodFragment.class.getName(), BuyMethodFragment.class.getName(), bundle);
	}
	
	private void setResult(String method) {
		Intent intent = new Intent();
		intent.putExtra(PARAM_RESULT, method);
		getActivity().setResult(MainActivityHelper.RESULT_CODE_BUY_METHOD, intent);
		getActivity().finish();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mMethods = getArguments() == null ? null : getArguments().getStringArrayList(PARAM_METHODS);
	}
	
	@Override
	public View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_buy_method, container, false);
		if (mMethods != null && !mMethods.isEmpty()) {
			int size = mMethods.size();
			for (int i = 0; i < size; i++) {
				View item = inflater.inflate(R.layout.fragment_buy_method_item, container, false);
				final String method = mMethods.get(i);
				((TextView) item.findViewById(R.id.title)).setText(getString(R.string.biz_buy_details_input_method_item_title, i + 1));
				((TextView) item.findViewById(R.id.method)).setText(method);
				
				item.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						setResult(method);
					}
				});
				
				view.addView(item);
			}
		} else {
			View item = inflater.inflate(R.layout.fragment_buy_method_item, container, false);
			((TextView) item.findViewById(R.id.title)).setText(getString(R.string.biz_buy_details_input_method_prefix));
			((TextView) item.findViewById(R.id.method)).setText(getString(R.string.biz_buy_details_input_method_none));
			
			item.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					setResult(getString(R.string.biz_buy_details_input_method_none));
				}
			});
			
			view.addView(item);
		}
		
		View customMethodView = inflater.inflate(R.layout.fragment_buy_method_item_custom, container, false);
		final EditText customEditText = (EditText) customMethodView.findViewById(R.id.method);
		Button submit = (Button) customMethodView.findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setResult(customEditText.getText().toString());
			}
		});
		view.addView(customMethodView);
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(R.string.biz_buy_details_input_method_title);
		
	}
}
